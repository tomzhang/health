
/**
 * 将名称送的'\"'去掉
 */

db.t_check_item.find({}).forEach(function(x){
	var itemName = x.itemName;
	if(itemName && itemName.length > 0){
		itemName = itemName.replace('\"','');
		x.itemName = itemName;
		db.t_check_item.save(x);
	}
});