
/**
 * 将名称送的'\"'去掉
 */
db.b_checkup.find({}).forEach(function(x){
	var name = x.name;
	name = name.replace('\"','');
	x.name = name;
	db.b_checkup.save(x);
});