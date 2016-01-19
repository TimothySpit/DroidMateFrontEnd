package com.droidmate.apk;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

public class APKInformation {

	private final File file;
	private APKExplorationStatus status = APKExplorationStatus.NOT_RUNNING;
	private String packageName, versionCode, versionName;
	private boolean inlined = false;

	private ExplorationReport report;
	
	private boolean selected = false;

	public APKInformation(int id, File file, String packageName, String versionCode, String versionName) {
		super();
		this.file = file;
		this.packageName = packageName;
		this.versionCode = versionCode;
		this.versionName = versionName;
		
		this.setReport(ExplorationReport.getDefaultReport());
	}
	
	public Path getInlinedPath() {
		return Paths.get(getFile().getParent().toString(), "/inlined",
				FilenameUtils.removeExtension(getFile().getName()) + "-inlined.apk");
	}

	public boolean isInlined() {
		inlined = getInlinedPath().toFile().exists();
		 
		 return inlined;
	}

	public APKExplorationStatus getStatus() {
		return status;
	}

	public File getFile() {
		return file;
	}

	public APKExplorationStatus getExplorationStatus() {
		return status;
	}

	public void setExplorationStatus(APKExplorationStatus status) {
		this.status = status;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public JSONObject toJSONObject() {
		JSONObject object = new JSONObject();
		object.put("name", getFile().getName());
		object.put("size", FileUtils.byteCountToDisplaySize(getFile().length()));
		object.put("package", getPackageName());
		object.put("version", getVersionName() + " (#" + getVersionCode() + ")");
		object.put("selected", selected);
		object.put("inlined", isInlined());
		
		return object;
	}

	public synchronized ExplorationReport getReport() {
		return report;
	}

	public synchronized void setReport(ExplorationReport report) {
		this.report = report;
	}

}
