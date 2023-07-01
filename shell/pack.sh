#!/bin/bash

#系统参数
cd /opt
rm -rf temp
mkdir -p temp/fox-edge
cd temp/fox-edge
workdir=/opt/temp/fox-edge

#==============================================================================#

#复制二级目录
cpdir=bin/kernel
mkdir -p $cpdir
cp -r /opt/fox-edge/$cpdir bin


#复制一级目录
cpdir=conf
mkdir -p $cpdir
cp -r /opt/fox-edge/$cpdir .

#复制一级目录
cpdir=dist
mkdir -p $cpdir
cp -r /opt/fox-edge/$cpdir .

#复制一级目录
cpdir=doc
mkdir -p $cpdir
cp -r /opt/fox-edge/$cpdir .

#复制一级目录
cpdir=jar
mkdir -p $cpdir
cp -r /opt/fox-edge/$cpdir .

#创建一级目录
cpdir=logs
mkdir -p $cpdir

#复制二目录
cpdir=repository/decoder
mkdir -p $cpdir
cp -r /opt/fox-edge/$cpdir repository

#创建目录并复制文件
cpdir=repository/service
mkdir -p $cpdir
cp -r /opt/fox-edge/$cpdir/serviceList.jsn $cpdir

#复制一级目录并清理service和system目录
cpdir=shell
mkdir -p $cpdir
cp -r /opt/fox-edge/$cpdir .
rm -rf shell/service
rm -rf shell/system

#复制一级目录
cpdir=sql
mkdir -p $cpdir
cp -r /opt/fox-edge/$cpdir .

#复制一级目录
cpdir=template
mkdir -p $cpdir
cp -r /opt/fox-edge/$cpdir .

#==============================================================================#
#打包为tar.gz包
cd /opt/temp
tar -czvf fox-edge.tar.gz fox-edge

#回到原来的shell目录
cd /opt/fox-edge/shell

#解压命令
#tar -xzvf  fox-edge.tar.gz
#mv fox-edge /opt

