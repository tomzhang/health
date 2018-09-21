alter table p_pack add column is_searched int(11) DEFAULT '0' COMMENT '针对会诊套餐（该医生是否可被搜索）';
alter table p_pack modify column description text ;

alter table p_pack add column help_times int DEFAULT 0 COMMENT '求助次数';
alter table p_order add column help_times int DEFAULT 0 COMMENT '求助次数';
alter table p_pack_doctor add column receive_remind tinyint DEFAULT 0 COMMENT '是否接收提醒';
alter table p_order_doctor add column receive_remind tinyint DEFAULT 0 COMMENT '是否接收提醒';