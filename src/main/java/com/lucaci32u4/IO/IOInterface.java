package com.lucaci32u4.IO;

import com.lucaci32u4.main.ApplicationConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class IOInterface {
	
	private AtomicReference<PrintStream> stderr = new AtomicReference<>(System.out);
	private AtomicBoolean initialised = new AtomicBoolean(false);
	private AtomicReference<String> workspace = new AtomicReference<>("");
	
	private static IOInterface ourInstance = new IOInterface();
	public static IOInterface getInstance() {
		return ourInstance;
	}
	
	public void init(PrintStream stderr, String workspace) {
		if (!initialised.get()) {
			initialised.set(true);
			this.stderr.set(stderr);
			this.workspace.set(workspace);
		}
	}
	
	public String loadResourceString(String resource) {
		String res = null;
		InputStream inputStream;
		try {
			inputStream = getClass().getResourceAsStream(resource);
			if (inputStream != null) {
				res = IOUtils.toString(inputStream, ApplicationConstants.getInstance().get("resource.encoding"));
			}
		} catch (IOException ioe) {
			ioe.printStackTrace(stderr.get());
		}
		return res;
	}
	
	public byte[] loadResourceBytes(String resource) {
		byte[] res = null;
		InputStream inputStream;
		try {
			inputStream = getClass().getResourceAsStream(resource);
			if (inputStream != null) {
				res = IOUtils.toByteArray(inputStream);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace(stderr.get());
		}
		return res;
	}
	
	public String setSystemWorkspaceDirectory(String dir) {
		String separator = ApplicationConstants.getInstance().get("file.separator");
		if (!dir.endsWith(separator)) dir += separator;
		return workspace.getAndSet(dir);
	}
	
	private String convertToSystemPath(String path) {
		String separator = ApplicationConstants.getInstance().get("file.separator");
		StringBuilder sb = new StringBuilder();
		String bits[] = path.split("\\\\");
		sb.append(workspace.get());
		for (int i = 0; i < bits.length; i++) {
			sb.append(bits[i]);
			if (i != bits.length - 1) sb.append(separator);
		}
		path = sb.toString();
		return path;
	}
	
	public String loadFile(boolean systemAbsolute, String path) {
		String res = null;
		try {
			if (!systemAbsolute) path = convertToSystemPath(path);
			res = FileUtils.readFileToString(new File(path), ApplicationConstants.getInstance().get("file.encoding"));
		} catch (IOException ioe) {
			ioe.printStackTrace(stderr.get());
		}
		return res;
	}
	
	public byte[] loadFileBytes(boolean systemAbsolute, String path) {
		byte[] res = null;
		try {
			if (!systemAbsolute) path = convertToSystemPath(path);
			res = FileUtils.readFileToByteArray(new File(path));
		} catch (IOException ioe) {
			ioe.printStackTrace(stderr.get());
		}
		return res;
	}
	
	public IOException storeFile(boolean systemAbsolute, String path, String content) {
		IOException res = null;
		try {
			if (!systemAbsolute) path = convertToSystemPath(path);
			FileUtils.writeStringToFile(new File(path), content, ApplicationConstants.getInstance().get("file.encoding"));
		} catch (IOException e) {
			res = e;
		}
		return res;
	}
	
	public IOException storeFile(boolean systemAbsolute, String path, byte[] content) {
		IOException res = null;
		try {
			if (!systemAbsolute) path = convertToSystemPath(path);
			FileUtils.writeByteArrayToFile(new File(path), content);
		} catch (IOException e) {
			res = e;
		}
		return res;
	}
}
