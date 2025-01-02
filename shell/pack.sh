#!/bin/bash

shell_path=$(cd `dirname $0`; pwd)
app_home=${shell_path%/*}

#系统参数
cd /opt
rm -rf temp
mkdir -p temp/fox-edge
cd temp/fox-edge
workdir=/opt/temp/fox-edge

#=============================================配置文件的读写函数定义区============================================#
#读取INI文件的通用函数：$(readINI 文件名 section名 item名)
#范例：
#     value=$(readINI fox-edge.ini 段名1 Key2)
#     读取fox-edge.ini的配置文件，文件中有个叫'段名1'的section，在这个section里有个叫Key2的item，然后将这个作为变量数值赋值给value变量
function readINI()
{
 FILENAME=$1; SECTION=$2; KEY=$3
 RESULT=`awk -F '=' '/\['$SECTION'\]/{a=1}a==1&&$1~/'$KEY'/{print $2;exit}' $FILENAME`
 echo $RESULT
} 
#=============================================配置文件的读写函数定义区============================================#

#环境变量
app_env_kernel=$(readINI $app_home/shell/fox-edge.ini environment kernel)

#=====================================================打包文件目录================================================#

#复制二级目录
cpdir=bin/kernel
mkdir -p $cpdir
if [[ $app_env_kernel == compose ]]; then
	cp -r $app_home/$cpdir/manager-compose bin/kernel/manager-compose
elif [[ $app_env_kernel == native ]]; then
	cp -r $app_home/$cpdir/gateway-native bin/kernel/gateway-native
	cp -r $app_home/$cpdir/manager-native bin/kernel/manager-native
else
	cp -r $app_home/$cpdir/gateway-service bin/kernel/gateway-service
	cp -r $app_home/$cpdir/manager-service bin/kernel/manager-service
fi


#复制一级目录并清理service和system目录
cpdir=shell
mkdir -p $cpdir
cp -r $app_home/$cpdir .
rm -rf shell/kernel
rm -rf shell/system
rm -rf shell/service
rm -rf shell/logs
mkdir -p shell/kernel

if [[ $app_env_kernel == compose ]]; then
	cp -r $app_home/$cpdir/kernel/manager-compose shell/kernel
elif [[ $app_env_kernel == native ]]; then	
	cp -r $app_home/$cpdir/kernel/gateway-native shell/kernel
	cp -r $app_home/$cpdir/kernel/manager-native shell/kernel	
else
	cp -r $app_home/$cpdir/kernel/gateway-service shell/kernel
	cp -r $app_home/$cpdir/kernel/manager-service shell/kernel
fi

#复制一级目录
cpdir=conf
mkdir -p $cpdir
if [[ $app_env_kernel == compose ]]; then
	mkdir -p conf/kernel/manager-compose
	cp -r $app_home/$cpdir/kernel/manager-compose conf/kernel
elif [[ $app_env_kernel == native ]]; then	
	mkdir -p conf/kernel/manager-native	
	cp -r $app_home/$cpdir/kernel/manager-native conf/kernel
fi

#复制一级目录
cpdir=dist
mkdir -p $cpdir
cp -r $app_home/$cpdir .

#复制一级目录
cpdir=doc
mkdir -p $cpdir
cp -r $app_home/$cpdir .

#复制一级目录
cpdir=jar
mkdir -p $cpdir
#cp -r $app_home/$cpdir .

#创建一级目录
cpdir=logs
mkdir -p $cpdir

#复制一目录
cpdir=repository
mkdir -p $cpdir
cp -r $app_home/$cpdir/notice.txt $cpdir

#复制二目录
cpdir=repository/decoder
mkdir -p $cpdir
cp -r $app_home/$cpdir/decoderList.jsn $cpdir

#创建目录并复制文件
#cpdir=repository/service
#mkdir -p $cpdir
#cp -r $app_home/$cpdir/serviceList.jsn $cpdir


#复制一级目录
cpdir=sql
mkdir -p $cpdir
cp -r $app_home/$cpdir .

#复制一级目录
cpdir=data
mkdir -p $cpdir
#cp -r $app_home/$cpdir .

#复制一级目录
cpdir=nacos
mkdir -p $cpdir
cp -r $app_home/$cpdir .

#复制一级目录
cpdir=template
mkdir -p $cpdir
cp -r $app_home/$cpdir .

#==============================================================================#
#打包为tar.gz包
cd /opt/temp
tar -czvf fox-edge.tar.gz fox-edge

#回到原来的shell目录
cd $app_home/shell

#解压命令
#tar -xzvf  fox-edge.tar.gz
#mv fox-edge /opt

