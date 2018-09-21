package com.dachen.util.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtTreeUtil {

    public static List<ExtTreeNode> buildTree(List<ExtTreeNode> list) {
        List<ExtTreeNode> resultList = new ArrayList<ExtTreeNode>();

        if (list == null || list.size() == 0) {
            return resultList;
        }

        Map<String, ExtTreeNode> tempMap = new HashMap<String, ExtTreeNode>();
        List<ExtTreeNode> tempList = new ArrayList<ExtTreeNode>();

        for (ExtTreeNode treeNode : list) {
            // 默认叶子节点
            treeNode.setLeaf(ExtTreeNode.IS_LEAF_NODE_Y);
            tempMap.put(treeNode.getId().toString(), treeNode);
            tempList.add(treeNode);
        }

        for (ExtTreeNode treeNode : tempList) {
            if (tempMap.containsKey(treeNode.getParentId())) {
                ExtTreeNode parentBean = tempMap.get(treeNode.getParentId());
                parentBean.setLeaf(ExtTreeNode.IS_LEAF_NODE_N);
                List<ExtTreeNode> childrenList = parentBean.getChildren();
                if (childrenList == null) {
                    childrenList = new ArrayList<ExtTreeNode>();
                }
                childrenList.add(treeNode);
                parentBean.setChildren(childrenList);
            }
            if (ExtTreeNode.ROOT_NODE_PARENT_ID.equals(treeNode.getParentId())) {
                resultList.add(treeNode);
            }
        }
        return resultList;
    }

}
