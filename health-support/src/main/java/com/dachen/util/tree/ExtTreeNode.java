package com.dachen.util.tree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProjectName： health-support<br>
 * ClassName： ExtTreeNode<br>
 * Description： 树节点json实体<br>
 * 
 * @author fanp
 * @createTime 2015年8月27日
 * @version 1.0.0
 */
public class ExtTreeNode implements Serializable {

    private static final long serialVersionUID = 1060370527473628379L;

    /* 根节点初始化时数字0 */
    public static final String ROOT_NODE_PARENT_ID = "0";

    /* 叶子节点 */
    public static final boolean IS_LEAF_NODE_Y = true;

    /* 非叶子节点 */
    public static final boolean IS_LEAF_NODE_N = false;

    /* 节点ID */
    private String id;

    /* 父节点Id */
    private String parentId;

    /* 节点名称 */
    private String name;

    /* 节点是否为叶子节点(false为非叶子节点 true为叶子节点) */
    private boolean leaf;

    /* 存储节点信息 */
    private Map<String, Object> attributes = new HashMap<String, Object>();

    /* 子节点集合 */
    private List<ExtTreeNode> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public List<ExtTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<ExtTreeNode> children) {
        this.children = children;
    }

}
