package com.treecore.filepath;

public abstract interface IXFilePath {
	public abstract String getAudioPath();

	public abstract String getVideoPath();

	public abstract String getImagePath();

	public abstract String getDownloadPath();

	public abstract String getCachePath();

	public abstract String getAppPath();
}