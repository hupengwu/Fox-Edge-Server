#!/bin/bash 

#系统目录
shell_path=$(cd `dirname $0`; pwd)
app_home=${shell_path%/*}

#命令行范例：
#./upgrade.sh kernel manager-service 1.0.0 master 9000

#======================================================命令行参数=================================================#
#用户参数，例如：./upgrade.sh kernel manager-service 1.0.0 master 9000
component=${1}
model_name=${2}
model_version=${3}
version=${4}
stage=${5}
server_port=${6}

if [ -z ${component:1:1} ]; then 
	echo "缺少component参数！范例：./upgrade.sh kernel manager-service v1 1.0.0 master 9000"
	exit
fi

if [ -z ${model_name:1:1} ]; then 
	echo "缺少model_name参数！范例：./upgrade.sh kernel manager-service v1 1.0.0 master 9000"
	exit
fi

if [ -z ${model_version:1:1} ]; then 
	echo "缺少model_version参数！范例：./upgrade.sh kernel manager-service v1 1.0.0 master 9000"
	exit
fi

if [ -z ${version:1:1} ]; then 
	echo "缺少version参数！范例：./upgrade.sh kernel manager-service v1 1.0.0 master 9000"
	exit
fi

if [ -z ${stage:1:1} ]; then 
	echo "缺少stage参数！范例：./upgrade.sh kernel manager-service v1 1.0.0 master 9000"
	exit
fi

if [ -z ${server_port:1:1} ]; then 
	echo "缺少server_port参数！范例：./upgrade.sh kernel manager-service v1 1.0.0 master 9000"
	exit
fi


#=============================================读取service.ini的业务配置===========================================#
#背景：采用sorce读取，是因为spring_param可能包含非shell格式允许的"="参数，spring参数基本上都是跟shell冲突的
source $app_home/repository/service/$model_name/$model_version/$version/$stage/$component/tar/shell/$component/$model_name/service.conf

#从配置文件中读取配置项
app_engine=$appEngine
app_type=$appType
app_name=$appName
#java的参数
jar_name=$jarName
loader_name=$loaderName
spring_param=$springParam
#python的参数
py_name=$pyName
py_param=$pyParam


#检查：配置参数是否读取成功
if [ -z ${app_type:1:1} ]; then 
	echo "读取$app_home/shell/$service_name/service.conf的appType配置参数失败！"
	exit
fi
if [ -z ${app_name:1:1} ]; then 
	echo "读取$app_home/shell/$service_name/service.conf的appName配置参数失败！"
	exit
fi

#检测：如果 app_engine没填写，那么就默认为java
if [ -z ${app_engine:1:1} ]; then 
	app_engine=java
fi

#检测：是否匹配了对应的程序文件参数
if [[ $app_engine == java ]]; then
	if [ -z ${jar_name:1:1} ]; then 
		echo "读取$app_home/shell/$service_name/service.conf的jarName配置参数失败！"
		exit
	fi
elif [[ $app_engine == python3 ]]; then
	if [ -z ${py_name:1:1} ]; then 
		echo "读取$app_home/shell/$service_name/service.conf的pyName配置参数失败！"
		exit
	fi
else
	echo "读取$app_home/shell/$service_name/service.conf的jarName配置参数失败：不支持的appEngine：$app_engine"
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
	cp -rf $app_home/repository/service/$model_name/$model_version/$version/$stage/$component/tar/bin/$component/$model_name/$jar_name $app_home/bin/$component/$model_name
	chmod 755 $app_home/bin/$component/$model_name/$jar_name
fi

#复制py:if [[]],双中括号，处理空格的问题:python的项目，是多个py文件组成的，所以要复制目录
if [[ -n ${py_name:1:1} ]]; then 
	#删除旧的目录
	rm -rf $app_home/bin/$component/$model_name
	#复制新的目录
	cp -rf $app_home/repository/service/$model_name/$model_version/$version/$stage/$component/tar/bin/$component/$model_name $app_home/bin/$component
fi
 
#复制loader:if [[]],双中括号，处理空格的问题
if [[ -n ${loader_name:1:1} ]]; then 
	cp -rf $app_home/repository/service/$model_name/$model_version/$version/$stage/$component/tar/bin/$component/$model_name/$loader_name $app_home/bin/$component/$model_name
	chmod 755 $app_home/bin/$component/$model_name/$loader_name
fi

#创建conf目录
mkdir -p $app_home/conf
#复制conf目录
cp -rf $app_home/repository/service/$model_name/$model_version/$version/$stage/$component/tar/conf $app_home

#创建shell目录
mkdir -p $app_home/shell/$component/$model_name
#复制service.conf文件
cp -f $app_home/repository/service/$model_name/$model_version/$version/$stage/$component/tar/shell/$component/$model_name/service.conf $app_home/shell/$component/$model_name

#重启服务
$app_home/shell/restart.sh $component/$model_name -p$server_port

#======================================================执行更新命令=================================================#