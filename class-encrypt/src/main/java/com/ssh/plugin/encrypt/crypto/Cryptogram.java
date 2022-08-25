package com.ssh.plugin.encrypt.crypto;

import java.util.Random;

public abstract class Cryptogram {
	private static byte[] key;

	public Cryptogram(byte[] _key) {
		key = _key;
	}

	protected static byte[] getKey() {
		return key;
	}

	public static final byte[] getRandomKey(int num) {
		byte[] sb = new byte[num];
		Random r = new Random();
		r.nextBytes(sb);
		return sb;
	}

	public abstract byte[] encode(byte[] input) throws Exception;

	public abstract byte[] decode(byte[] input) throws Exception;

}
