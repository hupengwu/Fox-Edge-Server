#!/bin/bash 

#命令行范例：
#./upgrade.sh kernel manager-service 1.0.0 9000

#======================================================命令行参数=================================================#
#用户参数，例如：./upgrade.sh kernel manager-service 1.0.0 9000
component=${1}
model_name=${2}
version=${3}
server_port=${4}

if [ -z ${component:1:1} ]; then 
	echo "缺少component参数！范例：./upgrade.sh kernel manager-service 1.0.0 9000"
	exit
fi

if [ -z ${model_name:1:1} ]; then 
	echo "缺少model_name参数！范例：./upgrade.sh kernel manager-service 1.0.0 9000"
	exit
fi

if [ -z ${version:1:1} ]; then 
	echo "缺少version参数！范例：./upgrade.sh kernel manager-service 1.0.0 9000"
	exit
fi

if [ -z ${server_port:1:1} ]; then 
	echo "缺少server_port参数！范例：./upgrade.sh kernel manager-service 1.0.0 9000"
	exit
fi

#系统目录
app_home=/opt/fox-edge

#=============================================读取service.ini的业务配置===========================================#
#背景：采用sorce读取，是因为spring_param可能包含非shell格式允许的"="参数，spring参数基本上都是跟shell冲突的
source $app_home/repository/service/$model_name/$version/$component/tar/shell/$component/$model_name/service.conf

#从配置文件中读取配置项
app_type=$appType
app_name=$appName
jar_name=$jarName
loader_name=$loaderName
spring_param=$springParam


#检查：配置参数是否读取成功
if [ -z ${app_type:1:1} ]; then 
	echo "读取$app_home/shell/$service_name/service.conf的appType配置参数失败！"
	exit
fi
if [ -z ${app_name:1:1} ]; then 
	echo "读取$app_home/shell/$service_name/service.conf的appName配置参数失败！"
	exit
fi
if [ -z ${jar_name:1:1} ]; then 
	echo "读取$app_home/shell/$service_name/service.conf的jarName配置参数失败！"
	exit
fi

#=============================================读取service.ini的业务配置===========================================#

#======================================================执行更新命令=================================================#

#切换目录
cd $app_home

#创建目录
mkdir -p $app_home/bin/$component/$model_name

#复制jar:if [[]],双中括号，处理空格的问题
if [[ -n ${jar_name:1:1} ]]; then 
	cp -rf $app_home/repository/service/$model_name/$version/$component/tar/bin/$component/$model_name/$jar_name $app_home/bin/$component/$model_name
	chmod 755 $app_home/bin/$component/$model_name/$jar_name
fi
 
#复制loader:if [[]],双中括号，处理空格的问题
if [[ -n ${loader_name:1:1} ]]; then 
	cp -rf $app_home/repository/service/$model_name/$version/$component/tar/bin/$component/$model_name/$loader_name $app_home/bin/$component/$model_name
	chmod 755 $app_home/bin/$component/$model_name/$loader_name
fi

#创建conf目录
mkdir -p $app_home/conf
#复制conf目录
cp -rf $app_home/repository/service/$model_name/$version/$component/tar/conf $app_home

#创建shell目录
mkdir -p $app_home/shell/$component/$model_name
#复制service.conf文件
cp -f $app_home/repository/service/$model_name/$version/$component/tar/shell/$component/$model_name/service.conf $app_home/shell/$component/$model_name

#重启服务
$app_home/shell/restart.sh $component/$model_name -p$server_port

#======================================================执行更新命令=================================================#