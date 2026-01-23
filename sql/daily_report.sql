CREATE TABLE `daily_report` (
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     int unsigned DEFAULT '1' COMMENT '用户ID（单用户固定为1）',
    `report_date` date NOT NULL COMMENT '日报日期',
    `week`        varchar(3)   DEFAULT NULL COMMENT '星期几',
    `content`     text NOT NULL COMMENT '工作内容',
    `remark`      varchar(200) DEFAULT '' COMMENT '备注',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`,`report_date`) COMMENT '用户+日期唯一约束'
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日报表';