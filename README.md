# Transfer
基于Netty编写的，局域网传输器，UI使用Swing。

分片上传大小默认8k，断点续传。
已经在pom中加入的了打包插件。直接在项目根目录下执行 mvn package 就可以得到完整包。
在bat文件中，直接 java -jar xx.jar
JProgressBarPanel为启动类

![image](https://github.com/18920522006/Transfer/blob/master/src/main/resources/image/首页.png)

![image](https://github.com/18920522006/Transfer/blob/master/src/main/resources/image/发布服务.png)

![image](https://github.com/18920522006/Transfer/blob/master/src/main/resources/image/启动服务.png)

![image](https://github.com/18920522006/Transfer/blob/master/src/main/resources/image/上传.png)

如果向服务器传入空文件，在客户端拦截，报文件异常！

![image](https://github.com/18920522006/Transfer/blob/master/src/main/resources/image/上传进度.png)
