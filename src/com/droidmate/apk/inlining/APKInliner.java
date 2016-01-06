package com.droidmate.apk.inlining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;

import com.droidmate.apk.APKExplorationStatus;
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
		inliningStatus =  InliningStatus.INLINING;
		GUISettings settings = new GUISettings();
		Path droidMateRoot = settings.getDroidMatePath();
		Path droidMateExecutable = Paths.get(droidMateRoot.toString(), "/gradlew.bat");
		Path inputAPKsPath = Paths.get(droidMateRoot.toString(), "/projects/apk-inliner/input-apks/");

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
				Files.copy(apkInfo.getFile().toPath(), Paths.get(inputAPKsPath.toString(),apkInfo.getFile().getName()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
				inliningStatus = InliningStatus.ERROR;
				return;
			}
			if (!startDroidMateInliner(droidMateRoot, droidMateExecutable)) {
				inliningStatus = InliningStatus.ERROR;
				return;
			}
			apkInfo.setStatus(APKExplorationStatus.INLINING);
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
