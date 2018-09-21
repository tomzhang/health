package com.dachen.health.group.timertask;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;

@Component("groupTimerTask")
public class GroupTimerTask {
	
	private static Logger logger = LoggerFactory.getLogger(GroupTimerTask.class); 

	@Autowired
	protected IGroupDao groupDao;
	
	@Autowired
	protected UserRepository userDao;
	
	@Autowired
	protected IGroupDoctorDao gdDao;
	
	public void setGroupWeightEachDay(){
		List<Group> list = groupDao.findAll();
		if(list != null && list.size() > 0){
			for (Group g : list) {
				String groupId = g.getId();
				Integer weight = 0;
				List<GroupDoctor> gds = gdDao.findGroupDoctorsByGroupId(groupId);
				if(gds != null && gds.size() > 0){
					for (GroupDoctor gd : gds) {
						Integer doctorId = gd.getDoctorId();
						User u = userDao.getUser(doctorId);
						if(u != null && u.getDoctor() != null && u.getStatus()==1){
							String rank = u.getDoctor().getTitleRank();
							if("1".equals(StringUtils.trimToEmpty(rank))){
								weight += 10;
							}else if ("2".equals(StringUtils.trimToEmpty(rank))){
								weight += 8;
							}else {
								weight += 5;
							}
						}
					}
				}
				if(g.getCureNum() != null){
					weight += g.getCureNum();
				}
				Group pg = new Group();
				pg.setWeight(weight);
				pg.setId(groupId);
				groupDao.update(pg);
			}
		}
	}
}
