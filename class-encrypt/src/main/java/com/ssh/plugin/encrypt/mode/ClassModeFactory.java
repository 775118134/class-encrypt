package com.ssh.plugin.encrypt.mode;

import com.ssh.plugin.encrypt.type.EncryptContext;

public class ClassModeFactory {

	public static Mode factory(String encryptMode, EncryptContext encryptContext) {
		if ("agent".equalsIgnoreCase(encryptMode)) {
			return new AgentClassMode(encryptContext.getOutputDirectory(), encryptContext.getFinalName(),
					encryptContext.getEncryptKey());
		} else if ("jvm".equalsIgnoreCase(encryptMode)) {
			return new JVMClassMode(encryptContext.getOutputDirectory(), encryptContext.getEncryptKey());
		} else {
			throw new RuntimeException(
					"invalid encryptMode ：" + encryptMode + " you can use : agent and jvm parameter");
		}
	}

}
