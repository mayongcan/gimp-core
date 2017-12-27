package com.gimplatform.core.tree;

import java.util.LinkedList;

/**
 * 自定义树堆栈
 * @author zzd
 *
 */
public class TreeStack {
	
	private LinkedList<Object> list = new LinkedList<Object>();

	public void push(TreeNode v) {
		list.addFirst(v);
	}

	public void push(Object v) {
		list.addFirst(v);
	}

	public TreeNode top() {
		return (TreeNode) list.getFirst();
	}

	public Object topObj() {
		return list.getFirst();
	}

	public TreeNode pop() {
		return (TreeNode) list.removeFirst();
	}

	public Object popObj() {
		return list.removeFirst();
	}

	public String toString() {
		return list.toString();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
}