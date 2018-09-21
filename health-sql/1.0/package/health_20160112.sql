drop table if exists t_income_details;

/*==============================================================*/
/* Table: t_income_details                                       */
/*==============================================================*/
create table t_income_details
(
   id                   int not null auto_increment comment '主键',
   doctor_id            int not null comment '医生id',
   order_id             int not null comment '订单id',
   money                bigint not null default 0 comment '订单总金额',
   share_money          bigint not null default 0 comment '医生实际在订单实际获益金额',
   income_type          int not null comment '收益类型(1:订单收益，2:分成收益)',
   doc_proportion       int comment '该医生的直接上级医生所提成比例',
   up_doc               int comment '该医生的直接上级医生ID',
   group_proportion     int comment '该医生在获取订单时的集团收益比例',
   up_group             varchar(50) comment '该医生在获取订单时的集团ID',
   sys_proportion       int comment '玄关健康平台的收益比例',
   self_proportion     int comment '医生收益比例',
   status               int not null comment '状态是否退款',
   remark               varchar(100) comment '备注',
   create_time          bigint not null comment '创建时间',
   modify_time          bigint,
   extend_1             varchar(100) comment '备用字段',
   extend_2             varchar(100) comment '备用字段',
   primary key (id)
);

drop table if exists t_settle;

/*==============================================================*/
/* Table: t_settle                                              */
/*==============================================================*/
create table t_settle
(
   id                   int not null auto_increment,
   user_id              varchar(50) not null,
   user_type            int not null,
   settle_money         bigint not null,
   status               int not null,
   tax_money            bigint not null,
   actual_money         bigint not null,
   user_bank_id         int comment '用户可以随时切换默认银行卡，所以要绑定用户银行卡',
   create_time          bigint not null,
   settle_time          bigint comment '结算时间',
   primary key (id)
);

alter table t_settle comment '此表数据基于用户集团来统计';


drop table if exists t_income_settle;

/*==============================================================*/
/* Table: t_income_settle                                       */
/*==============================================================*/
create table t_income_settle
(
   id                   int not null auto_increment,
   income_id            int not null,
   settle_id            int not null,
   create_time          bigint not null,
   primary key (id)
);

alter table t_income_settle comment '收入表与结算表为多对多';