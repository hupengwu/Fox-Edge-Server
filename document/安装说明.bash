#===============================================常用工具的安装===============================================#
#这些linux工具是fox-edge要用到的，必须向安装，否则会影响到fox-edge的启动

#dos2unix工具：windows环境下编辑的sh文件，经常会因为文件格式不同导致无法运行，此时需要使用dos2unix进行格式转
#换处理，否则会出现/bin/bash^M: bad interpreter: No such file or directory的错误，导致无法执行
apt install dos2unix -y


#===============================================常用工具的安装===============================================#

#==================================================安装SSH==================================================#

# 更新软件
apt-get update    
# 安装openssh
apt install openssh-server -y
#修改root密码
passwd root
#允许root远程登录
vim /etc/ssh/sshd_config
#将PermitRootLogin prohibit-password修改为PermitRootLogin yes

#重启ssh
service ssh restart

#重新远程登录SSH

#==================================================安装SSH==================================================#

#=================================================安装samba==================================================#
#参考：https://blog.csdn.net/dslobo/article/details/108175737

#更新
apt-get update

#安装redis
apt-get install samba -y

#修改配置文件
vim /etc/samba/smb.conf  
#增加下面的配置
#------------------#
[share]
# 设置共享目录
path = /
# 设置访问用户 
valid users = root
# 设置读写权限
writable = yes  
#------------------#

#创建samba用户
smbpasswd -a root

#重启samba
service smbd restart

#=================================================安装samba==================================================#

#==================================================安装JAVA==================================================#

# 更新软件
apt-get update    
# 安装java
apt-get install openjdk-11-jdk -y
#查看安装状态
java -version
#安装jmap
apt install openjdk-11-jdk-headless -y
#检查jmap是否安装成功
jmap


#==================================================安装JAVA==================================================#

#==================================================安装FTP==================================================#

# 更新软件
apt-get update    
# 安装vsftpd
apt-get install vsftpd -y
 
# 设置开机启动并启动ftp服务
systemctl enable vsftpd
systemctl start vsftpd

#查看其运行状态
systemctl  status vsftpd
#重启服务
systemctl  restart vsftpd

#==================================================安装FTP==================================================#


#==================================================添加FTP用户==============================================#
#参考文档：https://www.jianshu.com/p/f666278dc3b7

#创建一个共享目录
mkdir /home/uftp

#在操作系统上添加一个用户
useradd -d /home/uftp -s /bin/bash uftp

#设置用户的密码
passwd uftp

#修改文件夹的拥有者为uftp用户
chown uftp /home/uftp/

#编辑配置文件
vim /etc/vsftpd.conf
#在末尾添加如下配置：
userlist_deny=NO
userlist_enable=YES
userlist_file=/etc/allowed_users
seccomp_sandbox=NO
local_enable=YES
pasv_promiscuous=YES
write_enable=YES

#新建/etc/allowed_users文件
vim /etc/allowed_users 
#输入内容
uftp

#重启
service vsftpd restart


#==================================================添加FTP用户==============================================#


#================================================卸载mysql==================================================#
#查看MySQL依赖
dpkg --list|grep mysql
#卸载： 
apt-get remove mysql-common -y
#卸载： (这里版本对应即可)
apt-get autoremove --purge mysql-server-8.0 -y
#清除残留数据: 
dpkg -l|grep ^rc|awk '{print$2}'|sudo xargs dpkg -P
#再次查看MySQL的剩余依赖项: (这里一般就没有输出了，如果有执行下一步)
dpkg --list|grep mysql
#继续删除剩余依赖项，如：
apt-get autoremove --purge mysql-apt-config
apt purge mysql-*
#删除残余文件
rm -rf /etc/mysql/ /var/lib/mysql

apt autoremove

apt autoclean

#================================================卸载mysql==================================================#

#================================================安装mysql==================================================#
#更新源
apt-get update  
#安装
apt-get install mysql-server -y
#验证
systemctl status mysql

#修改配置文件:bind-address = 127.0.0.1修改为bind-address = 0.0.0.0
#关闭binlog日志：在末尾添加 skip-log-bin ，登录后可以用show variables like '%log_bin%%';查询log_bin变为off
vi /etc/mysql/mysql.conf.d/mysqld.cnf 
#

#重启mysql
systemctl restart mysql
#验证mysql
systemctl status mysql

#================================================安装mysql===================================================#


#================================================创建系统用户================================================#
# -u 指定用户名 -p需要输入密码  回车输入密码
mysql -u root -p  

#后面是进入mysql后的操作

#查看用户权限
mysql> 
use mysql;
select host, user, plugin from user;

#创建用户:'root'@'%'
create user 'root'@'%' identified by '12345678';
grant all privileges on *.* to 'root'@'%';
flush privileges;

#================================================创建系统用户================================================#

#================================================创建应用用户================================================#

#创建数据库fox_edge（注意，职能使用下划线，否则下面的授权会掉坑里去）
mysql> 
use mysql;
create database `fox_edge`;

#查看用户权限
use mysql;
select host, user, plugin from user;

#创建本地连接用户:'fox-edge'@'localhost'
drop user if exists 'fox-edge'@'localhost', 'fox-edge'@'%';
create user 'fox-edge'@'localhost' identified by '12345678';
grant all privileges on fox_edge.* to 'fox-edge'@'localhost';
flush privileges;

#创建远程连接用户:'fox-edge'@'%'
drop user if exists 'fox-edge'@'%', 'fox-edge'@'%';
create user 'fox-edge'@'%' identified by '12345678';
grant all privileges on fox_edge.* to 'fox-edge'@'%';
flush privileges;

#================================================创建应用用户================================================#

#=================================================安装redis==================================================#
#参考：https://gu-han-zhe.blog.csdn.net/article/details/117538180

#更新
apt-get update

#安装redis
apt install redis-server -y

#检查安装结果
systemctl status redis-server

#修改配置文件
vim /etc/redis/redis.conf
#1.注释掉 bind 127.0.0.1 ::1   位置在69行左右
#2.修改protected-mode为no      位置在88行左右
#3.修改requirepass为12345678   位置在507行左右

#重启redis
systemctl restart redis-server
#=================================================安装redis==================================================#


#=================================================ubuntu磁盘扩容=============================================#
#ubuntu22安装后，一半磁盘空间未使用的解决
#参考文章：https://blog.csdn.net/weixin_37830416/article/details/120792705
#参考文章：https://blog.csdn.net/weixin_43302340/article/details/120341241

#显示存在的卷组，Alloc PE是已经分配的磁盘空间，Free PE是尚未分配的磁盘空间
vgdisplay

#显查看磁盘目录：可以看到正在使用的磁盘/dev/mapper/ubuntu--vg-ubuntu--lv
df -h

#全部空间都给这个盘
lvextend -l +100%FREE /dev/mapper/ubuntu--vg-ubuntu--lv

#重新计算磁盘大小
resize2fs /dev/mapper/ubuntu--vg-ubuntu--lv

#再次显查看磁盘目录，可以看到/dev/mapper/ubuntu--vg-ubuntu--lv已经把那部分磁盘空间利用上了
df -h

#=================================================ubuntu磁盘扩容=============================================#

#=============================================ubuntu虚拟内存扩容=============================================#
#Fox-Edge-Server是JAVA程序，会占用比较多的内存，需要开启虚拟内存，保证内存空间的足够。
#目前测试，1000个设备，每个设备100个对象，也就是10W个数据对象，如果再配置触发器的话，接近需要4G内存空间
#推荐配置：4G物理内存，然后开启8G到10G虚拟内存，这样就可以有12G的内存空间供JAVA程序的消耗了。
#参考文章：https://www.ngui.cc/el/743554.html?action=onClick

#察看当前swap分区大小
free -h

#查看swap分区挂载位置，默认是/swap.img
cat /proc/swaps

#停止原来的交换分区
#注意：这要等一段时间
swapoff /swap.img

#删除原来的分区文件
rm /swap.img

#重新建立分区文件swapfile：我的物理内存是4G，所以这里准备新建的swap分区是4G，bs x count = 1024 × 4000000 = 4G
#注意：这要等一段时间，可能要十分钟，需要耐心等待
dd if=/dev/zero of=/swap.img bs=1024 count=4096000

#启用
chmod 600 /swap.img
mkswap -f /swap.img
swapon /swap.img

#检查结果
free -h
cat /proc/swaps

#=============================================ubuntu虚拟内存扩容=============================================#

#===============================================nginx安装任务================================================#
#nginx是前端web页面呈现的引擎
#参考文章:https://www.itcoder.tech/posts/how-to-install-nginx-on-ubuntu-20-04/

#更新仓库
apt update

#默认安装nginx
apt install nginx -y

#检查nginx的安装是否成功
systemctl status nginx

#配置：将fox-edge.com.ipadr.conf文件复制到\etc\nginx\conf.d

#重新装载配置
nginx -s reload

#将fox-edge的前端包dist复制到\opt\fox-edge\dist目录

#===============================================nginx安装任务================================================#

#===============================================ubuntu安装防火墙=============================================#
#参考文章：https://blog.csdn.net/carefree2005/article/details/120271903
#参考文章：https://www.bilibili.com/read/cv13051394

#更新仓库
apt update

#默认安装防火墙
apt install ufw

#版本查看
ufw version

#查看防火墙运行状态，此时默认是未激活状态的
#注意：此时千万别激活，否则你会发现全部都登录不上去了，因为防火墙默认的策略是"禁止所有入向，放行所有出向"
ufw status

#重要：首先要放开ssh端口和web端口和gateway端口，保证你在防火墙激活后，能够登录SSH和WEB
#在开发阶段，可以自己添加放开的指定端口
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 9000/tcp

#允许开发者的IP访问。自己的公网IP可以通过百度浏览器上搜索IP，百度会反射回你的公网IP给你
ufw allow from 120.230.0.1/16

#启动防火墙：此时，你会发现无法通过web访问工控机了，因为防火墙默认的策略是"禁止所有入向，放行所有出向"
ufw enable

#查看防火墙策略的编号
ufw status numbered

#删除指定的策略
#ufw delete 2

#禁止防火墙：开发调试阶段，可以先关闭防火墙
#ufw disable


#===============================================ubuntu安装防火墙=============================================#

#===============================================ubuntu定时任务===============================================#
#检查是否安装了cron
ps -aux|grep cron

#安装cron
#apt-get install cron

#1、进入编辑模式
crontab -e

#2、进入编辑：按ctrl+O，此时会出现File Name To Write: /temp/crontab.xxxxxx的提示，然后按回车键


#3、输入下面内容
#3.1 每天08:00执行一次该命令
0 8 * * * echo 3 > /proc/sys/vm/drop_caches
#3.2 定时同步时钟源
0 8 * * * ntpdate cn.pool.ntp.org

#4、保存编辑：按ctrl+X，此时会提示是否保存，然后选中Y

#5、检查编辑的定时任务，是否包含刚才的新增内容
crontab -l

#===============================================ubuntu定时任务===============================================#




#===============================================ubuntu设置时区===============================================#
#查看当前时区
date -R

#将时区设置为上海时区
timedatectl   set-timezone   Asia/Shanghai

#修改时间为24小时制
vim /etc/default/locale
#增加下面一行
LC_TIME=en_DK.UTF-8

#重启计算机
reboot

#===============================================ubuntu设置时区===============================================#


#=================================================安装SNMP服务===============================================#
#参考文章：https://blog.csdn.net/my_angle2016/article/details/124327128
#安装SNMP服务
apt-get install snmpd snmp snmp-mibs-downloader -y

#检查服务状态
service snmpd status


#本地测试一下
snmpwalk -v 2c -c public localhost 1.3.6.1.2.1.1.1
#应该会显示如下内容：iso.3.6.1.2.1.1.1.0 = STRING: "Linux server 5.15.0-60-generic #66-Ubuntu SMP Fri Jan 20 14:29:49 UTC 2023 x86_64"

#配置snmp可读内容
vim /etc/snmp/snmpd.conf
#修改内容如下：注释掉下面两行（在64行左右），并添加view systemonly include .1
#view   systemonly  included   .1.3.6.1.2.1.1
#view   systemonly  included   .1.3.6.1.2.1.25.1
view systemonly included .1
#允许远程访问：注释掉只允许本地访问的限制，大约在49行
#agentaddress  127.0.0.1


#重启服务
service snmpd restart

#本地测试一下:此时会显示一大批OID
snmpwalk -v 2c -c public localhost 1

#本地测试一下:此时会显示一个OID数据
snmpwalk -v 2c -c public localhost .1.3.6.1.4.1.2021.4.3.0

#=================================================安装SNMP服务===============================================#

#============================================非必须安装工具：go安装==========================================#
#参考文章：https://blog.csdn.net/Hexa_H/article/details/129802991
#go主要用来有个开发、测试、打包时，需要的xjar加密编译器，在生产环境中，不需要安装这个

#切换目录
cd /home

#下载安装包
wget -c https://dl.google.com/go/go1.20.3.linux-amd64.tar.gz

# 移动安装包到/usr/local目录
mv go1.20.3.linux-amd64.tar.gz /usr/local

#切换目录/usr/local
cd /usr/local

#解压安装
tar -zxf go1.20.3.linux-amd64.tar.gz

#配置环境变量：将两个export追加到末尾
vim /etc/bash.bashrc
#golang env config
#export GOROOT=/usr/local/go
#export PATH=$PATH:$GOROOT/bin:$GOPATH/bin

#使环境配置生效
source /etc/bash.bashrc

#检查配置效果
go version

#检查环境变量
go env

#设置代理环境变量
go env -w GOPROXY=https://goproxy.cn,direct
go env -w GOPRIVATE=git.mycompany.com,github.com/my/private

#============================================非必须安装工具：go安装==========================================#