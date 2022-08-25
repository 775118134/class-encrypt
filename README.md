# JAVA Class 加密

## 应用场景

1、第三方部署演示场景

2、项目开发完，但未验收场景

。。。 。。。

不管怎样的应用场景，只有存在非信任的情况，都有必要为编译文件加密，可避免源码泄漏或字节码被反编译。

## 实现原理

> class加载原理：
>
> >loadclass:判断是否已加载，使用双亲委派模型，请求父加载器，都为空，使用findclass
> >
> >>findclass:根据名称或位置加载.class字节码,然后使用defineClass
> >>
> >>>defineclass：解析定义.class字节流，返回class对象

基于上述应用场景及实现原理 写了一个maven 插件 ,目前仅支持des加密。

## 使用方式

配置参数：

```
-------------------------------------------------------------------------
--------    Usage:                                               --------
--------     <configuration>                                     --------
--------         <encryptType>class</encryptType>                --------
--------         <encryptMode>jvm</encryptMode>                  --------
--------         <encryptEncryption>des</encryptEncryption>      --------
--------         <encryptPwd8Multiple></encryptPwd8Multiple>     --------
--------     </configuration>                                    --------
--------    where parameters include:                            --------
--------     encryptType:[class|jar]                             --------
--------                 Encrypt file type                       --------
--------                 The default parameter is class          --------
--------                                                         --------
--------     encryptMode:[agent|jvm]                             --------
--------                 Encrypted file mode                     --------
--------                 The default parameter is jvm            --------
--------                                                         --------
--------     encryptEncryption:[des]                             --------
--------                 Encryption                              --------
--------                 The default parameter is des            --------
--------                                                         --------
--------     encryptKey:                                         --------
--------                 Encryption key, it is a multiple of 8   --------
--------                 The default parameter is random         --------
-------------------------------------------------------------------------
```

项目中使用：

```xml
<build>
		<finalName>XXX</finalName>
		... ...
		<plugins>
			<plugin>
				<groupId>com.ssh.plugin</groupId>
				<artifactId>maven-class-encrypt-plugin</artifactId>
				<version>0.0.1-SNAPSHOT</version>
				<executions>
					<execution>
						<phase>package</phase>
						<goals>
							<goal>encrypt</goal>
						</goals>
						<configuration>
							<encryptKey>XXXXXXXXXXXXXXXXXXXXX</encryptKey>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
```

在项目打包时直接输出 `XXX-class-encrypts.jar`和`rt.jar` ;`XXX-class-encrypts.jar`为已加密jar包，将`rt.jar` 覆盖`jre\lib`下即可（目前仅支持jdk1.8）。（亦可通过配置修改`<encryptMode>agent</encryptMode>`为`-javaagent`模式）



个人感觉前端(`ast`)和java 对于混淆（合并、压缩）、加密都是防君子不防小人；

打个广告：本人有偿承接 java 或 前端 加解密 任务![img](file:///assets/01395BBA.png)



###### 项目完成时间：2017年5月

