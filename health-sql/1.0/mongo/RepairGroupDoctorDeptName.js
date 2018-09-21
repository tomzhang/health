var db = connect("127.0.0.1:27017/health");
db.auth("health","healthnx")

db.c_group_doctor.find({"deptName":null}).forEach(function(x){
    var deptName = x.deptName;
    
    var t = db.user.findOne({"_id":x.doctorId});
    
    if(t!=null){
        x.deptName = t.doctor.departments;
        db.c_group_doctor.save(x);
    }
    
});