package com.droidmate.apk;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

public class APKInformation {

	private final File inlineTempFile;

	private APKStatus status = APKStatus.UNKNOWN;
	private APKInliningStatus inliningStatus = APKInliningStatus.UNKNOWN;

	AAPTInformation aaptInfo;

	private boolean selected = false;

	private ExplorationInformation explorationInfo = new ExplorationInformation();

	public APKInformation(AAPTInformation aaptInfo, Path tempInlinePath) {
		super();

		this.aaptInfo = aaptInfo;
		inlineTempFile = tempInlinePath.resolve(FilenameUtils.removeExtension(aaptInfo.getFile().getName()) + "-inlined.apk").toFile();
	}

	public Path getInlinedPath() {
		return Paths.get(getFile().getParent().toString(), "/inlined", FilenameUtils.removeExtension(getFile().getName()) + "-inlined.apk");
	}

	/**
	 * Determines the inline state based on the droidmate inlining output folder
	 * 
	 * @return True, if the inlined file in the droidmate inlining output folder
	 *         exists
	 */
	public boolean isTempInlined() {
		return inlineTempFile.exists();
	}

	/**
	 * Determines the inline state based on the user chosen apk folder
	 * 
	 * @return True, if the inlined file in the user chosen apk folder exists
	 */
	public boolean isInlined() {
		return getInlinedPath().toFile().exists();
	}

	public JSONObject toJSONObject() {
		JSONObject object = new JSONObject();
		object.put("status", status.getName());
		object.put("inliningStatus", inliningStatus.getName());
		object.put("name", getFile().getName());
		object.put("size", getFile().length());
		object.put("sizeReadable", FileUtils.byteCountToDisplaySize(getFile().length()));
		object.put("package", getPackageName());
		object.put("version", getVersionName() + " (#" + getVersionCode() + ")");
		object.put("activityName", getActivityName());
		object.put("selected", selected);

		return object;
	}

	public File getFile() {
		return aaptInfo.getFile();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getPackageName() {
		return aaptInfo.getPackageName();
	}

	public String getVersionCode() {
		return aaptInfo.getPackageVersionCode();
	}

	public String getActivityName() {
		return aaptInfo.getActivityName();
	}
	
	public String getVersionName() {
		return aaptInfo.getPackageVersionName();
	}

	public File getInlineTempFile() {
		return inlineTempFile;
	}

	// Status
	public APKStatus getStatus() {
		return status;
	}

	public void setStatus(APKStatus status) {
		this.status = status;
	}

	public APKInliningStatus getInliningStatus() {
		return inliningStatus;
	}

	public void setInliningStatus(APKInliningStatus inliningStatus) {
		this.inliningStatus = inliningStatus;
	}

	public ExplorationInformation getExplorationInfo() {
		return explorationInfo;
	}

	public void setExplorationInfo(ExplorationInformation explorationInfo) {
		this.explorationInfo = explorationInfo;
	}
}
