/*
SQLyog Ultimate v11.24 (32 bit)
MySQL - 5.6.25 : Database - health
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`health` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `health`;

/*Table structure for table `p_pack_doctor` */

DROP TABLE IF EXISTS `p_pack_doctor`;

CREATE TABLE `p_pack_doctor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pack_id` int(11) DEFAULT NULL,
  `doctor_id` int(11) DEFAULT NULL,
  `split_ratio` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `s_care_template` */

DROP TABLE IF EXISTS `s_care_template`;

CREATE TABLE `s_care_template` (
  `id` varchar(32) NOT NULL COMMENT '模板主键',
  `groupId` varchar(32) NOT NULL COMMENT '集团ID',
  `tmpType` int(11) DEFAULT NULL COMMENT '模板类型',
  `name` varchar(32) NOT NULL COMMENT '模板名称',
  `diseaseTypeId` varchar(32) NOT NULL COMMENT '病种ID',
  `description` varchar(200) DEFAULT NULL COMMENT '模板描述',
  `price` varchar(32) DEFAULT NULL COMMENT '价格参考',
  `createTime` bigint(20) NOT NULL COMMENT '创建时间',
  `cretor` int(11) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `s_care_template_ghnr` */

DROP TABLE IF EXISTS `s_care_template_ghnr`;

CREATE TABLE `s_care_template_ghnr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `care_template_id` varchar(32) NOT NULL COMMENT '随访模板id',
  `owners` varchar(32) NOT NULL COMMENT '关怀内容id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=utf8;

/*Table structure for table `s_follow_answer` */

DROP TABLE IF EXISTS `s_follow_answer`;

CREATE TABLE `s_follow_answer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `followPatientId` bigint(20) DEFAULT NULL,
  `questionId` varchar(32) DEFAULT NULL,
  `answer` varchar(100) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `s_follow_doctor` */

DROP TABLE IF EXISTS `s_follow_doctor`;

CREATE TABLE `s_follow_doctor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `doctorId` bigint(20) DEFAULT NULL,
  `groupId` varchar(25) DEFAULT NULL,
  `careTemplateId` varchar(32) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `title` varchar(50) DEFAULT NULL COMMENT '标题',
  `description` varchar(200) DEFAULT NULL COMMENT '介绍',
  `status` int(11) DEFAULT NULL COMMENT '介绍',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `s_follow_patient` */

DROP TABLE IF EXISTS `s_follow_patient`;

CREATE TABLE `s_follow_patient` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `followDoctorId` bigint(20) NOT NULL,
  `patientId` bigint(20) NOT NULL,
  `status` int(11) DEFAULT '1' COMMENT '1：未完成；2：已完成',
  `create_time` bigint(20) DEFAULT NULL,
  `msg_group_id` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8;

/*Table structure for table `t_fp_bhsfo` */

DROP TABLE IF EXISTS `t_fp_bhsfo`;

CREATE TABLE `t_fp_bhsfo` (
  `id` varchar(32) NOT NULL,
  `owners` varchar(32) DEFAULT NULL,
  `indexs` int(11) NOT NULL,
  `number` varchar(100) NOT NULL,
  `label` varchar(100) NOT NULL,
  `remark` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_fp_bhsft` */

DROP TABLE IF EXISTS `t_fp_bhsft`;

CREATE TABLE `t_fp_bhsft` (
  `id` varchar(32) NOT NULL,
  `owners` varchar(32) DEFAULT NULL,
  `indexs` int(11) NOT NULL,
  `type` varchar(50) NOT NULL,
  `number` varchar(100) NOT NULL,
  `wtms` varchar(600) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_fp_bqjh` */

DROP TABLE IF EXISTS `t_fp_bqjh`;

CREATE TABLE `t_fp_bqjh` (
  `id` varchar(32) NOT NULL,
  `owners` varchar(32) DEFAULT NULL,
  `indexs` int(11) NOT NULL,
  `sfzq` varchar(20) NOT NULL,
  `jsxx` varchar(10) DEFAULT NULL,
  `hjzl` varchar(32) DEFAULT NULL,
  `bhtxjb` varchar(50) DEFAULT NULL,
  `bhtxxx` varchar(10) DEFAULT NULL,
  `bhtxnr` varchar(600) DEFAULT NULL,
  `ystxjb` varchar(50) DEFAULT NULL,
  `ystxxx` varchar(10) DEFAULT NULL,
  `ystxnr` varchar(600) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_fp_bqmx` */

DROP TABLE IF EXISTS `t_fp_bqmx`;

CREATE TABLE `t_fp_bqmx` (
  `id` varchar(32) NOT NULL,
  `owners` varchar(32) NOT NULL COMMENT '关怀内容ID',
  `issue_bqwt_id` varchar(32) NOT NULL COMMENT '问题ID',
  `sqe` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_fp_bqwt` */

DROP TABLE IF EXISTS `t_fp_bqwt`;

CREATE TABLE `t_fp_bqwt` (
  `id` varchar(32) NOT NULL DEFAULT '',
  `category` varchar(32) DEFAULT NULL,
  `tmms` varchar(600) DEFAULT NULL,
  `remark` varchar(1000) DEFAULT NULL,
  `creator` varchar(32) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `last_modifier` varchar(32) DEFAULT NULL,
  `last_modified_time` datetime DEFAULT NULL,
  `valid` char(1) DEFAULT NULL,
  `used` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_fp_bqxx` */

DROP TABLE IF EXISTS `t_fp_bqxx`;

CREATE TABLE `t_fp_bqxx` (
  `id` varchar(32) NOT NULL DEFAULT '',
  `owners` varchar(32) DEFAULT NULL,
  `indexs` int(11) NOT NULL,
  `label` varchar(40) NOT NULL,
  `remark` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_fp_ghnr` */

DROP TABLE IF EXISTS `t_fp_ghnr`;

CREATE TABLE `t_fp_ghnr` (
  `id` varchar(32) NOT NULL DEFAULT '',
  `title` varchar(100) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `category` varchar(32) NOT NULL,
  `remark` varchar(1000) DEFAULT NULL,
  `creator` varchar(32) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `last_modifier` varchar(32) DEFAULT NULL,
  `last_modified_time` datetime DEFAULT NULL,
  `used` char(1) DEFAULT NULL,
  `valid` char(1) DEFAULT NULL,
  `sfzq` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_fp_rcmx` */

DROP TABLE IF EXISTS `t_fp_rcmx`;

CREATE TABLE `t_fp_rcmx` (
  `id` varchar(32) NOT NULL,
  `owners` varchar(32) DEFAULT NULL,
  `indexs` int(11) NOT NULL,
  `sfzq` varchar(20) NOT NULL,
  `label` varchar(100) NOT NULL,
  `hztx` varchar(1000) DEFAULT NULL,
  `ystx` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_fp_shpg` */

DROP TABLE IF EXISTS `t_fp_shpg`;

CREATE TABLE `t_fp_shpg` (
  `id` varchar(32) NOT NULL DEFAULT '',
  `owners` varchar(32) DEFAULT NULL,
  `indexs` int(11) NOT NULL,
  `min_score` int(11) NOT NULL,
  `max_score` int(11) NOT NULL,
  `conclusion` varchar(1000) NOT NULL,
  `hjzl` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_fp_shwt` */

DROP TABLE IF EXISTS `t_fp_shwt`;

CREATE TABLE `t_fp_shwt` (
  `id` varchar(32) NOT NULL DEFAULT '',
  `owners` varchar(32) DEFAULT NULL,
  `indexs` int(11) NOT NULL,
  `number` varchar(100) NOT NULL,
  `wtms` varchar(600) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_fp_shwx` */

DROP TABLE IF EXISTS `t_fp_shwx`;

CREATE TABLE `t_fp_shwx` (
  `id` varchar(32) NOT NULL DEFAULT '',
  `owners` varchar(32) DEFAULT NULL,
  `indexs` int(11) NOT NULL,
  `number` varchar(100) NOT NULL,
  `label` varchar(100) NOT NULL,
  `remark` varchar(1000) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*套餐表修改*/
ALTER TABLE p_pack ADD COLUMN care_template_id VARCHAR(32)  COMMENT '关怀计划id';
/*订单表修改*/
ALTER TABLE p_order ADD COLUMN care_template_id VARCHAR(32)  COMMENT '关怀计划id';



drop table if exists p_order_care_item;

/*==============================================================*/
/* Table: p_order_care_item                                     */
/*==============================================================*/
create table p_order_care_item
(
   id                   int not null auto_increment,
   order_id             int,
   type                 int,
   item_id              varchar(32) comment '问题id/生活量表id/日程项id',
   day_num              int comment '根据问题随访周期生成',
   date                 date comment '对应的实际日期',
   is_doctor_set        int,
   track_answer         varchar(32) comment '病情跟踪问题答案',
   is_end               int comment '病情跟踪问题是否结束',
   scale_score          int comment '本次生活量表总分',
   status               int,
   primary key (id)
);



drop table if exists s_care_scale_answer;

/*==============================================================*/
/* Table: s_care_scale_answer                                   */
/*==============================================================*/
create table s_care_scale_answer
(
   id                   int not null auto_increment,
   order_care_item_id   varchar(32),
   question_id          varchar(32),
   answer_id            varchar(32),
   primary key (id)
);


drop table if exists p_order_doctor;

/*==============================================================*/
/* Table: p_order_doctor                                        */
/*==============================================================*/
create table p_order_doctor
(
   id                   int not null auto_increment,
   order_id             int,
   doctor_id            int,
   split_ratio          int,
   primary key (id)
);

ALTER TABLE s_care_template ADD COLUMN status int(11)  COMMENT '状态';

ALTER TABLE p_order_care_item ADD COLUMN send_time bigint(20) DEFAULT NULL COMMENT '发送时间';

ALTER TABLE p_order_care_item ADD COLUMN end_item VARCHAR(10)  COMMENT '结束选项';

drop table if exists s_follow_template;

/*==============================================================*/
/* Table: s_follow_template                                     */
/*==============================================================*/
create table s_follow_template
(
   id                   varchar(32) not null,
   name                 varchar(255),
   groupId              varchar(32),
   doctorId             int,
   diseaseLable         varchar(255) comment '以json格式存储',
   create_time          bigint,
   creater              int,
   status               int comment '1:已删除',
   description          varchar(255),
   primary key (id)
);


drop table if exists s_follow_template_date;

/*==============================================================*/
/* Table: s_follow_template_date                                */
/*==============================================================*/
create table s_follow_template_date
(
   id                   varchar(32) not null,
   follow_template_id   varchar(32),
   day_num              int,
   name                 varchar(255),
   primary key (id)
);


drop table if exists s_follow_template_detail;

/*==============================================================*/
/* Table: s_follow_template_detail                              */
/*==============================================================*/
create table s_follow_template_detail
(
   id                   int not null auto_increment,
   follow_template_date_id varchar(32),
   type                 int,
   item_id              varchar(32),
   primary key (id)
);



/*套餐表修改*/
ALTER TABLE p_pack ADD COLUMN follow_template_id varchar(32)  COMMENT '随访id';
/*订单表修改*/
ALTER TABLE p_order ADD COLUMN follow_template_id varchar(32)  COMMENT '随访id';

ALTER TABLE s_follow_template_detail MODIFY  COLUMN item_id  VARCHAR(255);
ALTER TABLE p_order_care_item MODIFY  COLUMN item_id  VARCHAR(255);