
var db = connect("127.0.0.1:27017/health");
db.auth("health","healthnx");

//改表名
db.c_group_profit.renameCollection("c_group_profit_old");

//创建表 c_group_profit
db.createCollection("c_group_profit");

//迭代每一条记录
db.c_group_profit_old.find({}).forEach(function(x) {
	
	x.doctorId = new NumberInt(x._id);
	x._id = new ObjectId();
	db.c_group_profit.insert(x);
	
});


