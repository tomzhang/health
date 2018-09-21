

/**
 * 删除126mongo库 c_group_doctor 表中“大辰66医生集团”相关的部分脏数据，便于测试
 * 2015年12月29日16:38:10
 * wangqiao
 */
var db = connect("127.0.0.1:27017/health");
db.auth("health","healthnx")

db.c_group_doctor.remove({"status":"S"});
db.c_group_doctor.remove({"status":"I"});