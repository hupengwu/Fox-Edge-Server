#!/bin/bash

#系统目录
shell_path=$(cd `dirname $0`; pwd)
app_home=${shell_path%/*}

#启动核心进程
$app_home/shell/restart.sh kernel/gateway-service -p9000
$app_home/shell/restart.sh kernel/manager-service -p9101


