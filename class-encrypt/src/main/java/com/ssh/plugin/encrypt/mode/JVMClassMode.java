package com.ssh.plugin.encrypt.mode;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.ssh.plugin.encrypt.compiler.JavaStringCompiler;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class JVMClassMode extends JVMMode {

	public JVMClassMode(String outputPath, byte[] encryptKey) {
		super(outputPath, encryptKey);
	}

	@Override
	protected void initZosPutEntrys(Map<String, byte[]> zosPutEntrys, byte[] encryptKey) {
		try {
			Map<String, byte[]> classDecypherClasss = generateClassLoader$ClassDecypherClass(encryptKey);
			ClassPool pool = ClassPool.getDefault();
			classDecypherClasss.forEach((k, v) -> {
				zosPutEntrys.put(k.replaceAll("\\.", "/") + ".class", v);
				try {
					pool.makeClass(new ByteArrayInputStream(v));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			zosPutEntrys.putAll(generateClassLoaderClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, byte[]> generateClassLoader$ClassDecypherClass(byte[] encryptKey) throws Exception {
		JavaStringCompiler compiler = new JavaStringCompiler();
		Map<String, byte[]> classCompileResources = compiler.getClassCompileResources(
				"java/lang/ClassLoader$ClassDecypher.java",
				"package java.lang;\n"
						+ "enum ClassLoader$ClassDecypher {																					"
						+ " 		INSTENCE {																								"
						+ " 		private javax.crypto.Cipher cipher = initCipher();														"
						+ " 																												"
						+ " 		private javax.crypto.Cipher initCipher() {																"
						+ " 			try {																								"
						+ " 				String algorithm = \"DES\";																		"
						+ " 				java.security.SecureRandom sr = new java.security.SecureRandom();								"
						+ " 				byte rawKey[] = " + ArrayUtils.toString(encryptKey)
						+ ";										"
						+ " 				javax.crypto.spec.DESKeySpec dks = new javax.crypto.spec.DESKeySpec(rawKey);					"
						+ " 				javax.crypto.SecretKeyFactory keyFactory = javax.crypto.SecretKeyFactory.getInstance(algorithm);"
						+ " 				javax.crypto.SecretKey key = keyFactory.generateSecret(dks);									"
						+ " 				javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(algorithm);						"
						+ " 				cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, sr);											"
						+ " 				return cipher;																					"
						+ " 			} catch (Exception e) {																				"
						+ " 				System.out.println(\"CA FE BA BE ERROR\" + e.getMessage());										"
						+ " 			}																									"
						+ " 			return null;																						"
						+ " 		}																										"
						+ " 																												"
						+ " 		@Override																								"
						+ " 		public byte[] decrypt(byte[] source) throws Exception {													"
						+ " 			return cipher.doFinal(source);																		"
						+ " 		}																										"
						+ " 	};																											"
						+ " 	public abstract byte[] decrypt(byte[] source) throws Exception;												"
						+ " }																												");
		return classCompileResources;
	}

	private Map<String, byte[]> generateClassLoaderClass() throws Exception {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.get("java.lang.ClassLoader");

		cc.addField(CtField.make("private final byte[] isDecodableSecretKey = new byte[]{ -54, -2, -70, -66 };", cc));

		CtMethod isDecodableMthd = CtNewMethod
				.make(" private boolean isDecodable(byte[] _d) {							"
						+ " 	try {															"
						+ " 		for (int i = 0; i < isDecodableSecretKey.length; i++) {		"
						+ " 			if (isDecodableSecretKey[i] != _d[i])					"
						+ " 				return true;										"
						+ " 		}															"
						+ " 	} catch (Exception e) {											"
						+ " 		return false;												"
						+ " 	}																"
						+ " 	return false;													"
						+ " }																	", cc);
		cc.addMethod(isDecodableMthd);

		CtMethod defineClassMeth = cc.getMethod("defineClass",
				"(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;");
		defineClassMeth.insertBefore(" try {															 "
				+ " 	if (isDecodable($2)) {										 "
				+ " 		$2 = ClassLoader.ClassDecypher.INSTENCE.decrypt($2);	 "
				+ " 		$3 = 0;													 "
				+ " 		$4 = $2.length;											 "
				+ " 	}															 "
				+ " } catch (Exception e) {											 "
				+ " }																 ");

		return Collections.singletonMap("java/lang/ClassLoader.class", cc.toBytecode());
	}

}
