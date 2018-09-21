alter table `p_order` Add column `group_id` varchar(25)  comment '医生集团ID' AFTER `doctor_id`;

alter table `t_order_session` Add column `treat_begin_time` bigint(20)  comment '医生叫号时间' ;