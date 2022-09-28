package com.waben.option.common.util;


import com.waben.option.common.model.dto.user.UserTreeNodeDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Peter
 * @date: 2021/7/1 19:17
 */
public class TreeNodeUtil {

    /**
     * 使用递归方法建树
     *
     * @param treeNodes
     * @return
     */
    public static <T extends UserTreeNodeDTO> List<T> buildByRecursive(List<T> treeNodes, Object root, String phone) {
        List<T> trees = new ArrayList<T>();
        for (T treeNode : treeNodes) {
            if (root.equals(treeNode.getParentId())) {
                treeNode.setLevel(1);
                treeNode.setParentPhone(phone);
                trees.add(findChildren(treeNode, treeNodes));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     *
     * @param treeNodes
     * @return
     */
    public static <T extends UserTreeNodeDTO> T findChildren(T treeNode, List<T> treeNodes) {
        for (T it : treeNodes) {
            if (treeNode.getId().compareTo(it.getParentId()) == 0) {
                if (treeNode.getChildrenList() == null) {
                    treeNode.setChildrenList(new ArrayList<UserTreeNodeDTO>());
                }
                it.setLevel(treeNode.getLevel() + 1);
                it.setParentPhone(treeNode.getMobilePhone());
                treeNode.add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }
}
