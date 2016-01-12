package com.droidmate.apk.inlining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.droidmate.apk.APKInformation;
import com.droidmate.settings.GUISettings;
import com.droidmate.user.DroidMateUser;

public class APKInliner implements Runnable {

	private InliningStatus inliningStatus = InliningStatus.NOT_STARTED;

	private final DroidMateUser user;

	public APKInliner(DroidMateUser user) {
		if (user == null) {
			throw new NullPointerException();
		}

		this.user = user;
	}

	@Override
	public void run() {
		inline();
	}

	private void inline() {
		inliningStatus = InliningStatus.INLINING;
		GUISettings settings = new GUISettings();
		Path droidMateRoot = settings.getDroidMatePath();
		String gradlewName = "/gradlew.bat";
		if (System.getProperty("os.name").equals("Linux")) {
			gradlewName = "/gradlew";
		}
		Path droidMateExecutable = Paths.get(droidMateRoot.toString(), gradlewName);
		Path inputAPKsPath = Paths.get(droidMateRoot.toString(), "/projects/apk-inliner/input-apks/");

		Path apkPath = user.getAPKPath();

		// check for already inlined files
		for (APKInformation apk : user.getAPKS()) {
			if (apk.getFile().toString().contains("-inlined")) {
				inliningStatus = InliningStatus.ERROR;
				return;
			}
		}

		// check for "inlined" folder
		Path inlinedFolder = Paths.get(apkPath.toString(), "/inlined");
		if (inlinedFolder.toFile().exists()) {
			int counter = 0;
			// check if there are all files inlined
			for (APKInformation apkInfo : user.getAPKS()) {
				Path inlinedAPK = Paths.get(apkInfo.getFile().getParent().toString(), "/inlined",
						FilenameUtils.removeExtension(apkInfo.getFile().getName()) + "-inlined.apk");
				if (!inlinedAPK.toFile().exists()) {
					// not all files exist, delete directory and inline again
					try {
						FileUtils.deleteDirectory(inlinedFolder.toFile());
						break;
					} catch (IOException e) {
						e.printStackTrace();
						inliningStatus = InliningStatus.ERROR;
						return;
					}
				} else {
					counter++;
				}
			}

			if (counter == user.getAPKS().length) {
				// all files are already inlined
				inliningStatus = InliningStatus.FINISHED;
				return;
			}
		}

		// create "inlined" folder
		inlinedFolder.toFile().mkdir();

		// empty inlining directory
		try {
			FileUtils.deleteDirectory(inputAPKsPath.toFile());
			inputAPKsPath.toFile().mkdir();
		} catch (IOException e) {
			e.printStackTrace();
			inliningStatus = InliningStatus.ERROR;
			return;
		}

		// for each apk, inline it
		for (APKInformation apkInfo : user.getAPKS()) {
			try {
				Files.copy(apkInfo.getFile().toPath(), Paths.get(inputAPKsPath.toString(), apkInfo.getFile().getName()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
				inliningStatus = InliningStatus.ERROR;
				return;
			}

			// apkInfo.setStatus(InliningStatus.INLINING);
		}
		if (!startDroidMateInliner(droidMateRoot, droidMateExecutable)) {
			inliningStatus = InliningStatus.ERROR;
			return;
		}

		// copy inlined apks to "inlined" folder
		Path outputAPKsPath = Paths.get(droidMateRoot.toString(), "/projects/apk-inliner/output-apks/");
		for (APKInformation apkInfo : user.getAPKS()) {
			try {
				Path src = Paths.get(outputAPKsPath.toString(), FilenameUtils.removeExtension(apkInfo.getFile().getName()) + "-inlined.apk");
				Path dst = Paths.get(inlinedFolder.toString(), FilenameUtils.removeExtension(apkInfo.getFile().getName()) + "-inlined.apk");
				Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
				inliningStatus = InliningStatus.ERROR;
				return;
			}
		}
		inliningStatus = InliningStatus.FINISHED;
	}

	private boolean startDroidMateInliner(Path droidMateRoot, Path droidMateExecutable) {
		ProcessBuilder pb = new ProcessBuilder(droidMateExecutable.toString(), "--stacktrace", ":projects:core:prepareInlinedApks");
		pb.directory(droidMateRoot.toFile());
		pb.redirectErrorStream(true);
		try {
			Process p = pb.start();
			String s;
			BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = stdout.readLine()) != null) {
				System.out.println(s);
			}
			System.out.println("Exit value: " + p.waitFor());
			p.getInputStream().close();
			p.getOutputStream().close();
			p.getErrorStream().close();

			if (p.exitValue() != 0) {
				return false;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public synchronized InliningStatus getInliningStatus() {
		return inliningStatus;
	}

}
