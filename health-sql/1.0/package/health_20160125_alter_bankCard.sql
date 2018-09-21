alter table t_bank_card modify is_delete int(5);
ALTER TABLE t_bank_card MODIFY sub_bank VARCHAR(100)  NULL;
ALTER TABLE t_bank_card MODIFY bank_id int(11)  NULL;
ALTER TABLE t_bank_card add is_default bit(1)  NULL;
ALTER TABLE t_bank_card add group_id varchar(50)  NULL;
