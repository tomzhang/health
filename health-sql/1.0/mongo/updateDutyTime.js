db.c_group.find({"config.dutyStartTime":null}).forEach(function(x){
       x.config.dutyStartTime="08:00";
       db.c_group.save(x);
});

db.c_group.find({"config.dutyEndTime":null}).forEach(function(x){
       x.config.dutyEndTime="22:00";
       db.c_group.save(x);
});