CREATE TABLE `t_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(50) NOT NULL COMMENT '电子邮箱',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `is_enable` tinyint(4) NOT NULL COMMENT '是否可用，0：否，1：是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_nickname` (`nickname`),
  KEY `idx_email_password` (`email`,`password`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `t_order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `state` int(10) unsigned NOT NULL COMMENT '订单状态',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
  `address` varchar(50) DEFAULT NULL COMMENT '地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除，0：否，1：是',
  PRIMARY KEY (`id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
