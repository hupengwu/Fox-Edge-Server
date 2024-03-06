#执行命令：mysql -u root -p12345678;
#> source /opt/fox-edge/sql/init.sql;

#切换数据库
use mysql;

#查看用户权限
select host, user, plugin from user;

#创建用户:'admin'@'%'
drop user if exists 'admin'@'%';
create user 'admin'@'%' identified by '12345678';
grant all privileges on *.* to 'admin'@'%';
flush privileges;


#创建数据库fox_edge（注意，职能使用下划线，否则下面的授权会掉坑里去）
use mysql;
CREATE DATABASE IF NOT EXISTS `fox_edge`;

#查看用户权限
use mysql;
select host, user, plugin from user;

#创建本地连接用户:'fox-edge'@'localhost'
drop user if exists 'fox-edge'@'localhost', 'fox-edge'@'%';
create user 'fox-edge'@'localhost' identified by '12345678';
grant all privileges on fox_edge.* to 'fox-edge'@'localhost';
flush privileges;

#创建远程连接用户:'fox-edge'@'%'
drop user if exists 'fox-edge'@'%', 'fox-edge'@'%';
create user 'fox-edge'@'%' identified by '12345678';
grant all privileges on fox_edge.* to 'fox-edge'@'%';
flush privileges;


#刷入数据库脚本
source /opt/fox-edge/sql/init_table.sql;
source /opt/fox-edge/sql/ini_data.sql;

exit