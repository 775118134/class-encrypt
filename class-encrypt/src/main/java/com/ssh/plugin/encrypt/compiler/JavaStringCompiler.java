package com.ssh.plugin.encrypt.compiler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * In-memory compile Java source code as String.
 * 
 * @author ssh
 */
public class JavaStringCompiler {

	private final JavaCompiler compiler;
	private final StandardJavaFileManager stdManager;

	public JavaStringCompiler() {
		this.compiler = ToolProvider.getSystemJavaCompiler();
		this.stdManager = compiler.getStandardFileManager(null, null, null);
	}

	private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([$_a-zA-Z][$_a-zA-Z0-9\\.]*);");

	private static final Pattern PUBLIC_CLASS_PATTERN = Pattern
			.compile("public\\s+class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s+");

	private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s+");

	// private static final String CLASS_EXTENSION = ".class";

	private static final String JAVA_EXTENSION = ".java";

	private String parseClassName(String code) {
		Matcher matcher = PACKAGE_PATTERN.matcher(code);
		String pkg;
		if (matcher.find()) {
			pkg = matcher.group(1);
		} else {
			pkg = "";
		}
		matcher = PUBLIC_CLASS_PATTERN.matcher(code);
		String cls;
		if (matcher.find()) {
			cls = matcher.group(1);
		} else {
			matcher = CLASS_PATTERN.matcher(code);
			if (matcher.find()) {
				cls = matcher.group(1);
			} else {
				throw new IllegalArgumentException("No such class name in " + code);
			}
		}
		return pkg != null && pkg.length() > 0 ? pkg + "." + cls : cls;
	}

	/**
	 * Compile a Java source file in memory.
	 * 
	 * @param source
	 *            The source code as String.
	 * @return The compiled results as Map that contains class name as key,
	 *         class binary as value.
	 * @throws IOException
	 *             If compile error.
	 */
	public Map<String, byte[]> getClassCompileResources(String source) throws IOException {
		String className = parseClassName(source);
		String fileName = className.replaceAll("\\.", "/") + JAVA_EXTENSION;
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
			JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);
			CompilationTask task = compiler.getTask(null, manager, null, null, null, Arrays.asList(javaFileObject));
			Boolean result = task.call();
			if (result == null || !result.booleanValue()) {
				throw new RuntimeException("Compilation failed.");
			}
			return manager.getClassBytes();
		}
	}

	/**
	 * Compile a Java source file in memory.
	 * 
	 * @param fileName
	 *            Java file name, e.g. "com/ssh/Test.java"
	 * @param source
	 *            The source code as String.
	 * @return The compiled results as Map that contains class name as key,
	 *         class binary as value.
	 * @throws IOException
	 *             If compile error.
	 */
	public Map<String, byte[]> getClassCompileResources(String fileName, String source) throws IOException {
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
			JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);
			CompilationTask task = compiler.getTask(null, manager, null, null, null, Arrays.asList(javaFileObject));
			Boolean result = task.call();
			if (result == null || !result.booleanValue()) {
				throw new RuntimeException("Compilation failed.");
			}
			return manager.getClassBytes();
		}
	}

	/**
	 * Compile a Java source file in memory.
	 * 
	 * @param source
	 *            The source code as String.
	 * @return The compiled results as Class Object,
	 * @throws IOException
	 * @throws ClassNotFoundException
	 *             If compile error.
	 */
	public Class<?> compile(String source) throws IOException, ClassNotFoundException {
		String className = parseClassName(source);
		return this.compile(className, source);
	}

	/**
	 * Compile a Java source file in memory.
	 *
	 * @param className
	 *            Java class name, e.g. "com.ssh.Test"
	 * @param source
	 *            The source code as String.
	 * @return The compiled results as Class Object,
	 * @throws IOException
	 * @throws ClassNotFoundException
	 *             If compile error.
	 */
	public Class<?> compile(String className, String source) throws IOException, ClassNotFoundException {
		String fileName = className.replaceAll("\\.", "/") + JAVA_EXTENSION;
		return this.compile(fileName, className, source);
	}

	/**
	 * Compile a Java source file in memory.
	 * 
	 * @param fileName
	 *            Java file name, e.g. "com/ssh/Test.java"
	 * @param className
	 *            Java class name, e.g. "com.ssh.Test"
	 * @param source
	 *            The source code as String.
	 * @return The compiled results as Class Object,
	 * @throws IOException
	 * @throws ClassNotFoundException
	 *             If compile error.
	 */
	public Class<?> compile(String fileName, String className, String source)
			throws IOException, ClassNotFoundException {
		System.out.println("fileNae: " + fileName + " className: " + className + " source: " + source);
		Map<String, byte[]> classCompileResources = this.getClassCompileResources(fileName, source);
		return this.loadClass(className, classCompileResources);
	}

	/**
	 * Load class from compiled classes.
	 * 
	 * @param name
	 *            Full class name.
	 * @param classBytes
	 *            Compiled results as a Map.
	 * @return The Class instance.
	 * @throws ClassNotFoundException
	 *             If class not found.
	 * @throws IOException
	 *             If load error.
	 */
	public Class<?> loadClass(String name, Map<String, byte[]> classBytes) throws ClassNotFoundException, IOException {
		try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes)) {
			return classLoader.loadClass(name);
		}
	}
}
