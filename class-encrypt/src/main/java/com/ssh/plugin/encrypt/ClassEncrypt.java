package com.ssh.plugin.encrypt;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.ssh.plugin.encrypt.type.EncryptContext;
import com.ssh.plugin.encrypt.type.Type;
import com.ssh.plugin.encrypt.type.TypeFactory;

@Mojo(name = "encrypt", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ClassEncrypt extends AbstractMojo {
	// TODO 输入
	@Parameter(defaultValue = "${project.build.finalName}", required = true)
	private String finalName;

	@Parameter(defaultValue = "${project.packaging}", property = "packaging", required = true)
	private String packaging;

	// TODO 输出
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDirectory", required = true)
	private String outputDirectory;

	@Parameter(defaultValue = "class-encrypts", property = "shadedClassifierName", required = true)
	private String shadedClassifierName;

	// TODO 参数
	@Parameter(defaultValue = "class", property = "encryptType", required = true)
	private String encryptType;// class/jar

	@Parameter(defaultValue = "jvm", property = "encryptMode", required = true)
	private String encryptMode;// original/agent/jvm

	@Parameter(defaultValue = "des", property = "encryptEncryption", required = true)
	private String encryptEncryption;// des

	@Parameter(property = "encryptKey")
	private String encryptKey;// 输入/随机

	// TODO =================================
	public void execute() {
		getLog().info(
				"↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ class encrypt ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");

		System.out.println("-------------------------------------------------------------------------\n"
				+ "--------    Usage:                                               --------\n"
				+ "--------     <configuration>                                     --------\n"
				+ "--------         <encryptType>class</encryptType>                --------\n"
				+ "--------         <encryptMode>jvm</encryptMode>                  --------\n"
				+ "--------         <encryptEncryption>des</encryptEncryption>      --------\n"
				+ "--------         <encryptPwd8Multiple></encryptPwd8Multiple>     --------\n"
				+ "--------     </configuration>                                    --------\n"
				+ "--------    where parameters include:                            --------\n"
				+ "--------     encryptType:[class|jar]                             --------\n"
				+ "--------                 Encrypt file type                       --------\n"
				+ "--------                 The default parameter is class          --------\n"
				+ "--------                                                         --------\n"
				+ "--------     encryptMode:[agent|jvm]                             --------\n"
				+ "--------                 Encrypted file mode                     --------\n"
				+ "--------                 The default parameter is jvm            --------\n"
				+ "--------                                                         --------\n"
				+ "--------     encryptEncryption:[des]                             --------\n"
				+ "--------                 Encryption                              --------\n"
				+ "--------                 The default parameter is des            --------\n"
				+ "--------                                                         --------\n"
				+ "--------     encryptKey:                                         --------\n"
				+ "--------                 Encryption key, it is a multiple of 8   --------\n"
				+ "--------                 The default parameter is random         --------\n"
				+ "-------------------------------------------------------------------------\n");

		File sourceFile = new File(outputDirectory, finalName + "." + packaging);
		if (!sourceFile.exists()) {
			getLog().error("~~~~~~~~~~~~~~~~ Class encrypt file not found exception ： " + sourceFile.getAbsolutePath() + " ~~~~~~~~~~~~~~~~");
			return;
		}
		byte[] key = null;
		if (!StringUtils.isEmpty(encryptKey)) {
			key = encryptKey.getBytes();
		}
		EncryptContext ctx = new EncryptContext(encryptType, encryptMode, encryptEncryption, key, outputDirectory,
				finalName, sourceFile.getAbsolutePath(),
				Paths.get(outputDirectory + "/" + finalName + "-" + shadedClassifierName + "." + packaging).toString(),
				getLog());
		System.out.println(ctx);

		Type type = TypeFactory.factory(encryptType);
		type.handle(ctx);

		getLog().info(
				"↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ class encrypt ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
	}

}
