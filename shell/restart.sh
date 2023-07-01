#!/bin/bash 

#命令行说明： 
#    参数1：模块/业务，例如 system/controller-service
#    参数2：带-p的服务端口或者-d的调试端口，例如 -p9001 -d192.168.3.133:5005
#命令行范例：
#./restart.sh system/controller-service -p9001 -d192.168.3.133:5005

#======================================================命令行参数=================================================#
#用户参数，例如：system/controller-service
service_name=${1}
service_port1=${2}
service_port2=${3}
#======================================================命令行参数=================================================#
 
#=============================================配置文件的读写函数定义区============================================#
#读取INI文件的通用函数：$(readINI 文件名 section名 item名)
#范例：
#     value=$(readINI fox-edge.ini 段名1 Key2)
#     读取fox-edge.ini的配置文件，文件中有个叫'段名1'的section，在这个section里有个叫Key2的item，然后将这个作为变量数值赋值给value变量
function readINI()
{
 FILENAME=$1; SECTION=$2; KEY=$3
 RESULT=`awk -F '=' '/\['$SECTION'\]/{a=1}a==1&&$1~/'$KEY'/{print $2;exit}' $FILENAME`
 echo $RESULT
} 
#=============================================配置文件的读写函数定义区============================================#

#=============================================读取fox-edge.ini的全局配置==========================================#
#系统目录
app_home=/opt/fox-edge

#redis参数
app_param_redis_host=$(readINI $app_home/shell/fox-edge.ini redis host)
app_param_redis_port=$(readINI $app_home/shell/fox-edge.ini redis port)
app_param_redis_password=$(readINI $app_home/shell/fox-edge.ini redis password)
#mysql参数
app_param_mysql_host=$(readINI $app_home/shell/fox-edge.ini mysql host)
app_param_mysql_username=$(readINI $app_home/shell/fox-edge.ini mysql username)
app_param_mysql_password=$(readINI $app_home/shell/fox-edge.ini mysql password)
#绑定的本地IP
app_local_ip=$(readINI $app_home/shell/fox-edge.ini environment ip)

#=============================================读取fox-edge.ini的全局配置==========================================#

#=============================================读取service.ini的业务配置===========================================#
#背景：采用sorce读取，是因为spring_param可能包含非shell格式允许的"="参数，spring参数基本上都是跟shell冲突的
source $app_home/shell/$service_name/service.conf
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


#==========================================提取调试参数d 和端口参数p =============================================#
#提取参数：参数的预处理，将无序的参数，根据前缀p和d，初始化serverPort、debugPort
#命令范例：./restart.sh service/channel-iec104-master.sh -p9091 -d192.168.3.133:5005
funcValue=
funcParam()
{
	#检查：参数是否输入
    if [ -z "$1" ]; then 
		exit
    fi
	
	#检查：参数的前缀是否为p
	str=$1
	if [ ${str:0:2} = "-p" ]; then  		
		funcValue=${str:2}
		return 0
	fi
	
	#检查：参数的前缀是否为d
	if [ ${str:0:2} = "-d" ]; then  		
		funcValue=${str:2}
		return 0
	fi
}

#提取参数1
str=$service_port1
if [ -n "$str" ] && [ ${str:0:2} = "-p" ]; then  
  funcParam $str
  serverPort=$funcValue
fi
if [ -n "$str" ] && [ ${str:0:2} = "-d" ]; then  
  funcParam $str
  debugPort=$funcValue
fi


#提取参数2
str=$service_port2
if [ -n "$str" ] && [ ${str:0:2} = "-p" ]; then  
  funcParam $str
  serverPort=$funcValue
fi
if [ -n "$str" ] && [ ${str:0:2} = "-d" ]; then  
  funcParam $str
  debugPort=$funcValue
fi

#==========================================提取调试参数d 和端口参数p =============================================#


#=======================================生成变量server_port和dubeg_param==========================================#
#如果配置了serverPort
if [ -n "$serverPort" ]; then  
  #如果用户输入了端口参数，那么生成--server.port=###的配置参数
  server_port=--server.port=$serverPort 
else
  #如果用户没有输入了端口参数，那么生成个空参数，也就是默认使用jar自己预制的端口参数
  server_port= 
fi

#如果配置了debugPort
if [ -n "$debugPort" ]; then  
  #如果用户输入了端口参数，那么生成--server.port=###的配置参数
  dubeg_param=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=$debugPort 
else
  #如果用户没有输入了端口参数，那么生成个空参数，也就是默认使用jar自己预制的端口参数
  dubeg_param= 
fi
#=======================================生成变量server_port和dubeg_param==========================================#

#=============================================授权loader的可执行权限 =============================================#
#如果配置了serverPort
if [ -n "$loader_name" ]; then  
  # 给启动器设置启动权限
  chmod 755 $app_home/bin/$app_type/$app_name/$loader_name
fi

#=============================================授权loader的可执行权限 =============================================#


#=========================================组织命令行参数，并重启业务进程==========================================#
#杀死进程
ids=`ps -ef | grep $app_home/bin/$app_type/$app_name/$jar_name | grep -v 'grep' | awk '{print $2}'`
for id in $ids
do
    kill -9 $id
done

#设置当前工作目录
cd $app_home


#启动进程
if [ -n "$loader_name" ]; then  
	#启动loader java的命令
	nohup \
	$app_home/bin/$app_type/$app_name/$loader_name \
	java \
	$dubeg_param \
	--add-opens java.base/jdk.internal.loader=ALL-UNNAMED  \
	-jar \
	$app_home/bin/$app_type/$app_name/$jar_name \
	--app_name=$app_name \
	-Dspring.profiles.active=prod \
	$spring_param \
	$server_port \
	--spring.redis.host=$app_param_redis_host --spring.redis.port=$app_param_redis_port --spring.redis.password=$app_param_redis_password \
	--spring.datasource.username=$app_param_mysql_username --spring.datasource.password=$app_param_mysql_password  --spring.datasource.url=jdbc:mysql://$app_param_mysql_host:3306/fox_edge \
	>$app_home/logs/start_$jar_name.out 2>&1 & 
 
else
	#启动java的命令
	nohup \
	java -jar \
	$dubeg_param \
	$app_home/bin/$app_type/$app_name/$jar_name \
	--app_name=$app_name \
	-Dspring.profiles.active=prod \
	$spring_param \
	$server_port \
	--spring.redis.host=$app_param_redis_host --spring.redis.port=$app_param_redis_port --spring.redis.password=$app_param_redis_password \
	--spring.datasource.username=$app_param_mysql_username --spring.datasource.password=$app_param_mysql_password  --spring.datasource.url=jdbc:mysql://$app_param_mysql_host:3306/fox_edge \
	 >$app_home/logs/start_$jar_name.out 2>&1 & \
fi

#=========================================组织命令行参数，并重启业务进程==========================================#
