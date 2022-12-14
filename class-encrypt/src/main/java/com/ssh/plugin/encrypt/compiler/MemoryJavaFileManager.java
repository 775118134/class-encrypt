package com.ssh.plugin.encrypt.compiler;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;

/**
 * In-memory java file manager.
 * 
 * @author ssh
 */
public class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	// compiled classes in bytes:
	private final Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

	public MemoryJavaFileManager(JavaFileManager fileManager) {
		super(fileManager);
	}

	public Map<String, byte[]> getClassBytes() {
		return new HashMap<String, byte[]>(this.classBytes);
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
		classBytes.clear();
	}

	@Override
	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, Kind kind,
			FileObject sibling) throws IOException {
		if (kind == Kind.CLASS) {
			return new MemoryOutputJavaFileObject(className);
		} else {
			return super.getJavaFileForOutput(location, className, kind, sibling);
		}
	}

	public JavaFileObject makeStringSource(String name, String code) {
		return new MemoryInputJavaFileObject(name, code);
	}

	private class MemoryInputJavaFileObject extends SimpleJavaFileObject {

		private final String code;

		private MemoryInputJavaFileObject(String name, String code) {
			super(URI.create("string:///" + name), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
			return CharBuffer.wrap(code);
		}
	}

	private class MemoryOutputJavaFileObject extends SimpleJavaFileObject {
		private final String name;

		private MemoryOutputJavaFileObject(String name) {
			super(URI.create("string:///" + name), Kind.CLASS);
			this.name = name;
		}

		@Override
		public OutputStream openOutputStream() {
			return new FilterOutputStream(new ByteArrayOutputStream()) {
				@Override
				public void close() throws IOException {
					ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
					classBytes.put(name, bos.toByteArray());
					out.close();
					bos.close();
				}
			};
		}
	}
}
