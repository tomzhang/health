ALTER TABLE p_cure_record MODIFY COLUMN treat_advise VARCHAR(1000);
ALTER TABLE p_cure_record MODIFY COLUMN drug_advise VARCHAR(1000);
ALTER TABLE p_cure_record MODIFY COLUMN attention VARCHAR(1000);
ALTER TABLE p_cure_record MODIFY COLUMN consult_advise VARCHAR(1000);

ALTER TABLE t_order_session  Add column is_send_over_time tinyint(1)  comment '是否发送超期提醒' ;

ALTER TABLE p_case MODIFY birthday BIGINT(20) NULL;
ALTER TABLE p_case MODIFY sex SMALLINT(6) NULL;

ALTER TABLE p_order Add column pay_time BIGINT(20)  comment '支付时间';
ALTER TABLE p_order Add column pay_type int(11) DEFAULT 0  comment '支付类型';

ALTER TABLE t_patient_disease Add column group_id varchar(25)   comment '集团id';