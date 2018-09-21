package com.dachen.health.pack.illhistory.entity.vo;

import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.health.pack.consult.entity.po.IllCaseType;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Created by fuyongde on 2016/12/8.
 */
public class RecordTypeVo {


    /**
     * 来源于t_ill_case_type表
     */
    public final static int SOURCE_ILL_CASE_TYPE = 1;
    /**
     * 来源于b_checkup表
     */
    public final static int SOURCE_CHECKUP = 2;

    /**id**/
    private String id;
    /**名称**/
    private String name;
    /**父级id**/
    private String parentId;
    /**来源于那张表**/
    private Integer source;
    /**是否为叶子节点**/
    private Boolean isLeaf;

    public Boolean getLeaf() {
        return isLeaf;
    }

    public void setLeaf(Boolean leaf) {
        isLeaf = leaf;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RecordTypeVo) {
            RecordTypeVo recordTypeVo = (RecordTypeVo) obj;
            if (StringUtils.isBlank(recordTypeVo.getId())) {
                return false;
            }
            return this.getId().hashCode() == recordTypeVo.getId().hashCode();
        }
        return false;
    }

    public static RecordTypeVo formIllCaseType(IllCaseType illCaseType) {
        RecordTypeVo recordTypeVo = new RecordTypeVo();
        recordTypeVo.setId(illCaseType.getId());
        recordTypeVo.setName(illCaseType.getTypeName());
        recordTypeVo.setSource(SOURCE_ILL_CASE_TYPE);
        return recordTypeVo;
    }

    public static RecordTypeVo formCheckSuggest(CheckSuggest checkSuggest) {
        RecordTypeVo recordTypeVo = new RecordTypeVo();
        recordTypeVo.setId(checkSuggest.getId());
        recordTypeVo.setName(checkSuggest.getName());
        recordTypeVo.setParentId(checkSuggest.getParent());
        recordTypeVo.setSource(SOURCE_CHECKUP);
        recordTypeVo.setLeaf(checkSuggest.isLeaf());
        return recordTypeVo;
    }
}
