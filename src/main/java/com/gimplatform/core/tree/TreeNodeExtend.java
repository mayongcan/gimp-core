package com.gimplatform.core.tree;

import java.util.Map;

/**
 * 带属性的树节点
 * @author zzd
 */
public class TreeNodeExtend extends TreeNode {

    public TreeNodeExtend(String id, String text, String parent, boolean checked) {
        super(id, text, parent, checked);
    }

//    private Map<String, String> attributes;

    private Map<String, String> data;

    public TreeNodeExtend(String id, String text, String parent, boolean checked, Map<String, String> attributes) {
        super(id, text, parent, checked);
//        this.attributes = attributes;
        this.data = attributes;
    }

//    public Map<String, String> getAttributes() {
//        return attributes;
//    }
//
//    public void setAttributes(Map<String, String> attributes) {
//        this.attributes = attributes;
//    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
