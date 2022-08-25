package com.ssh.plugin.encrypt.type;

public class TypeFactory {

	public static Type factory(String encryptType) {
		if ("jvm".equalsIgnoreCase(encryptType)) {
			return new JvmType();
		} else if ("class".equalsIgnoreCase(encryptType)) {
			return new ClassType();
		} else {
			throw new RuntimeException("invalid encryptType ：" + encryptType + " you can use : jvm and class parameter");
		}
	}

}
