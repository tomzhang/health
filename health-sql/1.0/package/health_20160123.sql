drop table if exists t_doctor_income;

/*==============================================================*/
/* Table: t_doctor_income                 医生收入表                    */
/*==============================================================*/
CREATE TABLE `t_doctor_income` (
`id`  int NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`doctor_id`  int NULL COMMENT '医生id' ,
`order_id`  int NULL COMMENT '订单id' ,
`order_income`  double NULL COMMENT '订单收入' ,
`share_income`  double NULL COMMENT '订单分成收入' ,
`actual_income`  double NULL COMMENT '实际收入' ,
`division_doctor_prop`  int NULL COMMENT '上级医生分成比例' ,
`division_group_prop`  int NULL COMMENT '集团分成比例' ,
`division_sys_prop`  int NULL COMMENT '平台分成比例' ,
`division_doctor_id`  int NULL COMMENT '上级分成医生id' ,
`division_group_id`  varchar(50) NULL COMMENT '集团id' ,
`settle_status`  varchar(50) NULL COMMENT '结算状态，0=未结算，1=已结算' ,
`order_status`  varchar(50) NULL COMMENT '订单状态，3=已付款，4=已完成' ,
`remark`  varchar(100) NULL COMMENT '备注' ,
`create_time`  bigint NULL COMMENT '创建时间' ,
`complete_time`  bigint NULL COMMENT '完成时间' ,
`settle_time`  bigint NULL COMMENT '结算时间' ,
`extend_1`  varchar(100) NULL COMMENT '备用字段1' ,
`extend_2`  varchar(100) NULL COMMENT '备用字段2' ,
PRIMARY KEY (`id`)
)
;

drop table if exists t_doctor_division;

/*==============================================================*/
/* Table: t_doctor_division                      上级医生提成表              */
/*==============================================================*/
CREATE TABLE `t_doctor_division` (
`id`  int NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`group_id`  varchar(50) NULL COMMENT '集团id' ,
`division_doctor_id`  int NULL COMMENT '提成医生id' ,
`order_id`  int NULL COMMENT '订单id' ,
`income_id`  int NULL COMMENT '收入id' ,
`income_doctor_id`  int NULL COMMENT '收入医生id' ,
`division_income`  double NULL COMMENT '上级提成收入' ,
`settle_status`  varchar(50) NULL COMMENT '结算状态，0=未结算，1=已结算' ,
`create_time`  bigint NULL COMMENT '创建时间' ,
`settle_time`  bigint NULL COMMENT '结算时间' ,
`extend_1`  varchar(100) NULL COMMENT '备用字段1' ,
`extend_2`  varchar(100) NULL COMMENT '备用字段2' ,
PRIMARY KEY (`id`)
)
;


drop table if exists t_group_division;

/*==============================================================*/
/* Table: t_group_division                           集团提成表          */
/*==============================================================*/

CREATE TABLE `t_group_division` (
`id`  int NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`group_id`  varchar(50) NULL COMMENT '集团id' ,
`order_id`  int NULL COMMENT '订单id' ,
`income_id`  int NULL COMMENT '收入id' ,
`income_doctor_id`  int NULL COMMENT '收入医生id' ,
`division_income`  double NULL COMMENT '集团提成收入' ,
`settle_status`  varchar(50) NULL COMMENT '结算状态，0=未结算，1=已结算' ,
`create_time`  bigint NULL COMMENT '创建时间' ,
`settle_time`  bigint NULL COMMENT '结算时间' ,
`extend_1`  varchar(100) NULL COMMENT '备用字段1' ,
`extend_2`  varchar(100) NULL COMMENT '备用字段2' ,
PRIMARY KEY (`id`)
)
;

/*==============================================================*/
/* 更新 Table: t_income_settle                   新增字段type 标记收入类型          */
/*==============================================================*/
ALTER TABLE `t_income_settle`
ADD COLUMN `type`  varchar(50) NULL COMMENT '关联的收入类型：di=医生收入，dv=医生提成，gv=集团提成' AFTER `create_time`;


create table `t_settle_sys` (
	`id` int (11),
	`name` varchar (60),
	`settleMoney` bigint (20),
	`unSettleMoney` bigint (20),
	`userType` int (11),
	`settle_time` bigint (20),
	`create_time` bigint (20),
	PRIMARY KEY (`id`)
); 
create table `t_settle_sys_user` (
	`id` int (11),
	`sys_id` int (11),
	`user_id` int (11),
	`create_time` bigint (20),
	PRIMARY KEY (`id`)
); 
