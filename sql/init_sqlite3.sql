		CREATE TABLE IF NOT EXISTS "tb_channel" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"channel_name" VARCHAR(50) NOT NULL,
		"channel_type" VARCHAR(50) NOT NULL,
		"channel_param" TEXT NOT NULL,
		"extend_param" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_config" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"service_name" VARCHAR(50) NOT NULL,
		"service_type" VARCHAR(50) NOT NULL,
		"config_name" VARCHAR(50) NOT NULL,
		"config_value" TEXT NOT NULL,
		"config_param" TEXT NOT NULL,
		"remark" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_device" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"device_name" VARCHAR(50) NOT NULL,
		"device_type" VARCHAR(50) NOT NULL,
		"manufacturer" VARCHAR(50) NOT NULL,
		"channel_type" VARCHAR(50) NOT NULL,
		"channel_name" VARCHAR(50) NOT NULL,
		"device_param" TEXT NOT NULL,
		"extend_param" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_device_history" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"device_id" BIGINT NOT NULL,
		"object_name" VARCHAR(50) NOT NULL,
		"param_type" VARCHAR(32) NOT NULL,
		"param_value" VARCHAR(64) NOT NULL,
		"create_time" BIGINT NOT NULL
		);


		CREATE TABLE IF NOT EXISTS "tb_device_mapper" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"device_type" VARCHAR(50) NOT NULL,
		"manufacturer" VARCHAR(50) NOT NULL,
		"object_name" VARCHAR(50) NOT NULL,
		"mapper_name" VARCHAR(50) NOT NULL,
		"mapper_mode" INTEGER NULL,
		"value_type" VARCHAR(50) NOT NULL,
		"extend_param" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_device_model" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"model_name" VARCHAR(128) NULL,
		"device_type" VARCHAR(50) NOT NULL,
		"manufacturer" VARCHAR(50) NOT NULL,
		"model_param" TEXT  NOT NULL,
		"extend_param" TEXT  NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_device_object" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"device_name" VARCHAR(50) NOT NULL,
		"device_type" VARCHAR(50) NOT NULL,
		"manufacturer" VARCHAR(50) NOT NULL,
		"object_name" VARCHAR(50) NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_device_record" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"device_name" VARCHAR(50) NOT NULL,
		"device_type" VARCHAR(50) NOT NULL,
		"manufacturer" VARCHAR(50) NOT NULL,
		"record_name" VARCHAR(50) NOT NULL,
		"record_data" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_device_status" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"comm_time" BIGINT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);
		
		CREATE TABLE IF NOT EXISTS "tb_device_template" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"manufacturer" varchar(50) NOT NULL,
		"device_type" varchar(50) NOT NULL,
		"subset_name" varchar(50) NOT NULL,
		"template_type" varchar(50) NOT NULL,
		"template_name" varchar(50) NOT NULL,
		"template_param" TEXT NOT NULL,
		"extend_param" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);
		
		CREATE TABLE IF NOT EXISTS "tb_device_value_record" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"device_name" VARCHAR(50) NOT NULL,
		"device_type" VARCHAR(50) NOT NULL,
		"manufacturer" VARCHAR(50) NOT NULL,
		"device_value" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_device_value_task" (
  		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
  		"task_name" VARCHAR(50) NOT NULL,
  		"task_param" TEXT NOT NULL,
  		"create_time" BIGINT NOT NULL,
  		"update_time" BIGINT NOT NULL
		);
		
		CREATE TABLE IF NOT EXISTS "tb_extend" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"extend_name" VARCHAR(50) NOT NULL,
		"extend_type" VARCHAR(50) NOT NULL,
		"extend_param" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_iot_device_model" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"model_name" VARCHAR(50) NOT NULL,
		"model_type" VARCHAR(50) NOT NULL,
		"provider" VARCHAR(50) NOT NULL,
		"service_param" TEXT NOT NULL,
		"model_schema" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);
		
		CREATE TABLE IF NOT EXISTS "tb_iot_template" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"iot_name" varchar(50) NOT NULL,
		"subset_name" varchar(50) NOT NULL,
		"template_type" varchar(50) NOT NULL,
		"template_name" varchar(50) NOT NULL,
		"template_param" TEXT NOT NULL,
		"extend_param" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);
		
		CREATE TABLE IF NOT EXISTS "tb_link" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"link_name" VARCHAR(50) NOT NULL, 
		"link_type" VARCHAR(50) NOT NULL, 
		"link_param" TEXT NOT NULL,
		"extend_param" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_operate" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"operate_name" VARCHAR(50) NOT NULL,
		"device_type" VARCHAR(50) NOT NULL,
		"manufacturer" VARCHAR(50) NOT NULL,
		"engine_type" VARCHAR(50) NOT NULL,
		"service_type" VARCHAR(50) NOT NULL,
		"engine_param" TEXT NOT NULL,
		"extend_param" TEXT NOT NULL,
		"data_type" VARCHAR(50) NOT NULL,
		"operate_mode" VARCHAR(50) NOT NULL,
		"polling" INTEGER NULL,
		"timeout" INTEGER NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_operate_channel_task" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"task_name" VARCHAR(50) NOT NULL,
		"channel_name" VARCHAR(50) NOT NULL,
		"channel_type" VARCHAR(50) NOT NULL,
		"task_param" TEXT NOT NULL,
		"send_mode" VARCHAR(50) NOT NULL,
		"timeout" INTEGER(10) NOT NULL,
		"create_time" BIGINT  NOT NULL,
		"update_time" BIGINT  NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_operate_manual_task" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"task_name" VARCHAR(50) NOT NULL,
		"device_name" VARCHAR(50) NOT NULL,
		"device_type" VARCHAR(50) NOT NULL,
		"manufacturer" VARCHAR(50) NOT NULL,
		"task_param" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_operate_monitor_task" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"template_name" VARCHAR(50) NOT NULL,
		"device_type" VARCHAR(50) NOT NULL,
		"manufacturer" VARCHAR(50) NOT NULL,
		"template_param" TEXT NOT NULL,
		"device_ids" TEXT NOT NULL,
		"task_param" TEXT NOT NULL,
		"create_time" BIGINT  NOT NULL,
		"update_time" BIGINT  NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_operate_record" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"device_name" VARCHAR(50) NOT NULL,
		"device_type" VARCHAR(50) NOT NULL,
		"manufacturer" VARCHAR(50) NOT NULL,
		"record_name" VARCHAR(50) NOT NULL,
		"client_model" VARCHAR(50) NOT NULL,
		"operate_uuid" VARCHAR(50) NOT NULL,
		"record_param" TEXT NOT NULL,
		"record_data" TEXT NOT NULL,
		"create_time" BIGINT  NOT NULL,
		"update_time" BIGINT  NOT NULL
		);
		
		CREATE TABLE IF NOT EXISTS "tb_period_record" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"task_id" INTEGER NOT NULL,
		"record_batch" VARCHAR(50) NOT NULL,
		"device_id" INTEGER NOT NULL,
		"object_name" VARCHAR(50) NOT NULL,
		"object_value" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL
		);
		CREATE INDEX "idx_tb_period_record_task_id" ON "tb_period_record" (task_id);	
		CREATE INDEX "idx_tb_period_record_record_batch" ON "tb_period_record" (record_batch);	
		CREATE INDEX "idx_tb_period_record_device_id" ON "tb_period_record" (device_id);	
		CREATE INDEX "idx_tb_period_record_object_name" ON "tb_period_record" (object_name);	

		
		CREATE TABLE IF NOT EXISTS "tb_period_task" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"task_name" VARCHAR(50) NOT NULL,
		"device_type" VARCHAR(50) NOT NULL,
		"manufacturer" VARCHAR(50) NOT NULL,
		"task_param" TEXT NOT NULL,
		"select_device" INTEGER NOT NULL,
		"device_ids" TEXT NOT NULL,
		"object_ids" TEXT NOT NULL,
		"create_time" BIGINT  NOT NULL,
		"update_time" BIGINT  NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_probe" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"device_name" VARCHAR(50) NOT NULL,
		"device_type" VARCHAR(50) NOT NULL,
		"manufacturer" VARCHAR(50) NOT NULL,
		"operate_name" VARCHAR(50) NOT NULL,
		"operate_param" TEXT NOT NULL,
		"operate_period" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_repo_comp" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"comp_repo" VARCHAR(50) NOT NULL,
		"comp_type" VARCHAR(50) NOT NULL,
		"comp_name" VARCHAR(256) NOT NULL,
		"comp_param" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);
		

		CREATE TABLE IF NOT EXISTS "tb_user" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"username" VARCHAR(50) NOT NULL,
		"password" VARCHAR(128) NOT NULL,
		"user_type" VARCHAR(50) NOT NULL,
		"role" VARCHAR(50) NOT NULL,
		"permission" VARCHAR(50) NOT NULL,
		"menu" VARCHAR(50) NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_user_menu" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"name" VARCHAR(50) NOT NULL,
		"menu" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_user_permission" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"name" VARCHAR(50) NOT NULL,
		"permission" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		CREATE TABLE IF NOT EXISTS "tb_user_role" (
		"id" INTEGER PRIMARY KEY AUTOINCREMENT,
		"name" VARCHAR(50) NOT NULL,
		"role" TEXT NOT NULL,
		"create_time" BIGINT NOT NULL,
		"update_time" BIGINT NOT NULL
		);

		INSERT INTO "tb_user" ("id", "username", "password", "user_type", "role", "permission", "menu", "create_time", "update_time") VALUES (1, 'admin', '$2a$10$CoUy1K.W3WjxeeV91FJzEu7u2IQTQKm31lgsJHbvD3Lc2EPNXCDOy', 'system', 'ADMIN', 'ADMIN', 'ADMIN', 1651806258856, 1651831121752);
		INSERT INTO "tb_user" ("id", "username", "password", "user_type", "role", "permission", "menu", "create_time", "update_time") VALUES (2, 'system', '', 'user', 'SYSTEM', 'ADMIN', 'ADMIN', 1651806258856, 1730204711673);
		REPLACE INTO "tb_user_menu" ("id", "name", "menu", "create_time", "update_time") VALUES (1, 'ADMIN', '[{"meta": {"icon": "template-manage", "roles": ["ADMIN"], "title": "任务管理", "hidden": false, "keepAlive": true, "alwaysShow": true}, "path": "/task", "children": [{"meta": {"icon": "operate-monitor-task", "roles": ["ADMIN"], "title": "设备监控任务", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "operate-monitor-task", "path": "operate-monitor-task", "component": "system/device/operate/task/monitor/index"}, {"meta": {"icon": "operate-manual-task", "roles": ["ADMIN"], "title": "设备操作任务", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "operate-manual-task", "path": "operate-manual-task", "component": "system/device/operate/task/manual/index"}, {"meta": {"icon": "operate-channel-task", "roles": ["ADMIN"], "title": "通道操作任务", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "operate-channel-task", "path": "operate-channel-task", "component": "system/device/operate/task/channel/index"}], "redirect": "/system/device", "component": "Layout"}, {"meta": {"icon": "system", "roles": ["ADMIN"], "title": "系统管理", "hidden": false, "keepAlive": true, "alwaysShow": true}, "path": "/system", "children": [{"meta": {"icon": "system-config", "roles": ["ADMIN"], "title": "系统参数", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "config", "path": "config", "component": "system/config/index"}, {"meta": {"icon": "system-exchange", "roles": ["ADMIN"], "title": "系统变量", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "exchange", "path": "exchange", "component": "system/exchange/index"}, {"meta": {"icon": "user", "roles": ["ADMIN"], "title": "用户管理", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "user", "path": "user", "component": "system/user/index"}, {"meta": {"icon": "process-status", "roles": ["ADMIN"], "title": "服务模块", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "process-status", "path": "process-status", "component": "system/service/process/index"}, {"meta": {"icon": "console", "roles": ["ADMIN"], "title": "前台日志", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "console", "path": "console", "component": "system/console/index"}], "redirect": "/system/device", "component": "Layout"}, {"meta": {"icon": "repo-comp-manage", "roles": ["ADMIN"], "title": "组件仓库", "hidden": false, "keepAlive": true, "alwaysShow": true}, "path": "/repo-comp/cloud", "children": [{"meta": {"icon": "repo-comp-service", "roles": ["ADMIN"], "title": "服务模块", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "cloud/service", "path": "repo-comp-service", "component": "system/repo-comp/cloud/service/index"}, {"meta": {"icon": "repo-comp-jar-decoder", "roles": ["ADMIN"], "title": "静态解码", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "cloud/jar-decoder", "path": "jar-decoder", "component": "system/repo-comp/cloud/jar-decoder/index"}, {"meta": {"icon": "repo-comp-jsp-decoder", "roles": ["ADMIN"], "title": "动态解码", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "cloud/jsp-decoder/comp-list", "path": "jsp-decoder/comp-list", "component": "system/repo-comp/cloud/jsp-decoder/comp-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "版本列表", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/jsp-decoder/version-list", "path": "jsp-decoder/version-list", "component": "system/repo-comp/cloud/jsp-decoder/version-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "操作列表", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/jsp-decoder/operate-list", "path": "jsp-decoder/operate-list", "component": "system/repo-comp/cloud/jsp-decoder/operate-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "操作详情", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/jsp-decoder/version-info", "path": "jsp-decoder/version-info", "component": "system/repo-comp/cloud/jsp-decoder/operate-info/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "动态模板", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "cloud/jsn-decoder/comp-list", "path": "jsn-decoder/comp-list", "component": "system/repo-comp/cloud/jsn-decoder/comp-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "版本列表", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/jsn-decoder/version-list", "path": "jsn-decoder/version-list", "component": "system/repo-comp/cloud/jsn-decoder/version-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "操作列表", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/jsn-decoder/object-list", "path": "jsn-decoder/object-list", "component": "system/repo-comp/cloud/jsn-decoder/object-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "操作详情", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/jsn-decoder/version-info", "path": "jsn-decoder/version-info", "component": "system/repo-comp/cloud/jsn-decoder/object-info/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "设备模板", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "cloud/dev-template/comp-list", "path": "dev-template/comp-list", "component": "system/repo-comp/cloud/dev-template/comp-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "版本列表", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/dev-template/version-list", "path": "dev-template/version-list", "component": "system/repo-comp/cloud/dev-template/version-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "操作列表", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/dev-template/object-list", "path": "dev-template/object-list", "component": "system/repo-comp/cloud/dev-template/object-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "操作详情", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/dev-template/version-info", "path": "dev-template/version-info", "component": "system/repo-comp/cloud/dev-template/object-info/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "北向模板", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "cloud/iot-template/comp-list", "path": "iot-template/comp-list", "component": "system/repo-comp/cloud/iot-template/comp-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "版本列表", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/iot-template/version-list", "path": "iot-template/version-list", "component": "system/repo-comp/cloud/iot-template/version-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "操作列表", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/iot-template/object-list", "path": "iot-template/object-list", "component": "system/repo-comp/cloud/iot-template/object-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "操作详情", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/iot-template/version-info", "path": "iot-template/version-info", "component": "system/repo-comp/cloud/iot-template/object-info/index"}, {"meta": {"icon": "repo-comp-webpack", "roles": ["ADMIN"], "title": "前端模块", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "cloud/repo-comp-webpack", "path": "repo-comp-webpack", "component": "system/repo-comp/cloud/webpack/index"}, {"meta": {"icon": "repo-comp-product", "roles": ["ADMIN"], "title": "适配产品", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/repo-comp-product", "path": "repo-comp-product", "component": "system/repo-comp/cloud/product/index"}, {"meta": {"icon": "repo-comp-product-detail", "roles": ["ADMIN"], "title": "产品详情", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "cloud/repo-comp-product-detail", "path": "repo-comp-product-detail", "component": "system/repo-comp/cloud/product/detail/index"}], "redirect": "/system/repo-comp", "component": "Layout"}, {"meta": {"icon": "repo-comp-manage", "roles": ["ADMIN"], "title": "本地组件", "hidden": false, "keepAlive": true, "alwaysShow": true}, "path": "/repo-comp/local", "children": [{"meta": {"icon": "repo-comp-jar-decoder", "roles": ["ADMIN"], "title": "服务模块", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "local/app-service/comp-list", "path": "app-service/comp-list", "component": "system/repo-comp/local/app-service/comp-list/index"}, {"meta": {"icon": "repo-comp-jar-decoder", "roles": ["ADMIN"], "title": "静态解码", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "local/jar-decoder/comp-list", "path": "jar-decoder/comp-list", "component": "system/repo-comp/local/jar-decoder/comp-list/index"}, {"meta": {"icon": "repo-comp-jar-decoder", "roles": ["ADMIN"], "title": "操作列表", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "local/jar-decoder/operate-list", "path": "jar-decoder/operate-list", "component": "system/repo-comp/local/jar-decoder/operate-list/index"}, {"meta": {"icon": "repo-comp-jsp-decoder", "roles": ["ADMIN"], "title": "动态解码", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "local/jsp-decoder/comp-list", "path": "jsp-decoder/comp-list", "component": "system/repo-comp/local/jsp-decoder/comp-list/index"}, {"meta": {"icon": "repo-comp-jsp-decoder", "roles": ["ADMIN"], "title": "操作列表", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "local/jsp-decoder/operate-list", "path": "jsp-decoder/operate-list", "component": "system/repo-comp/local/jsp-decoder/operate-list/index"}, {"meta": {"icon": "repo-comp-decoder", "roles": ["ADMIN"], "title": "操作详情", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "local/jsp-decoder/version-info", "path": "jsp-decoder/version-info", "component": "system/repo-comp/local/jsp-decoder/operate-info/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "设备模板", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "local/dev-template/comp-list", "path": "dev-template/comp-list", "component": "system/repo-comp/local/dev-template/comp-list/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "模板分类", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "local/dev-template/model-list", "path": "dev-template/model-list", "component": "system/repo-comp/local/dev-template/model-list/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "设备参数", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "local/dev-template/device-list", "path": "dev-template/device-list", "component": "system/repo-comp/local/dev-template/device-list/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "操作参数", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "local/dev-template/operate-list", "path": "dev-template/operate-list", "component": "system/repo-comp/local/dev-template/operate-list/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "通道参数", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "local/dev-template/channel-list", "path": "dev-template/channel-list", "component": "system/repo-comp/local/dev-template/channel-list/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "动态模板", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "local/jsn-decoder/comp-list", "path": "jsn-decoder/comp-list", "component": "system/repo-comp/local/jsn-decoder/comp-list/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "模型列表", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "local/jsn-decoder/model-list", "path": "jsn-decoder/model-list", "component": "system/repo-comp/local/jsn-decoder/model-list/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "北向模型", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "local/iot-template/comp-list", "path": "iot-template/comp-list", "component": "system/repo-comp/local/iot-template/comp-list/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "模型分类", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "local/iot-template/model-list", "path": "iot-template/model-list", "component": "system/repo-comp/local/iot-template/model-list/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "设备模型", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "local/iot-template/device-list", "path": "iot-template/device-list", "component": "system/repo-comp/local/iot-template/device-list/index"}, {"meta": {"icon": "repo-comp-jsn-decoder", "roles": ["ADMIN"], "title": "操作模型", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "local/iot-template/operate-list", "path": "iot-template/operate-list", "component": "system/repo-comp/local/iot-template/operate-list/index"}], "redirect": "/system/repo-comp-local", "component": "Layout"}, {"meta": {"icon": "device-manage", "roles": ["ADMIN"], "title": "设备管理", "hidden": false, "keepAlive": true, "alwaysShow": true}, "path": "/device", "children": [{"meta": {"icon": "device-list", "roles": ["ADMIN"], "title": "设备列表", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "device", "path": "device", "component": "system/device/entity/index"}, {"meta": {"icon": "device-value", "roles": ["ADMIN"], "title": "设备数值", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "device-value", "path": "device-value", "component": "system/device/value/device-value/index"}, {"meta": {"icon": "device-mapper", "roles": ["ADMIN"], "title": "设备映射", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "device-mapper", "path": "device-mapper", "component": "system/device/mapper/index"}, {"meta": {"icon": "extend", "roles": ["ADMIN"], "title": "扩展信息", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "device-extend", "path": "device-extend", "component": "system/extend/index"}, {"meta": {"icon": "device-status", "roles": ["ADMIN"], "title": "通信状态", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "device-status", "path": "device-status", "component": "system/device/status/index"}], "redirect": "/system/device", "component": "Layout"}, {"meta": {"icon": "channel-manage", "roles": ["ADMIN"], "title": "通道管理", "hidden": false, "keepAlive": true, "alwaysShow": true}, "path": "/channel", "children": [{"meta": {"icon": "channel", "roles": ["ADMIN"], "title": "通道列表", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "channel-list", "path": "channel-list", "component": "system/channel/channel-list/index"}, {"meta": {"icon": "channel", "roles": ["ADMIN"], "title": "通道状态", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "channel-status", "path": "channel-status", "component": "system/channel/channel-status/index"}], "redirect": "/system/channel", "component": "Layout"}, {"meta": {"icon": "model-manage", "roles": ["ADMIN"], "title": "北向模型", "hidden": false, "keepAlive": true, "alwaysShow": true}, "path": "/model", "children": [{"meta": {"icon": "device-model", "roles": ["ADMIN"], "title": "设备模型", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "device-model", "path": "device-model", "component": "system/device/model/index"}], "redirect": "/system/device/model", "component": "Layout"}, {"meta": {"icon": "device-valuex-manage", "roles": ["ADMIN"], "title": "数值计算", "hidden": false, "keepAlive": true, "alwaysShow": true}, "path": "/device-valuex", "children": [{"meta": {"icon": "device-valuex-task", "roles": ["ADMIN"], "title": "计算任务", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "device-valuex-task", "path": "device-valuex-task", "component": "system/device/value/device-valuex/task/index"}, {"meta": {"icon": "device-valuex-script", "roles": ["ADMIN"], "title": "脚本详情", "hidden": true, "keepAlive": true, "alwaysShow": false}, "name": "device-valuex-script", "path": "device-valuex-script", "component": "system/device/value/device-valuex/script/index"}, {"meta": {"icon": "device-valuex-value", "roles": ["ADMIN"], "title": "计算结果", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "device-valuex-value", "path": "device-valuex-value", "component": "system/device/value/device-valuex/value/index"}], "redirect": "/system/device", "component": "Layout"}, {"meta": {"icon": "period-manage", "roles": ["ADMIN"], "title": "周期管理", "hidden": false, "keepAlive": true, "alwaysShow": true}, "path": "/period", "children": [{"meta": {"icon": "period-task", "roles": ["ADMIN"], "title": "周期任务", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "period-task", "path": "period-task", "component": "system/period/task/index"}, {"meta": {"icon": "period-record", "roles": ["ADMIN"], "title": "周期记录", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "period-record", "path": "period-record", "component": "system/period/record/index"}], "redirect": "/system/device", "component": "Layout"}, {"meta": {"icon": "record-manage", "roles": ["ADMIN"], "title": "记录管理", "hidden": false, "keepAlive": true, "alwaysShow": true}, "path": "/record", "children": [{"meta": {"icon": "device-record", "roles": ["ADMIN"], "title": "设备记录", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "device-record", "path": "device-record", "component": "system/device/record/index"}, {"meta": {"icon": "device-value-record", "roles": ["ADMIN"], "title": "数值记录", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "device-value-record", "path": "device-value-record", "component": "system/device/value/record/index"}, {"meta": {"icon": "operate-record", "roles": ["ADMIN"], "title": "操作记录", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "operate-record", "path": "operate-record", "component": "system/device/operate/record/index"}, {"meta": {"icon": "history-record", "roles": ["ADMIN"], "title": "历史记录", "hidden": false, "keepAlive": true, "alwaysShow": false}, "name": "device-history", "path": "device-history", "component": "system/device/history/index"}], "redirect": "/system/device", "component": "Layout"}, {"meta": {"icon": "link", "roles": ["ADMIN"], "title": "官方网站", "hidden": false, "keepAlive": true, "alwaysShow": true}, "path": "/website-link", "children": [{"meta": {"icon": "link", "roles": ["ADMIN"], "title": "官方网站", "hidden": false, "keepAlive": true, "alwaysShow": false}, "path": "http://www.fox-tech.cn"}, {"meta": {"icon": "link", "roles": ["ADMIN"], "title": "官方文档", "hidden": false, "keepAlive": true, "alwaysShow": false}, "path": "http://docs.fox-tech.cn"}, {"meta": {"icon": "link", "roles": ["ADMIN"], "title": "官方源码", "hidden": false, "keepAlive": true, "alwaysShow": false}, "path": "https://gitee.com/fierce_wolf"}, {"meta": {"icon": "link", "roles": ["ADMIN"], "title": "Fox-Cloud", "hidden": false, "keepAlive": true, "alwaysShow": false}, "path": "http://cloud.fox-tech.cn"}], "redirect": "noredirect", "component": "Layout"}]', 1651831121752, 1651831121752);
		INSERT INTO "tb_user_permission" ("id", "name", "permission", "create_time", "update_time") VALUES (1, 'ADMIN', '["sys:user:view", "sys:user:edit", "sys:user:add", "sys:user:delete"]', 1651831121752, 1651831121752);
		INSERT INTO "tb_user_role" ("id", "name", "role", "create_time", "update_time") VALUES (1, 'ADMIN', '["ADMIN"]', 1651831121752, 1651831121752);
