package com.lucaci32u4.IO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class IOInterface {
	private static final String separator = FileSystems.getDefault().getSeparator();
	
	private AtomicReference<PrintStream> stderr = new AtomicReference<>(System.out);
	private AtomicBoolean initialised = new AtomicBoolean(false);
	private AtomicReference<String> workspace = new AtomicReference<>("");
	
	private static IOInterface ourInstance = new IOInterface();
	public IOInterface getInstance() {
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
				res = IOUtils.toString(inputStream, "UTF-8");
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
		if (!dir.endsWith(separator)) dir += separator;
		return workspace.getAndSet(dir);
	}
	
	private String convertToSystemPath(String path) {
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
			res = FileUtils.readFileToString(new File(path), "UTF-8");
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
	
	public static String getDefaultsystemWorkspace() {
		String wksp;
		if (System.getProperty("os.name").startsWith("Windows")) {
			//wksp = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
			wksp = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
		} else {
			wksp = System.getProperty("user.home");
		}
		if (!wksp.endsWith(separator)) wksp += separator;
		
		return wksp;
	}
}
