/*
Navicat MySQL Data Transfer

Source Server         : 7
Source Server Version : 50625
Source Host           : 192.168.3.7:3306
Source Database       : health

Target Server Type    : MYSQL
Target Server Version : 50625
File Encoding         : 65001

Date: 2015-09-25 13:35:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_patient_disease
-- ----------------------------
DROP TABLE IF EXISTS `t_patient_disease`;
CREATE TABLE `t_patient_disease` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `patient_id` int(11) NOT NULL COMMENT '患者id',
  `doctor_id` int(11) NOT NULL COMMENT '医生id',
  `order_id` int(11) NOT NULL COMMENT '订单id',
  `disease_type_id` varchar(20) NOT NULL COMMENT '病种id',
  `disease_type_name` varchar(20) NOT NULL COMMENT '病种名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8 COMMENT='患者病种标签表';
