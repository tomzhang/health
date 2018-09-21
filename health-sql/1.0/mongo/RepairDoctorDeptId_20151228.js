
var db = connect("127.0.0.1:27017/health");
db.auth("health","healthnx")

//修复user.doctor.deptId为空的记录
db.user.find({"userType":3, "doctor":{$ne:null}, "doctor.departments":{$ne:null}}).forEach(function(x) {
	
	var dept = db.b_hospitaldept.findOne({"name" : x.doctor.departments});
	
	if (dept != null) {
		x.doctor.deptId = dept._id;
		
		if (x.doctor.check != null && x.doctor.check.departments != null) {
		  	var dept2 = db.b_hospitaldept.findOne({"name" : x.doctor.check.departments});
		  	if (dept2 != null) {
		  		x.doctor.check.deptId = dept2._id;
		  	}
		}
		db.user.save(x);
	}
});