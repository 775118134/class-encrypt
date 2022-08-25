package com.ssh.plugin.encrypt.type;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.maven.plugin.logging.Log;

public class EncryptContext {
	public static Log log;
	private final String encryptType;// class/jar

	private final String encryptMode;// original/agent/jvm

	private final String encryptEncryption;// des

	private byte[] encryptKey;// 输入/随机

	private final String outputDirectory;// 输出路径

	private final String finalName;// 原始文件名

	private final String sourceFile;

	private final String targetFile;

	public EncryptContext(String encryptType, String encryptMode, String encryptEncryption, byte[] encryptKey,
			String outputDirectory, String finalName, String sourceFile, String targetFile, Log _log) {
		this.encryptType = encryptType;
		this.encryptMode = encryptMode;
		this.encryptEncryption = encryptEncryption;
		this.encryptKey = encryptKey;
		this.outputDirectory = outputDirectory;
		this.finalName = finalName;
		this.sourceFile = sourceFile;
		this.targetFile = targetFile;
		log = _log;
	}

	public String getEncryptType() {
		return encryptType;
	}

	public String getEncryptMode() {
		return encryptMode;
	}

	public String getEncryptEncryption() {
		return encryptEncryption;
	}

	public void setEncryptKey(byte[] encryptKey) {
		this.encryptKey = encryptKey;
	}

	public byte[] getEncryptKey() {
		return encryptKey;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public String getFinalName() {
		return finalName;
	}

	public String getSourceFile() {
		return sourceFile;
	}

	public String getTargetFile() {
		return targetFile;
	}

	@Override
	public String toString() {
		return "Context [encryptType=" + encryptType + ", encryptMode=" + encryptMode + ", encryptEncryption="
				+ encryptEncryption + ", encryptKey=" + ArrayUtils.toString(encryptKey) + ", outputDirectory="
				+ outputDirectory + ", finalName=" + finalName + ", sourceFile=" + sourceFile + ", targetFile="
				+ targetFile + "]";
	}

}
