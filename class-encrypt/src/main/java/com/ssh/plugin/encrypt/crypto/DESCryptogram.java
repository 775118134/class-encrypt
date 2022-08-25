package com.ssh.plugin.encrypt.crypto;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESCryptogram extends Cryptogram {
	public DESCryptogram(byte[] key) {
		super(key);
	}

	public DESCryptogram(String key) {
		super(key.getBytes());
	}

	private interface DESEncryption {
		public Cipher getCipher();

		public Cipher initCipher();
	}

	private enum Encryption implements DESEncryption {
		INSTANCE() {
			public Cipher initCipher() {
				try {
					String algorithm = "DES";
					SecureRandom sr = new SecureRandom();
					DESKeySpec dks = new DESKeySpec(DESCryptogram.getKey());
					SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
					SecretKey key = keyFactory.generateSecret(dks);
					Cipher cipher = Cipher.getInstance(algorithm);
					cipher.init(Cipher.ENCRYPT_MODE, key, sr);
					return cipher;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		private Cipher cipher = initCipher();

		public Cipher getCipher() {
			return cipher;
		}
	}

	private enum Decryption implements DESEncryption {
		INSTANCE {
			public Cipher initCipher() {
				System.out.println("init Decryption");
				try {
					String algorithm = "DES";
					SecureRandom sr = new SecureRandom();
					DESKeySpec dks = new DESKeySpec(DESCryptogram.getKey());
					SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
					SecretKey key = keyFactory.generateSecret(dks);
					Cipher cipher = Cipher.getInstance(algorithm);
					cipher.init(Cipher.DECRYPT_MODE, key, sr);
					return cipher;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};

		private Cipher cipher = initCipher();

		public Cipher getCipher() {
			return cipher;
		}
	}

	@Override
	public byte[] encode(byte[] input) throws Exception {
		return Encryption.INSTANCE.getCipher().doFinal(input);
	}

	@Override
	public byte[] decode(byte[] input) throws Exception {
		return Decryption.INSTANCE.getCipher().doFinal(input);
	}

}
