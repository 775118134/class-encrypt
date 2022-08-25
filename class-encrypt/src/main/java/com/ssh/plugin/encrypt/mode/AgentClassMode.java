package com.ssh.plugin.encrypt.mode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.lang3.ArrayUtils;

import com.ssh.plugin.encrypt.compiler.JavaStringCompiler;

public class AgentClassMode implements Mode {
	private final static String META_INF = "META-INF/MANIFEST.MF";
	private final static String META_INF_CONTENT = "Manifest-Version: 1.0\nPremain-Class: com.ssh.instrument.PremainClass\n";
	private final static String AGENT_SUFFIX = "-java-agent.jar";
	private final static String CLASS_EXTENSION = ".class";

	private String outputDirectoryPath;
	private byte[] encryptKey;

	public AgentClassMode(String outputDirectory, String finalName, byte[] encryptKey) {
		this.outputDirectoryPath = outputDirectory + finalName + AGENT_SUFFIX;
		this.encryptKey = encryptKey;
	}

	public String getAgentJavaContent() {
		return "package com.ssh.instrument;\n"
				+ "\n"
				+ "import java.lang.instrument.Instrumentation;\n"
				+ "import java.security.SecureRandom;\n"
				+ "\n"
				+ "import javax.crypto.Cipher;\n"
				+ "import javax.crypto.SecretKey;\n"
				+ "import javax.crypto.SecretKeyFactory;\n"
				+ "import javax.crypto.spec.DESKeySpec;\n"
				+ "\n"
				+ "public class PremainClass {\n"
				+ "\n"
				+ "	private static final Cipher cipher = init();\n"
				+ "\n"
				+ "	private static Cipher init() {\n"
				+ "		try {\n"
				+ "			String algorithm = \"DES\";\n"
				+ "			SecureRandom sr = new SecureRandom();\n"
				+ "			byte rawKey[] = " + ArrayUtils.toString(encryptKey) + ";\n"
				+ "			DESKeySpec dks = new DESKeySpec(rawKey);\n"
				+ "			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);\n"
				+ "			SecretKey key = keyFactory.generateSecret(dks);\n"
				+ "			Cipher cipher = Cipher.getInstance(algorithm);\n"
				+ "			cipher.init(Cipher.DECRYPT_MODE, key, sr);\n"
				+ "			return cipher;\n"
				+ "		} catch (Exception e) {\n"
				+ "			e.printStackTrace();\n"
				+ "			System.exit(-1);\n"
				+ "		}\n"
				+ "		return null;\n"
				+ "	}\n"
				+ "\n"
				+ "	public static void premain(String agentArgs, Instrumentation instrumentation) {\n"
				+ "		instrumentation.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {\n"
				+ "			byte decryptedClassData[];\n"
				+ "			System.out.println(\"================ init: \" + className + \" ;class.length: \" + classfileBuffer.length);\n"
				+ "			try {\n"
				+ "				decryptedClassData = cipher.doFinal(classfileBuffer);\n"
				+ "				System.out.println(\" decrypted\");\n"
				+ "			} catch (Exception e) {\n"
				+ "				decryptedClassData = classfileBuffer;\n"
				+ "				System.out.println(\" not decrypted\");\n"
				+ "			}\n"
				+ "			return decryptedClassData;\n"
				+ "		});\n"
				+ "	}\n"
				+ "\n"
				+ "	public static void main(String[] args) {\n"
				+ "		PremainClass p = new PremainClass();\n"
				+ "		System.out.println(p);\n"
				+ "\n"
				+ "	}\n"
				+ "}";
	}

	@Override
	public void handle() throws Exception {
		JavaStringCompiler compiler = new JavaStringCompiler();
		Map<String, byte[]> classCompileResources = compiler.getClassCompileResources(getAgentJavaContent());
		try (FileOutputStream fileOutputStream = new FileOutputStream(new File(outputDirectoryPath));
				ZipOutputStream zos = new ZipOutputStream(fileOutputStream)) {
			classCompileResources.forEach((k, v) -> {
				try {
					zos.putNextEntry(new ZipArchiveEntry(k.replaceAll("\\.", "/") + CLASS_EXTENSION));
					zos.write(v);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			zos.putNextEntry(new ZipArchiveEntry(META_INF));
			zos.write(META_INF_CONTENT.getBytes());
		}
		this.printSuccessLog(outputDirectoryPath);
	}

	private void printSuccessLog(String outputDirectory) {
		System.out.println("~~~~~~~~~~~~~~~~ Decryption file: " + outputDirectory + " ~~~~~~~~~~~~~~~~");
		System.out.println(
				"~~~~~~~~~~~~~~~~ Instructions: java -javaagent:" + outputDirectory + " -jar xxx.jar ~~~~~~~~~~~~~~~~");
	}

}
