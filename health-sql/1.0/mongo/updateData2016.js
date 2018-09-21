/**
 * 修改护士的类型为8
 */
db.user.update({"userType":9,"nurse":{$exists:true}},{$set:{"userType":NumberInt(8)}},false,true);