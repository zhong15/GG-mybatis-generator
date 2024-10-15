ALTER TABLE `t_user` add `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除，0：否，1：是' AFTER update_time;
