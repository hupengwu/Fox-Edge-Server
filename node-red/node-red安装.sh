#下载指定的nodejs的18版
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -

#本地安装
apt-get install -y nodejs

#检查是否安装成功
node -v
npm -v
npx -v

# 切换为国内的源（比如华为源），主要是国外的太慢了，容易因为超时而安装不成功
npm config set registry https://mirrors.huaweicloud.com/repository/npm

# 安装nodered，安装时间比较长，需要等待一会
npm install -g --unsafe-perm node-red

# node-red启动
node-red


#卸载node-red
npm uninstall -g node-red

#查找残留配置：默认安装时候，本地会残留/root/.node-red
find / -name 'node-red'

#删除本地残留文件
rm -rf /root/.node-red