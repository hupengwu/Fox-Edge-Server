#!/bin/bash

shell_path=$(cd `dirname $0`; pwd)
app_home=${shell_path%/*}

#切换当前目录
cd $app_home

#开启启动脚本

#1.将minio.service复制到系统目录/etc/systemd/system/
/bin/cp -rf $app_home/shell/fox-edge.service /etc/systemd/system/fox-edge.service

#2.将文件从dos格式转换为linux格式
dos2unix  /etc/systemd/system/fox-edge.service  >/dev/null 2>&1
dos2unix  $app_home/shell/startup.sh  >/dev/null 2>&1
dos2unix  $app_home/shell/shutdown.sh  >/dev/null 2>&1
#3.重新装载/etc/systemd/system/fox-edge.service到/lib/systemd/system/fox-edge.service
systemctl daemon-reload
#4.新增执行权限
chmod +x $app_home/shell/startup.sh
chmod +x $app_home/shell/shutdown.sh
chmod +x $app_home/bin/kernel/gateway-service/fox-edge-server-gateway-service-1.0.0.loader
chmod +x $app_home/bin/kernel/manager-service/fox-edge-server-manager-system-service-1.0.0.loader

#5.停止服务脚本
systemctl stop fox-edge.service
#6.启动服务脚本
systemctl start fox-edge.service
#7.将服务脚本配置问开机启动
systemctl enable fox-edge.service
#8.显示minio信息
ps -aux|grep $app_home/bin

