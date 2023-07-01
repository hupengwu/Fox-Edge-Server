#!/bin/bash
#定义进程启动命令中的唯一性特征
APP_FEATURES=java

psCmd=`ps -ef|grep $APP_FEATURES|grep -v grep|awk '{print $2}'` 
if [ -z "$psCmd" ]; then  
  exit 1
fi


for pid in $psCmd
do
	exitCmd=`ps -ef|grep $pid|grep -v grep|awk '{print $2}'` 
	isnum=`echo $exitCmd |egrep ^[0-9]+$`
	if [ "$isnum"x != ""x ]; then 
        jmap -histo:live $pid | head -10
	fi
done



