#!/bin/bash

#系统目录
shell_path=$(cd `dirname $0`; pwd)
app_home=${shell_path%/*}

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


#启动核心进程
if [[ $app_env_kernel == compose ]]; then	
	$app_home/shell/restart.sh kernel/manager-compose -p9000
elif [[ $app_env_kernel == native ]]; then	
	$app_home/shell/restart.sh kernel/gateway-native -p9000
	$app_home/shell/restart.sh kernel/manager-native -p9101
else
	$app_home/shell/restart.sh kernel/gateway-service -p9000
	$app_home/shell/restart.sh kernel/manager-service -p9101	
fi




