package com.ssh.plugin.encrypt.type;

import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

import com.ssh.plugin.encrypt.crypto.Cryptogram;
import com.ssh.plugin.encrypt.crypto.CryptogramFactory;
import com.ssh.plugin.encrypt.mode.ClassModeFactory;
import com.ssh.plugin.encrypt.mode.Mode;

public class ClassType implements Type {

	public void handle(EncryptContext ctx) {
		Cryptogram cryptogram = CryptogramFactory.factory(ctx.getEncryptEncryption(), ctx);
		Mode mode = ClassModeFactory.factory(ctx.getEncryptMode(), ctx);
		try {
			this.encryptClassFile(cryptogram, ctx.getSourceFile(), ctx.getTargetFile());
			mode.handle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void encryptClassFile(Cryptogram cryptogram, String sourceFile, String targetFile) throws Exception {
		try (ZipFile zipFile = new ZipFile(sourceFile);
				FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
				ZipOutputStream zos = new ZipOutputStream(fileOutputStream);) {
			ZipArchiveEntry entry = null;
			String name = null;
			byte[] buffer = null;
			for (Enumeration<ZipArchiveEntry> entries = zipFile.getEntries(); entries.hasMoreElements();) {
				entry = entries.nextElement();
				name = entry.getName();
				zos.putNextEntry(new ZipArchiveEntry(name));
				if (name.endsWith(".class")) {
					buffer = IOUtils.toByteArray(zipFile.getInputStream(entry));
					buffer = cryptogram.encode(buffer);
					zos.write(buffer);
					System.out.println("\t---encrypt class file: " + name);
				} else {
					IOUtils.copy(zipFile.getInputStream(entry), zos);
					System.out.println("\t\t!!!no encrypt class file: " + name);

				}
				zos.flush();
			}
		}
		System.out.println("~~~~~~~~~~~~~~~~ Encryption file: " + targetFile + " ~~~~~~~~~~~~~~~~");
	}

}
