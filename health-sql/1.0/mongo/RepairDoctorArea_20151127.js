
var db = connect("127.0.0.1:27017/health");
db.auth("health","healthnx")

//修复user.doctor.province、city、country为空的记录
db.user.find({"doctor":{$ne:null}, "doctor.check.hospitalId":{$ne:null}, "userType":3}).forEach(function(x) {
	
	var hospital = db.b_hospital.findOne({"_id" : x.doctor.check.hospitalId});
	
	if (hospital != null) {
		x.doctor.provinceId = new NumberInt(hospital.province);
		x.doctor.cityId = new NumberInt(hospital.city);
		x.doctor.countryId = new NumberInt(hospital.country);
		x.doctor.hospitalId = hospital.Id;
		x.doctor.hospital = hospital.name;
		
		if (hospital.province != null) {
			var area = db.b_area.findOne({"code" : hospital.province});
			if (area != null)
				x.doctor.province = area.name;	
		}
		if (hospital.city != null) {
			var area = db.b_area.findOne({"code" : hospital.city});
			if (area != null)
				x.doctor.city = area.name;
		}
		if (hospital.country != null) {
			var area = db.b_area.findOne({"code" : hospital.country});
			if (area != null)
				x.doctor.country = area.name;
		}	
		db.user.save(x);
	}
});