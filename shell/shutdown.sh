#!/bin/bash
#定义进程启动命令中的唯一性特征

#系统目录
shell_path=$(cd `dirname $0`; pwd)
app_home=${shell_path%/*}

function killApp()
{
	#特征值
	feature=$1;
	
	#生成查询进程的命令行
	shell=`ps -ef|grep $feature|grep -v grep|awk '{print $2}'`
	
	#执行该命令行，获得这些进程的ID列表
	result=$shell
	
	#判定结果：然后kill这些进程ID
	if [[ -n ${result:1:1} ]]; then 
		kill -9 $shell
	fi
}

result=$(killApp ${app_home}/bin )



