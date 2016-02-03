package com.droidmate.processes;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.droidmate.interfaces.Observable;

public class DirectoryWatcher extends Observable<DirectoryWatcherEvent> {

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private final boolean recursive;

	private AtomicBoolean isWatchingEvents = new AtomicBoolean(false);
	private AtomicBoolean shouldClose = new AtomicBoolean(false);

	private Thread readinThread = null;

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	public DirectoryWatcher(Path dirToWatch, boolean recursive) throws IOException {
		if (dirToWatch == null) {
			throw new IllegalArgumentException("Directory to be watched must not be null.");
		}
		if (!dirToWatch.toFile().exists()) {
			throw new FileNotFoundException("Directory " + dirToWatch + " does not exist.");
		}
		if (!dirToWatch.toFile().isDirectory()) {
			throw new IllegalArgumentException("Path " + dirToWatch + " is no directory.");
		}

		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.recursive = recursive;

		if (recursive) {
			registerAll(dirToWatch);
		} else {
			register(dirToWatch);
		}

	}

	private void registerAll(final Path startDirectory) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(startDirectory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		keys.put(key, dir);
	}

	void stop() {
		isWatchingEvents.set(false);
	}

	void joinIfNoDataAvailable() throws InterruptedException {
		if (readinThread == null)
			return;

		shouldClose.set(true);

		readinThread.join();
	}

	void processEvents() throws FileNotFoundException, InterruptedException {
		if (isWatchingEvents.get()) {
			throw new IllegalStateException("Directory watcher is already started.");
		}

		// concurrent reading
		this.readinThread = (new Thread() {
			@Override
			public void run() {
				isWatchingEvents.set(true);
				startReadingEvents();
			}
		});
		readinThread.start();
	}

	private void startReadingEvents() {
		while (isWatchingEvents.get()) {

			// wait for key to be signaled
			WatchKey key;
			key = watcher.poll();

			if (key == null) {
				// if should exit, exit because no more data information is
				// needed
				if (shouldClose.get()) {
					if ((key = watcher.poll()) == null) {
						return;
					}
				} else {
					continue;
				}
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// nottify children
				notifyObservers(new DirectoryWatcherEvent(event, child));

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (recursive && (kind == ENTRY_CREATE)) {
					try {
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					} catch (IOException x) {
						// ignore to keep sample readbale
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}
}
