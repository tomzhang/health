
-- 患者
alter table `t_patient` Add column `top_path` varchar(100)  default 0 AFTER `telephone`;

-- 订单修改
alter table `p_order` drop column `check_status`;
alter table `p_order` drop column `close_status`;
alter table `p_order` drop column `pay_status`;
alter table `p_order` Add column `order_status` int not null default 1 comment '订单状态--1：待预约；2：待支付；3：已支付；4：已完成；5：已取消' AFTER `disease_id`;
alter table `p_order` Add column `time_long` int not null default 0 comment '套餐时间' AFTER `order_status`;
alter table `p_order` Add column `order_type` int not null default 1 comment '订单类型' AFTER `time_long`;



-- ----------------------------
-- 报到模块建表sql
-- ----------------------------

-- ----------------------------
-- Table structure for p_case
-- ----------------------------
DROP TABLE IF EXISTS `p_case`;
CREATE TABLE `p_case` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `check_in_id` int(11) NOT NULL DEFAULT '0' COMMENT '报到id',
  `patient_id` int(11) NOT NULL COMMENT '患者id',
  `patient_name` varchar(255) NOT NULL COMMENT '患者姓名',
  `sex` smallint(6) NOT NULL COMMENT '性别',
  `birthday` bigint(20) NOT NULL COMMENT '生日',
  `phone` varchar(11) NOT NULL COMMENT '手机',
  `hospital` varchar(255) DEFAULT NULL COMMENT '就诊医院',
  `record_num` varchar(255) DEFAULT NULL COMMENT '病例号',
  `last_cure_time` bigint(20) DEFAULT NULL COMMENT '上次就诊时间',
  `description` varchar(255) DEFAULT NULL COMMENT '医生诊断信息',
  `message` varchar(255) DEFAULT NULL COMMENT '留言',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 COMMENT='病例信息表';

-- ----------------------------
-- Table structure for p_check_in
-- ----------------------------
DROP TABLE IF EXISTS `p_check_in`;
CREATE TABLE `p_check_in` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `patient_id` int(11) NOT NULL COMMENT '患者id',
  `doctor_id` int(11) NOT NULL COMMENT '医生id',
  `order_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单id',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `status` int(11) NOT NULL COMMENT '状态（1：未处理，2：确认；3：忽略）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for p_cure_record
-- ----------------------------
DROP TABLE IF EXISTS `p_cure_record`;
CREATE TABLE `p_cure_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` int(11) NOT NULL COMMENT '订单id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `doctor_id` int(11) NOT NULL COMMENT '医生id',
  `patient_id` int(11) NOT NULL COMMENT '患者id',
  `createTime` bigint(20) NOT NULL COMMENT '创建时间',
  `description` text NOT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='诊疗记录表';

-- ----------------------------
-- Table structure for p_image_data
-- ----------------------------
DROP TABLE IF EXISTS `p_image_data`;
CREATE TABLE `p_image_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `relation_id` int(11) NOT NULL COMMENT '关联id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `image_url` varchar(255) NOT NULL COMMENT '图像路径',
  `image_type` int(11) NOT NULL COMMENT '图像类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='病例图像关联表';




------------------------------------
-- 诊疗记录修改
------------------------------------
ALTER TABLE `p_cure_record`
CHANGE COLUMN `createTime` `create_time`  bigint(20) NOT NULL COMMENT '创建时间' AFTER `patient_id`,
CHANGE COLUMN `description` `treat_advise`  varchar(0) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '治疗建议' AFTER `create_time`,
ADD COLUMN `drug_advise`  varchar(255) NULL COMMENT '用药建议' AFTER `treat_advise`,
ADD COLUMN `attention`  varchar(255) NULL COMMENT '注意事项' AFTER `drug_advise`;

ALTER TABLE `p_cure_record`
MODIFY COLUMN `treat_advise`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '治疗建议' AFTER `create_time`;

------------------------------------
-- 订单表删除套餐外键
------------------------------------
ALTER TABLE `p_order` DROP FOREIGN KEY `FK_Reference_9`;

ALTER TABLE `p_order`
DROP INDEX `FK_Reference_9`;

-- 增加上一次订单皇太
ALTER TABLE `p_order`
ADD COLUMN `pre_order_status`  int NULL DEFAULT NULL  COMMENT '上一个状态（订单状态）' AFTER `time_long`;
-- 增加取消人
ALTER TABLE `p_order`
ADD COLUMN `cancel_from`  int NULL DEFAULT NULL COMMENT '取消人（0 sys）' AFTER `pre_order_status`;


create table t_order_status_log
(
   id                   int not null auto_increment comment '主键',
   order_id             int comment '订单id',
   source_val           int comment '原值',
   val                  int comment '修改后的值',
   create_time          bigint comment '创建时间',
   primary key (id)
);

alter table t_order_status_log comment '订单状态修改记录';


-- 添加订单号字段
alter table `p_order` Add column `order_no` int not null default 0 comment '订单号' AFTER `cancel_from`;


-- 诊疗记录增加咨询建议
ALTER TABLE `p_cure_record` ADD COLUMN `consult_advise`  varchar(255) NULL COMMENT '咨询建议' AFTER `attention`;


ALTER TABLE `p_image_data`
ADD COLUMN `time_long`  bigint NULL COMMENT '时长' AFTER `image_type`;

ALTER TABLE `p_order` ADD COLUMN `remarks`  varchar(1000) NULL COMMENT '备注';
ALTER TABLE `p_order` ADD COLUMN `record_status`  int not null default 1 COMMENT '记录状态';

ALTER TABLE `p_cure_record` ADD COLUMN `consult_advise_diseases`  varchar(500) NULL COMMENT '咨询结果病种' AFTER `consult_advise`;

