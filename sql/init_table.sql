-- --------------------------------------------------------
-- 主机:                           192.168.3.133
-- 服务器版本:                        8.0.33-0ubuntu0.22.04.2 - (Ubuntu)
-- 服务器操作系统:                      Linux
-- HeidiSQL 版本:                  12.2.0.6576
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- 导出 fox_edge 的数据库结构
DROP DATABASE IF EXISTS `fox_edge`;
CREATE DATABASE IF NOT EXISTS `fox_edge`;
USE `fox_edge`;

-- 导出  表 fox_edge.tb_channel 结构
DROP TABLE IF EXISTS `tb_channel`;
CREATE TABLE IF NOT EXISTS `tb_channel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `channel_name` varchar(50) DEFAULT NULL COMMENT '通道名称',
  `channel_type` varchar(50) DEFAULT NULL COMMENT '通道类型',
  `channel_param` json DEFAULT NULL COMMENT '通道参数(JSON)',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `channel_name_channel_type` (`channel_name`,`channel_type`),
  KEY `channel_name` (`channel_name`),
  KEY `channel_type` (`channel_type`)
) ENGINE=InnoDB AUTO_INCREMENT=48;

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_config 结构
DROP TABLE IF EXISTS `tb_config`;
CREATE TABLE IF NOT EXISTS `tb_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `service_name` varchar(50) DEFAULT NULL COMMENT '服务名称（消费者）',
  `service_type` varchar(50) DEFAULT NULL COMMENT '服务类型（消费者）',
  `config_name` varchar(50) DEFAULT NULL COMMENT '配置名称（消费者）',
  `config_value` json DEFAULT NULL COMMENT '数值',
  `config_param` json DEFAULT NULL COMMENT '参数',
  `remark` text COMMENT '描述',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `service_name_service_type_config_name` (`service_name`,`service_type`,`config_name`)
) ENGINE=InnoDB AUTO_INCREMENT=60 COMMENT='该参数是由manage服务来配置，再给各个服务消费';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_device 结构
DROP TABLE IF EXISTS `tb_device`;
CREATE TABLE IF NOT EXISTS `tb_device` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `device_name` varchar(50) DEFAULT NULL COMMENT '设备名称',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备类型',
  `channel_type` varchar(50) DEFAULT NULL COMMENT '通道类型',
  `channel_name` varchar(50) DEFAULT NULL COMMENT '通道名称',
  `device_param` json DEFAULT NULL COMMENT '配置参数',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `device_name` (`device_name`),
  KEY `device_type` (`device_type`)
) ENGINE=InnoDB AUTO_INCREMENT=2210;

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_device_history 结构
DROP TABLE IF EXISTS `tb_device_history`;
CREATE TABLE IF NOT EXISTS `tb_device_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主表ID',
  `device_id` bigint NOT NULL DEFAULT '0' COMMENT '设备ID',
  `object_name` varchar(128) NOT NULL DEFAULT '' COMMENT '对象名称',
  `param_type` varchar(32) DEFAULT NULL COMMENT '参数类型',
  `param_value` varchar(64) DEFAULT NULL COMMENT '参数值',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `device_id` (`device_id`),
  KEY `object_name` (`object_name`)
) ENGINE=InnoDB AUTO_INCREMENT=31796845;

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_device_mapper 结构
DROP TABLE IF EXISTS `tb_device_mapper`;
CREATE TABLE IF NOT EXISTS `tb_device_mapper` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备型号',
  `object_name` varchar(128) DEFAULT NULL COMMENT '对象名称',
  `mapper_name` varchar(128) DEFAULT NULL COMMENT '对象重命名',
  `mapper_mode` int DEFAULT NULL COMMENT '映射方式（0：不进行处理，1：替换，3：副本，3：剔除）',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `device_type_object_name` (`device_type`,`object_name`)
) ENGINE=InnoDB AUTO_INCREMENT=628281 COMMENT='对象重命名';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_device_object 结构
DROP TABLE IF EXISTS `tb_device_object`;
CREATE TABLE IF NOT EXISTS `tb_device_object` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `device_name` varchar(50) DEFAULT NULL COMMENT '设备名称',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备型号',
  `object_name` varchar(128) DEFAULT NULL COMMENT '对象名称',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `device_name_object_name` (`device_name`,`object_name`),
  KEY `device_type` (`device_type`),
  KEY `device_name` (`device_name`),
  KEY `object_name` (`object_name`)
) ENGINE=InnoDB AUTO_INCREMENT=822854 COMMENT='设备的一个个数据对象信息';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_device_record 结构
DROP TABLE IF EXISTS `tb_device_record`;
CREATE TABLE IF NOT EXISTS `tb_device_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `device_name` varchar(50) DEFAULT NULL COMMENT '设备名称',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备类型',
  `record_name` varchar(50) DEFAULT NULL COMMENT '事件名称',
  `record_data` json DEFAULT NULL COMMENT '事件数据（解码器自定义JSON格式）',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `device_name` (`device_name`) USING BTREE,
  KEY `device_type` (`device_type`) USING BTREE,
  KEY `event_name` (`record_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=235;

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_device_status 结构
DROP TABLE IF EXISTS `tb_device_status`;
CREATE TABLE IF NOT EXISTS `tb_device_status` (
  `id` bigint NOT NULL COMMENT '序号',
  `comm_time` bigint DEFAULT NULL COMMENT '通信时间',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB;

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_operate 结构
DROP TABLE IF EXISTS `tb_operate`;
CREATE TABLE IF NOT EXISTS `tb_operate` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `device_type` varchar(50) NOT NULL COMMENT '设备类型',
  `operate_name` varchar(50) NOT NULL COMMENT '操作名称',
  `operate_mode` varchar(16) NOT NULL COMMENT '操作模式',
  `manufacturer` varchar(50) NOT NULL,
  `data_type` varchar(8) NOT NULL COMMENT '数据类型（状态/记录）',
  `polling` int NOT NULL COMMENT '是否轮询',
  `timeout` int NOT NULL COMMENT '通信超时',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `device_type_operate_name` (`device_type`,`operate_name`)
) ENGINE=InnoDB AUTO_INCREMENT=690;

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_operate_channel_task 结构
DROP TABLE IF EXISTS `tb_operate_channel_task`;
CREATE TABLE IF NOT EXISTS `tb_operate_channel_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `task_name` varchar(50) DEFAULT NULL COMMENT '实例名称',
  `channel_name` varchar(50) DEFAULT NULL COMMENT '通道名称',
  `channel_type` varchar(50) DEFAULT NULL COMMENT '通道类型',
  `task_param` json DEFAULT NULL COMMENT '操作参数',
  `send_mode` varchar(50) DEFAULT NULL COMMENT '发送模式',
  `timeout` int DEFAULT NULL COMMENT '通信超时',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `task_name` (`task_name`),
  KEY `channel_name` (`channel_name`),
  KEY `channel_type` (`channel_type`)
) ENGINE=InnoDB AUTO_INCREMENT=19 COMMENT='操作实例：用户手动操作设备时，提交的任务';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_operate_manual_task 结构
DROP TABLE IF EXISTS `tb_operate_manual_task`;
CREATE TABLE IF NOT EXISTS `tb_operate_manual_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `task_name` varchar(50) DEFAULT NULL COMMENT '实例名称',
  `device_name` varchar(50) DEFAULT NULL COMMENT '设备名称',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备类型',
  `task_param` json DEFAULT NULL COMMENT '操作参数',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `task_name` (`task_name`),
  KEY `device_name` (`device_name`),
  KEY `device_type` (`device_type`)
) ENGINE=InnoDB AUTO_INCREMENT=34 COMMENT='操作实例：用户手动操作设备时，提交的任务';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_operate_monitor_task 结构
DROP TABLE IF EXISTS `tb_operate_monitor_task`;
CREATE TABLE IF NOT EXISTS `tb_operate_monitor_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `template_name` varchar(50) DEFAULT NULL COMMENT '模板名称',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备类型',
  `template_param` json DEFAULT NULL COMMENT '模板参数',
  `device_ids` json DEFAULT NULL COMMENT '设备列表',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `template_name` (`template_name`),
  KEY `device_type` (`device_type`)
) ENGINE=InnoDB AUTO_INCREMENT=19 COMMENT='控制器监控模板，控制器会以操作参数为缺省参数，自动填入。当设备参数有同名参数的时候，设备参数覆盖相同的数据。';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_operate_record 结构
DROP TABLE IF EXISTS `tb_operate_record`;
CREATE TABLE IF NOT EXISTS `tb_operate_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `device_name` varchar(50) DEFAULT NULL COMMENT '设备名称',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备类型',
  `record_name` varchar(50) DEFAULT NULL COMMENT '操作名称',
  `client_model` varchar(50) DEFAULT NULL COMMENT '客户端模块',
  `operate_uuid` varchar(50) DEFAULT NULL COMMENT '会话的uuid',
  `record_param` json DEFAULT NULL COMMENT '事件参数（解码器自定义JSON格式）',
  `record_data` json DEFAULT NULL COMMENT '事件数据（解码器自定义JSON格式）',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `device_name` (`device_name`) USING BTREE,
  KEY `device_type` (`device_type`) USING BTREE,
  KEY `event_name` (`record_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=23245;

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_param_template 结构
DROP TABLE IF EXISTS `tb_param_template`;
CREATE TABLE IF NOT EXISTS `tb_param_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `template_name` varchar(50) DEFAULT NULL COMMENT '模板名称',
  `template_type` varchar(50) DEFAULT NULL COMMENT '模板类型（提供给谁使用）',
  `template_param` json DEFAULT NULL COMMENT '设备参数（JSON格式）',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `device_name` (`template_name`) USING BTREE,
  KEY `device_type` (`template_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 COMMENT='为用户界面配置tb_device.device_param和tb_operate_monitor_task.operate_param时候，提供直接复制的工具';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_period_record 结构
DROP TABLE IF EXISTS `tb_period_record`;
CREATE TABLE IF NOT EXISTS `tb_period_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `task_id` bigint DEFAULT NULL COMMENT '任务名称',
  `record_batch` varchar(50) DEFAULT NULL COMMENT '记录批次',
  `device_id` bigint DEFAULT NULL COMMENT '设备ID',
  `object_name` varchar(50) DEFAULT NULL COMMENT '对象名称',
  `object_value` json DEFAULT NULL COMMENT '对象数值',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `task_id` (`task_id`) USING BTREE,
  KEY `device_id` (`device_id`) USING BTREE,
  KEY `object_name` (`object_name`) USING BTREE,
  KEY `record_batch` (`record_batch`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=200173381 COMMENT='设备数值的周期记录';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_period_task 结构
DROP TABLE IF EXISTS `tb_period_task`;
CREATE TABLE IF NOT EXISTS `tb_period_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_name` varchar(50) DEFAULT NULL COMMENT '任务名称',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备类型',
  `task_param` json DEFAULT NULL COMMENT '任务参数（如何工作的信息）',
  `select_device` int DEFAULT NULL COMMENT '指定设备',
  `device_ids` json DEFAULT NULL COMMENT '设备列表',
  `object_ids` json DEFAULT NULL COMMENT '对象列表',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `device_name` (`task_name`) USING BTREE,
  KEY `device_type` (`device_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35 COMMENT='周期记录任务';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_probe 结构
DROP TABLE IF EXISTS `tb_probe`;
CREATE TABLE IF NOT EXISTS `tb_probe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `device_name` varchar(50) DEFAULT NULL COMMENT '设备名称',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备型号',
  `operate_name` varchar(50) DEFAULT NULL COMMENT '操作名称',
  `operate_param` text COMMENT '操作参数（JSON格式）',
  `operate_period` text COMMENT '持续周期（JSON格式）',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `device_name` (`device_name`),
  KEY `device_type` (`device_type`),
  KEY `operate_name` (`operate_name`)
) ENGINE=InnoDB AUTO_INCREMENT=433544 COMMENT='设备探针：用于对某些对象进行实时性监控的临时性任务。';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_trigger 结构
DROP TABLE IF EXISTS `tb_trigger`;
CREATE TABLE IF NOT EXISTS `tb_trigger` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `model_name` varchar(64) DEFAULT NULL COMMENT '触发器模块',
  `method_name` varchar(64) DEFAULT NULL COMMENT '触发器的名称',
  `manufacturer` varchar(64) DEFAULT NULL COMMENT '开发者',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `model_name_method_name_manufacturer` (`model_name`,`method_name`,`manufacturer`)
) ENGINE=InnoDB AUTO_INCREMENT=5 COMMENT='判定告警的逻辑代码，这是告警框架扫描触发器代码，而自动生成的';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_trigger_config 结构
DROP TABLE IF EXISTS `tb_trigger_config`;
CREATE TABLE IF NOT EXISTS `tb_trigger_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `trigger_config_name` varchar(50) NOT NULL COMMENT '配置名称',
  `object_range` varchar(50) NOT NULL COMMENT '告警范围(全局/设备/对象)',
  `device_name` varchar(50) DEFAULT NULL COMMENT '设备名称',
  `device_type` varchar(50) NOT NULL COMMENT '设备类型',
  `objects_name` json NOT NULL COMMENT '对象列表',
  `trigger_model_name` varchar(64) NOT NULL COMMENT '触发器模块名称',
  `trigger_method_name` varchar(64) NOT NULL COMMENT '触发器方法名称',
  `queue_deep` int NOT NULL DEFAULT '1' COMMENT '队列深度',
  `trigger_param` json NOT NULL COMMENT '触发器参数',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=436375 COMMENT='设备告警配置';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_trigger_object 结构
DROP TABLE IF EXISTS `tb_trigger_object`;
CREATE TABLE IF NOT EXISTS `tb_trigger_object` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `device_name` varchar(50) DEFAULT NULL COMMENT '设备名称',
  `device_type` varchar(50) NOT NULL COMMENT '设备类型',
  `trigger_config_name` varchar(50) NOT NULL COMMENT '配置名称',
  `object_name` varchar(128) NOT NULL COMMENT '告警范围(全局/设备/对象)',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `device_name_trigger_config_name_object_name` (`device_name`,`trigger_config_name`,`object_name`),
  KEY `device_type` (`device_type`),
  KEY `device_name` (`device_name`),
  KEY `trigger_config_name` (`trigger_config_name`),
  KEY `object_name` (`object_name`)
) ENGINE=InnoDB AUTO_INCREMENT=635870 COMMENT='触发值对象';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_user 结构
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE IF NOT EXISTS `tb_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名称',
  `password` varchar(128) DEFAULT NULL COMMENT '用户密码',
  `user_type` varchar(50) DEFAULT NULL COMMENT '用户类型',
  `role` varchar(50) DEFAULT NULL COMMENT '角色信息',
  `permission` varchar(50) DEFAULT NULL COMMENT '权限信息',
  `menu` varchar(50) DEFAULT NULL COMMENT '菜单信息',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=17;

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_user_menu 结构
DROP TABLE IF EXISTS `tb_user_menu`;
CREATE TABLE IF NOT EXISTS `tb_user_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `name` varchar(50) DEFAULT NULL COMMENT '模板名称',
  `menu` json DEFAULT NULL COMMENT '菜单信息',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 COMMENT='菜单模板，预定义了某类用户的菜单信息。用户表可以根据名称引用该模板，作为自己的界面菜单信息';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_user_permission 结构
DROP TABLE IF EXISTS `tb_user_permission`;
CREATE TABLE IF NOT EXISTS `tb_user_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `name` varchar(50) DEFAULT NULL COMMENT '模板名称',
  `permission` json DEFAULT NULL COMMENT '权限信息',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 COMMENT='后台权限模板，预定义了某类用户的权限信息。用户表可以根据名称引用该模板，作为自己的权限信息';

-- 数据导出被取消选择。

-- 导出  表 fox_edge.tb_user_role 结构
DROP TABLE IF EXISTS `tb_user_role`;
CREATE TABLE IF NOT EXISTS `tb_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `name` varchar(50) DEFAULT NULL COMMENT '模板名称',
  `role` json DEFAULT NULL COMMENT '角色信息',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 COMMENT='后台角色模板，预定义了某类用户的后台信息。用户表可以根据名称引用该模板，作为自己的后台信息';

-- 数据导出被取消选择。

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
