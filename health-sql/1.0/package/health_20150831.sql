/*
Navicat MySQL Data Transfer

Source Server         : 7
Source Server Version : 50625
Source Host           : 192.168.3.7:3306
Source Database       : health

Target Server Type    : MYSQL
Target Server Version : 50625
File Encoding         : 65001

Date: 2015-08-31 11:05:36
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for p_order
-- ----------------------------
DROP TABLE IF EXISTS `p_order`;
CREATE TABLE `p_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '购买用户id',
  `doctor_id` int(11) NOT NULL COMMENT '医生id',
  `pack_type` int(11) NOT NULL COMMENT '套餐类型',
  `pack_id` int(11) NOT NULL COMMENT '套餐id',
  `price` bigint(20) NOT NULL COMMENT '价格',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `finish_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '完成时间',
  `patient_id` int(11) NOT NULL COMMENT '病人id',
  `disease_id` int(11) NOT NULL COMMENT '病情id',
  `check_status` int(11) NOT NULL DEFAULT '1' COMMENT '审核状态--1：未审核；2：审核通过；3：审核不通过；',
  `close_status` int(11) NOT NULL DEFAULT '1' COMMENT '关闭状态--1：未关闭；2：系统关闭；3：用户关闭',
  `pay_status` int(11) NOT NULL DEFAULT '1' COMMENT '支付状态--1：未支付；2：支付成功；',
  `refund_status` int(11) NOT NULL DEFAULT '1' COMMENT '退款状态--1：未申请退款；2：申请退款；3：退款成功；4：退款失败',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=386 DEFAULT CHARSET=utf8 COMMENT='套餐订单表';

-- ----------------------------
-- Table structure for p_pack
-- ----------------------------
DROP TABLE IF EXISTS `p_pack`;
CREATE TABLE `p_pack` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `doctor_id` int(11) NOT NULL COMMENT '医生id',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `price` bigint(20) NOT NULL COMMENT '以分为单位',
  `pack_type` int(11) NOT NULL COMMENT '套餐类型',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `status` int(11) NOT NULL COMMENT '套餐状态--1：开通，2：关闭',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
   `time_limit` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=171 DEFAULT CHARSET=utf8 COMMENT='服务套餐主表';



-- ----------------------------
-- Table structure for t_account
-- ----------------------------
DROP TABLE IF EXISTS `t_account`;
CREATE TABLE `t_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `usable_money` bigint(20) NOT NULL DEFAULT '0' COMMENT '可用余额',
  `frozen_money` bigint(20) NOT NULL DEFAULT '0' COMMENT '冻结余额',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `mofidy_time` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='账单表';

-- ----------------------------
-- Table structure for t_account_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_account_detail`;
CREATE TABLE `t_account_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `change_money` bigint(20) NOT NULL DEFAULT '0' COMMENT '变化金额',
  `usable_money` bigint(20) NOT NULL DEFAULT '0' COMMENT '当前可用余额',
  `frozen_money` bigint(20) NOT NULL DEFAULT '0' COMMENT '当前冻结余额',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `history_usable_money` bigint(20) NOT NULL DEFAULT '0' COMMENT '历史可用余额',
  `history_frozen_money` bigint(20) NOT NULL DEFAULT '0' COMMENT '历史冻结余额',
  `history_create_time` bigint(20) NOT NULL COMMENT '历史时间',
  `source_type` int(11) NOT NULL COMMENT '来源类型',
  `source_id` int(11) NOT NULL COMMENT '来源id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='账单明细表';

-- ----------------------------
-- Table structure for t_bank
-- ----------------------------
DROP TABLE IF EXISTS `t_bank`;
CREATE TABLE `t_bank` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bank_name` varchar(50) NOT NULL COMMENT '银行名称',
  `orders` int(11) NOT NULL COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1015 DEFAULT CHARSET=utf8 COMMENT='银行表';

-- ----------------------------
-- Table structure for t_bank_card
-- ----------------------------
DROP TABLE IF EXISTS `t_bank_card`;
CREATE TABLE `t_bank_card` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `user_real_name` varchar(20) DEFAULT NULL COMMENT '开户人姓名',
  `bank_no` varchar(19) NOT NULL COMMENT '银行卡卡号',
  `bank_id` int(11) NOT NULL,
  `bank_name` varchar(100) NOT NULL COMMENT '银行卡名称',
  `sub_bank` varchar(100) NOT NULL COMMENT '支行名称',
  `is_delete` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_call_result
-- ----------------------------
DROP TABLE IF EXISTS `t_call_result`;
CREATE TABLE `t_call_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `event` varchar(50) DEFAULT NULL COMMENT '值为：callhangup',
  `callid` varchar(50) DEFAULT NULL COMMENT '呼叫的唯一标识',
  `accountid` varchar(44) DEFAULT NULL COMMENT '开发者账号id',
  `appid` varchar(50) DEFAULT NULL COMMENT '应用id',
  `confid` varchar(50) DEFAULT NULL COMMENT '群聊id (仅语音群聊场景)',
  `calltype` tinyint(4) DEFAULT NULL COMMENT '0：直拨，1：免费，2：回拨',
  `callertype` tinyint(4) DEFAULT NULL COMMENT '主叫号码类型，0：Client账号，1：普通电话',
  `caller` varchar(50) DEFAULT NULL COMMENT '主叫号码 \r\n            普通电话：18612345678 \r\n            Client号码：60000000000017',
  `calledtype` tinyint(4) DEFAULT NULL COMMENT '被叫号码类型，0：Client账号，1：普通电话',
  `called` varchar(50) DEFAULT NULL COMMENT '被叫号码 \r\n            普通电话：18612345678 \r\n            Client号码：60000000000017',
  `starttime` varchar(30) DEFAULT NULL COMMENT '开始通话时间。时间格式如：2014-06-16 16:47:28',
  `stoptime` varchar(50) DEFAULT NULL COMMENT '结束通话时间。时间格式如：2014-06-16 17:31:14',
  `length` int(11) DEFAULT NULL COMMENT '通话时长(s)',
  `recordurl` varchar(255) DEFAULT NULL COMMENT '通话录音完整下载地址，默认为空。',
  `user_data` varchar(255) DEFAULT NULL COMMENT '用户自定义数据字符串，最大长度128字节',
  `reason` varchar(10) DEFAULT NULL COMMENT '挂机原因描述，0：正常挂断；1：余额不足；2：媒体超时；3：无法接通；4：拒接；5：超时未接；6：拒接或超时未接；7：平台服务器网络错误；8：用户请求取消通话；9：第三方鉴权错误；255：其他原因。',
  `subreason` varchar(10) DEFAULT NULL COMMENT '挂机原因补充描述，1：主叫挂断；2：被叫挂断；目前当reason=0时有效。',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='回拨结果';

-- ----------------------------
-- Table structure for t_disease
-- ----------------------------
DROP TABLE IF EXISTS `t_disease`;
CREATE TABLE `t_disease` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id` int(11) DEFAULT NULL COMMENT '患者id',
  `disease_info` longtext COMMENT '病情信息',
  `need_help` tinyint(1) DEFAULT NULL COMMENT '需要帮助',
  `created_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '创建人',
  `telephone` varchar(20) DEFAULT NULL COMMENT '电话号码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=727 DEFAULT CHARSET=utf8 COMMENT='病情信息';

-- ----------------------------
-- Table structure for t_income
-- ----------------------------
DROP TABLE IF EXISTS `t_income`;
CREATE TABLE `t_income` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `doctor_id` int(11) NOT NULL COMMENT '用户id',
  `total_income` bigint(20) NOT NULL DEFAULT '0' COMMENT '总收入',
  `out_income` bigint(20) NOT NULL DEFAULT '0' COMMENT '未结算收入',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='收入表';

-- ----------------------------
-- Table structure for t_income_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_income_detail`;
CREATE TABLE `t_income_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `doctor_id` int(11) NOT NULL COMMENT '医生id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `order_id` int(11) NOT NULL COMMENT '订单id',
  `pack_id` int(11) NOT NULL COMMENT '套餐id',
  `money` bigint(20) NOT NULL DEFAULT '0' COMMENT '收益金额',
  `actual_money` bigint(20) NOT NULL DEFAULT '0' COMMENT '实际收益金额',
  `income_type` int(11) NOT NULL COMMENT '收益类型(1:订单收益，2:分成收益)',
  `remark` varchar(100) NOT NULL COMMENT '备注',
  `income_time` bigint(20) NOT NULL COMMENT '收入时间(订单完成时间)',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `finish_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '结算时间',
  `status` int(11) NOT NULL COMMENT '状态（1：已结算；2：未结算）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='收入表明细表';

-- ----------------------------
-- Table structure for t_income_month
-- ----------------------------
DROP TABLE IF EXISTS `t_income_month`;
CREATE TABLE `t_income_month` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `doctor_id` int(11) NOT NULL COMMENT '医生id',
  `month` int(11) NOT NULL COMMENT '月份，格式201508',
  `money` bigint(20) NOT NULL DEFAULT '0' COMMENT '金额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='收入按月统计表';

-- ----------------------------
-- Table structure for t_patient
-- ----------------------------
DROP TABLE IF EXISTS `t_patient`;
CREATE TABLE `t_patient` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `sex` smallint(6) DEFAULT NULL COMMENT '性别',
  `birthday` bigint(20) DEFAULT NULL COMMENT '生日',
  `relation` varchar(20) DEFAULT NULL COMMENT '关系',
  `area` varchar(255) DEFAULT NULL COMMENT '所在地区',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `telephone` varchar(20) DEFAULT NULL COMMENT '手机号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=605 DEFAULT CHARSET=utf8 COMMENT='患者信息';

-- ----------------------------
-- Table structure for t_recharge
-- ----------------------------
DROP TABLE IF EXISTS `t_recharge`;
CREATE TABLE `t_recharge` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `recharge_money` bigint(20) NOT NULL COMMENT '充值金额',
  `pay_type` int(11)  COMMENT '充值类型',
  `pay_no` varchar(255) NOT NULL COMMENT '充值订单号',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `recharge_status` int(11) NOT NULL COMMENT '充值状态',
  `source_type` int(11) NOT NULL COMMENT '来源类型',
  `source_id` int(11) NOT NULL COMMENT '来源id',
  `param` varchar(200) DEFAULT NULL,
  `log` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=388 DEFAULT CHARSET=utf8 COMMENT='充值表';


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

drop table if exists t_order_status_log;
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

drop table if exists t_call_record;
create table t_call_record
(
   id                   int not null auto_increment comment '主键',
   call_id              varchar(80) comment 'callid(第三方接口通话唯一标识)',
   resp_code            varchar(20) comment '第三方接口响应码（000000 成功）',
   create_time          bigint comment '创建时间',
   order_id             int comment '订单id',
   has_result           bool default false comment '已经有结果',
   primary key (id)
);

alter table t_call_record comment '拨号记录';