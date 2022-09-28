package com.waben.option.common.model.tree;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Peter
 * @date: 2021/7/1 19:14
 */
@Data
public class TreeNodeDTO {

    private Long id;
    private Long parentId;
    private Integer level;
    private String name;
    private String code;
    private String type;
    private List<TreeNodeDTO> children;

    public TreeNodeDTO(Long id, Long parentId, String name, String code) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.code = code;
    }

    public void add(TreeNodeDTO node) {
        children.add(node);
    }
}
