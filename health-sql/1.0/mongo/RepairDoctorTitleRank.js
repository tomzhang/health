

/**
 * 修改doctor 中的title 和 titleRank 不匹配的问题
 * 2015年12月29日16:38:10
 * wangl
 */
var db = connect("127.0.0.1:27017/health");
db.auth("health","healthnx")

db.user.find({"doctor.title":{$ne:null}}).forEach(function(x){
    var title = x.doctor.title;
    
    if (title != null) {
   		var t = db.b_doctortitle.findOne({"name":title});
    	if(t!=null){
        	x.doctor.titleRank = NumberInt(t.rank);
	        db.user.save(x);
    	}
    }
});