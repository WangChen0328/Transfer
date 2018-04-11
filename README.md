# Transfer
基于Netty编写的，局域网传输器，UI使用Swing。

分片上传大小默认5M，断点续传。50秒心跳验证，白名单验证。md5值验证文件。

已经在pom中加入的了打包插件。直接在项目根目录下执行 mvn package 就可以得到完整包。

在bat文件中，直接 java -jar xx.jar

JProgressBarPanel为启动类

![image](https://github.com/18920522006/Transfer/blob/master/src/main/resources/image/发布服务.png)

![image](https://github.com/18920522006/Transfer/blob/master/src/main/resources/image/上传.png)

如果向服务器传入空文件，在客户端拦截，报文件异常！

![image](https://github.com/18920522006/Transfer/blob/master/src/main/resources/image/上传进度.png)

---------------------------------------------------------------------------------------------------

加入接收Web页面的传输功能，JS 传输使用 WebUploader发送

以二进制形式发送数据。传输成功后，返回MD5加密的保存文件路径。

var uploader = WebUploader.create({
    sendAsBinary: true
});

由于 WebUploader 本身支持 跨域、分片发送、MD5校验等。和Netty可以完美配合。

![image](https://github.com/18920522006/Transfer/blob/master/src/main/resources/image/Web接收.png)
