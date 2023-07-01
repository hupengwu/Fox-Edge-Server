# fox-edge-server-common-utils-rsa

#### 介绍
``` 
RSA非对称加密工具
1、命令行打包
javac -encoding UTF-8 -d . RSAEncrypt.java RSASignature.java RSAExecute.java
jar -cvf RSASignature.jar .\com\foxteam\common\utils\rsa\RSAEncrypt.class .\com\foxteam\common\utils\rsa\RSASignature.class .\com\foxteam\common\utils\rsa\RSAExecute.class
jar -xvf RSASignature.jar
jar -cvfm RSASignature.jar .\MANIFEST.MF .\com\foxteam\common\utils\rsa\RSAEncrypt.class .\com\foxteam\common\utils\rsa\RSASignature.class .\com\foxteam\common\utils\rsa\RSAExecute.class

2、命令行签名
java -jar RSASignature.jar d:\ C3060400FFFBEBBF
pause

``` 
#### 软件架构

