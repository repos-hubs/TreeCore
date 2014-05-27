package com.treecore.crash.collector;

import android.content.Context;
import com.treecore.utils.stl.BoundedLinkedList;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

final class LogFileCollector {
	public static String collectLogFile(Context context, String fileName,
			int numberOfLines) throws IOException {
		BoundedLinkedList resultBuffer = new BoundedLinkedList(numberOfLines);
		BufferedReader reader;
		if (fileName.contains("/"))
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)), 1024);
		else {
			reader = new BufferedReader(new InputStreamReader(
					context.openFileInput(fileName)), 1024);
		}
		String line = reader.readLine();
		while (line != null) {
			resultBuffer.add(line + "\n");
			line = reader.readLine();
		}
		return resultBuffer.toString();
	}
}