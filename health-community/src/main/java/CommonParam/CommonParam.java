package CommonParam;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;

import com.dachen.commons.page.PageVO;
import com.dachen.health.community.entity.po.CollectIds;

public class CommonParam extends PageVO{
	private Integer userId;
	
	private Set<CollectIds> collects;
	
	private Set<ObjectId> collectObjectIds=new HashSet<>();
	
	private String groupId;
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Set<ObjectId> getCollectObjectIds() {
		for (CollectIds str : this.collects) { 
			ObjectId object=new ObjectId(str.getTopicId());
			this.collectObjectIds.add(object);  
		}  		
		return collectObjectIds;
	}

	public Set<CollectIds> getCollects() {
		return collects;
	}

	public void setCollects(Set<CollectIds> collects) {
		this.collects = collects;
	}


	
	
	
	
}
