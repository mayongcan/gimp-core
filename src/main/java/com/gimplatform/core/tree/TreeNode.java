package com.gimplatform.core.tree;

/**
 * 生成树节点
 * @author zzd
 *
 */
public class TreeNode {
	
	private String id;
	private String parent;
	private String text;
	private boolean checked;
	private int print;
	private String icon;
	private String state;

	public TreeNode(){
		
	}
	
	public TreeNode(String id, String text,String parent,boolean checked) {
		this.id = id;
		this.text = text;
		this.parent = parent;
		this.checked = checked;
		this.print = 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public int getPrint() {
		return print;
	}

	public void setPrint(int print) {
		this.print = print;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
