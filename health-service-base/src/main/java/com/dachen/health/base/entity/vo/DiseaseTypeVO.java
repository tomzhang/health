package com.dachen.health.base.entity.vo;

import org.mongodb.morphia.annotations.Property;

import java.util.List;

/**
 * ProjectName： health-service<br>
 * ClassName： TitleVO<br>
 * Description：病种VO <br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
public class DiseaseTypeVO {

    @Property("_id")
    private String id;

    private String name;

    private String parent;
    
    private boolean isLeaf;
    
    private long articleCount;
    
    private int weight;
    
    private Integer followed;

    private List<DiseaseTypeVO> children;

    private Boolean enable;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public List<DiseaseTypeVO> getChildren() {
        return children;
    }

    public void setChildren(List<DiseaseTypeVO> children) {
        this.children = children;
    }

	public long getArticleCount() {
		return articleCount;
	}

	public void setArticleCount(long articleCount) {
		this.articleCount = articleCount;
	}

	public Integer getFollowed() {
		return followed;
	}

	public void setFollowed(Integer followed) {
		this.followed = followed;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null) return false;
		if (this == arg0) return true;
		if (getClass() != arg0.getClass()) return false;
            
		DiseaseTypeVO vo = (DiseaseTypeVO) arg0;
		return this.id == null ? vo.id == null : this.id.equals(vo.id)
			&& this.name == null ? vo.name == null : this.name.equals(vo.name)
			&& this.parent == null ? vo.parent == null : this.parent.equals(vo.parent);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        return result;
	}
    
}
