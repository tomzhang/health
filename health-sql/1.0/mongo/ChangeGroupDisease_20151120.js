//var db = connect("120.24.94.126:27017/health");
//db.auth("health","healthnx")
//var db = connect("192.168.3.7:27017/health");
var db = connect("127.0.0.1:27017/health");
db.auth("health","healthnx")

//查找所有的医生集团，循环
db.c_group.find().forEach(function(doc){
  	var name = doc.name;
  	var disList = doc.diseaselist;
  	
  	//如果医生集团没有设置病种，直接返回
  	if (disList == null || disList.length == 0) {
		return;
  	} 
  	print(name + "修改前：=============" + disList);
  	
  	//定义新病种列表
  	var disOneLevelArr = new Array();
  	
  	//循环某集团的病种，如果病种为底层，则找到一级病种,放到新病种列表中（过滤重复的），更新医生集团擅长病种
  	for (var i=0; i<disList.length; i++) {
  	  var dis = disList[i];
  	  if (dis != null && dis.length >= 4) {
  	  	//截取四位，没有重新查询parent
  	  	var disOneLevel = dis.substring(0,4);
  	  	//查询病种是否已经放入到新病种列表
  	  	if (disOneLevelArr.indexOf(disOneLevel) == -1) {
  	      disOneLevelArr[disOneLevelArr.length] = disOneLevel;
  	  	}
  	  }
  	}
	//使用新病种列表更新集团病种，不考虑重名情况
  	db.c_group.update({"name": name},{$set:{"diseaselist":disOneLevelArr}});
  	print(name + "修改后：=============" +  disOneLevelArr);
});