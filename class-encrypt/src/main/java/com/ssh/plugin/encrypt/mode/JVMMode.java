package com.ssh.plugin.encrypt.mode;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

public abstract class JVMMode implements Mode {
	protected static final String RT_JAR_EXTENSION = File.separator + "rt.jar";
	protected static final String JAVA_HOME_RT_JAR = System.getProperty("java.home") + File.separator + "lib"
			+ RT_JAR_EXTENSION;

	private String outputDirectoryPath;
	private byte[] encryptKey;

	public JVMMode(String outputPath, byte[] encryptKey) {
		Path path = Paths.get(outputPath + RT_JAR_EXTENSION);
		outputDirectoryPath = path.toString();

		this.encryptKey = encryptKey;
	}

	protected abstract void initZosPutEntrys(Map<String, byte[]> zosPutEntrys, byte[] encryptKey);

	public void handle() throws Exception {

		Map<String, byte[]> zosPutEntrys = new HashMap<>();
		initZosPutEntrys(zosPutEntrys, encryptKey);

		try (ZipFile zipFile = new ZipFile(JAVA_HOME_RT_JAR);
				FileOutputStream fileOutputStream = new FileOutputStream(outputDirectoryPath);
				ZipOutputStream zos = new ZipOutputStream(fileOutputStream);) {

			for (Entry<String, byte[]> zosPutEntry : zosPutEntrys.entrySet()) {
				zos.putNextEntry(new ZipArchiveEntry(zosPutEntry.getKey()));
				zos.write(zosPutEntry.getValue());
			}

			ZipArchiveEntry entry = null;
			String name = null;
			for (Enumeration<ZipArchiveEntry> entries = zipFile.getEntries(); entries.hasMoreElements();) {
				entry = entries.nextElement();
				name = entry.getName();
				if (zosPutEntrys.get(name) == null) {
					zos.putNextEntry(new ZipArchiveEntry(name));
					IOUtils.copy(zipFile.getInputStream(entry), zos);
					zos.flush();
				}
			}
		}
		this.printSuccessLog(outputDirectoryPath);
	}

	private void printSuccessLog(String outputDirectory) {
		System.out.println("~~~~~~~~~~~~~~~~ Decryption file: " + outputDirectory + " ~~~~~~~~~~~~~~~~");
		System.out.println("~~~~~~~~~~~~~~~~ Instructions: Will decrypt the file to cover you " + JAVA_HOME_RT_JAR
				+ " And then normal execution ~~~~~~~~~~~~~~~~");
	}

}
