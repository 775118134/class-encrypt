package com.ssh.plugin.encrypt.crypto;

import com.ssh.plugin.encrypt.type.EncryptContext;

public class CryptogramFactory {

	public static Cryptogram factory(String encryptEncryption, EncryptContext encryptContext) {
		if ("DES".equalsIgnoreCase(encryptEncryption)) {
			if (encryptContext.getEncryptKey() == null) {
				encryptContext.setEncryptKey(Cryptogram.getRandomKey(128));
			}
			return new DESCryptogram(encryptContext.getEncryptKey());
		} else {
			throw new RuntimeException("invalid encryptEncryption ：" + encryptEncryption + " you can use : DES parameter");
		}
	}

}
