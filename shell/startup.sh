#!/bin/bash

#系统参数
app_home=/opt/fox-edge


#启动核心进程
$app_home/shell/restart.sh kernel/gateway-service -p9000
$app_home/shell/restart.sh kernel/manager-service -p9101


