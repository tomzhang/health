/*所有脚本都写在此文件，每个脚本注明更新日期*/

/* 2015-12-7*/
ALTER TABLE p_pack Add column group_id varchar(32)  comment '集团ID';


/**
 * 用药数据表
 */
DROP TABLE IF EXISTS `p_order_drug`;

CREATE TABLE `p_order_drug` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `order_id` int(11) DEFAULT NULL COMMENT '订单ID',
  `drug_id` varchar(32) DEFAULT NULL COMMENT '医药ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `p_order_recipe`;

CREATE TABLE `p_order_recipe` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `order_id` int(11) DEFAULT NULL COMMENT '订单ID',
  `recipe_id` varchar(32) DEFAULT NULL COMMENT '处方ID',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `p_pack_drug`;

CREATE TABLE `p_pack_drug` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pack_id` int(11) NOT NULL,
  `drug_id` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_fp_yymx`;

CREATE TABLE `t_fp_yymx` (
  `id` varchar(32) NOT NULL COMMENT '用药主键',
  `owners` varchar(32) NOT NULL COMMENT '内容ID',
  `durg_id` varchar(32) NOT NULL COMMENT '用药ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/**
 * 病情资料改造
 */
ALTER TABLE t_disease Add column area varchar(255)  comment '地区';
ALTER TABLE t_disease Add column relation varchar(20)  comment '关系';
ALTER TABLE t_disease Add column birthday BIGINT(20)  comment '生日';
ALTER TABLE t_disease Add column user_name varchar(50)  comment '用户名';
ALTER TABLE t_disease Add column age int(11)  comment '年龄';
ALTER TABLE t_disease Add column sex int(11)  comment '性别';

/**
 * 上次更新到此
 */

/**
 * 订单关怀时间线改造
 */
ALTER TABLE p_order_care_item Add column send_type int(11)  comment '发送类型';
/**
 * 关怀计划模板改造
 */

ALTER TABLE s_care_template Add column document_id varchar(32)  comment '文档内容ID';
/**
 * 套餐添加二维码及分享 订单中添加 激活状态
 */
ALTER TABLE p_pack Add column qrcode_path varchar(500)  comment '二维码地址';
ALTER TABLE p_order Add column acStatus int(11) DEFAULT 1 comment '激活状态1已激活0未激活';


ALTER TABLE t_patient_disease MODIFY COLUMN disease_type_id VARCHAR(20) COMMENT '病种id';
ALTER TABLE t_patient_disease MODIFY COLUMN disease_type_name VARCHAR(20) COMMENT '病种名称';

ALTER TABLE p_order_care_item Add column create_time bigint(20)  comment '创建时间';
ALTER TABLE p_order_care_item Add column seq int(11)  comment '顺序';
ALTER TABLE t_order_session MODIFY COLUMN msg_group_id VARCHAR(32) ;

/**
 * 上次更新到此
 */

--同一用户Id存在多个“本人”患者
UPDATE t_patient SET relation = '朋友' WHERE id IN 
(
SELECT id FROM (SELECT id FROM t_patient WHERE user_id IN (SELECT user_id FROM (SELECT user_name, user_id, relation, COUNT num FROM t_patient WHERE relation = '本人' GROUP BY user_id HAVING num > 1) AS A)
AND id NOT IN (SELECT id FROM (SELECT id, user_name, user_id, relation, COUNT num FROM t_patient WHERE relation = '本人' GROUP BY user_id HAVING num > 1 ORDER BY user_id, id) AS B)
AND relation = '本人' ORDER BY user_id, id) AS C
)
--在病情资料中增加字段
ALTER TABLE `t_disease`   ADD COLUMN `is_see_doctor`  TINYINT(1) NULL  COMMENT '是否就诊';
ALTER TABLE `t_disease`   ADD COLUMN  `disease_info_now` LONGTEXT CHARSET utf8 COLLATE utf8_general_ci NULL  COMMENT '现病史';
ALTER TABLE `t_disease`   ADD COLUMN  `disease_info_old` LONGTEXT CHARSET utf8 COLLATE utf8_general_ci NULL  COMMENT '既往史';
ALTER TABLE `t_disease`   ADD COLUMN  `family_disease_info` LONGTEXT CHARSET utf8 COLLATE utf8_general_ci NULL  COMMENT '家族史';
ALTER TABLE `t_disease`   ADD COLUMN  `menstruationdisease_info` LONGTEXT CHARSET utf8 COLLATE utf8_general_ci NULL  COMMENT '月经史';
ALTER TABLE `t_disease`   ADD COLUMN  `see_doctor_msg` LONGTEXT CHARSET utf8 COLLATE utf8_general_ci NULL  COMMENT '就诊情况';
ALTER TABLE `t_disease`   ADD COLUMN `visit_time` BIGINT (20) DEFAULT NULL COMMENT '创建时间' AFTER `see_doctor_msg`;
ALTER TABLE `t_disease`   ADD COLUMN `heigth` LONGTEXT NULL COMMENT '身高' AFTER `visit_time`;
ALTER TABLE `t_disease`   ADD COLUMN `weigth` LONGTEXT NULL COMMENT '体重' AFTER `heigth`;
ALTER TABLE `t_disease`   ADD COLUMN `marriage` LONGTEXT NULL COMMENT '婚姻' AFTER `weigth`;
ALTER TABLE `t_disease`   ADD COLUMN `profession` LONGTEXT NULL COMMENT '职业' AFTER `marriage` ;
ALTER TABLE `t_disease`   ADD COLUMN `cure_situation` LONGTEXT NULL  COMMENT '诊治情况' AFTER `profession`;

