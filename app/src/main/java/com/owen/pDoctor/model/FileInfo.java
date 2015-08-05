package com.owen.pDoctor.model;

/** 文件 **/
public class FileInfo {
	public String Name;
	public String Path;
	public long Size;
	public boolean IsDirectory = false;
	public int FileCount = 0;
	public int FolderCount = 0;
	public int scheduleId;
	public String realyName;

	public String getRealyName() {
		return realyName;
	}

	public void setRealyName(String realyName) {
		this.realyName = realyName;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPath() {
		return Path;
	}

	public void setPath(String path) {
		Path = path;
	}

	public long getSize() {
		return Size;
	}

	public void setSize(long size) {
		Size = size;
	}

	public boolean isIsDirectory() {
		return IsDirectory;
	}

	public void setIsDirectory(boolean isDirectory) {
		IsDirectory = isDirectory;
	}

	public int getFileCount() {
		return FileCount;
	}

	public void setFileCount(int fileCount) {
		FileCount = fileCount;
	}

	public int getFolderCount() {
		return FolderCount;
	}

	public void setFolderCount(int folderCount) {
		FolderCount = folderCount;
	}

	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}
}