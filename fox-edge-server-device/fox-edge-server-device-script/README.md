# fox-edge-server-device-script

#### 介绍

device服务的JavaScript引擎，通过JavaScript脚本提供了设备协议的编码/解码能力。

它跟Java的Jar比起来，主要是提供了JavaScript脚本的特性。JavaScript是一种非常大众化的脚本语言，有非常多的开发者会使用这门语言。

同时，Java的Jar是一个静态的解码器，它的开发过程，需要编写Java代码，编译、部署，重启device服务，性能虽然更好，
但是很多客户的设备数量不多，Java带来的性能并没有什么太多意义，冗长的开发到部署的流程，却是非常糟糕的体验。

所以，考虑到上面的因素，灵狐提供了JavaScript引擎的特性，来实现通过JavaScript来动态解码的能力

#### ECMAScript

JavaScript1999年，欧洲计算机制造协会（ECMA）在JavaScript1.5版本基础上指定了“ECMAScript程序语言规范书”（ECMA-262标准），
该标准被国际标准化组织（ISO）采纳，作为各浏览器使用的脚本程序的统一标准。

并且除了浏览器，JAVA的Nashorn引擎，GO的Goja引擎，都支持ECMAScript 5.1规范的。

ECMAScript 5.1的规范是比较早期的版本，所以不支持很多后期的JS语法，但是基本上都有解，所以要注意这个特性。

#### 约束

Fox-Edge使用的是JavaScript是基于JVM自带的Nashorn来运行的，Nashorn支持的JavaScript技术标准是ECMAScript。



