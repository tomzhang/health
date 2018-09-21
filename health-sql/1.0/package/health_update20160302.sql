alter table p_order add column illCaseInfo_id varchar(50) DEFAULT '' COMMENT '电子病历';

ALTER TABLE t_refund ADD COLUMN refund_no VARCHAR(50) NOT NULL COMMENT '退款批次号';
ALTER TABLE t_refund ADD COLUMN refund_reason VARCHAR(50) NOT NULL COMMENT '退款原因';
ALTER TABLE t_refund ADD COLUMN trans_id VARCHAR(50) NOT NULL COMMENT '第三方交易号';
ALTER TABLE t_refund MODIFY COLUMN money BIGINT NOT NULL COMMENT '交易额';
ALTER TABLE t_refund CHANGE COLUMN user_id order_id INT NOT NULL COMMENT '订单ID';

ALTER TABLE p_case MODIFY COLUMN description VARCHAR(2000);
ALTER TABLE p_case MODIFY COLUMN message VARCHAR(2000);
