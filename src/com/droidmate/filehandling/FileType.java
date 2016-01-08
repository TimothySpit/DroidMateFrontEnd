package com.droidmate.filehandling;

public enum FileType {
	UNKNOWN("unknowm"), DIRECTORY("directory"), ALL("all");

	private final String name;

	private FileType(String s) {
		name = s;
	}

	public static FileType fromString(String s) {
		for (FileType ft : FileType.values()) {
			if (ft.equalsName(s)) {
				return ft;
			}
		}
		return FileType.UNKNOWN;
	}
	
	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return this.name;
	}
}
