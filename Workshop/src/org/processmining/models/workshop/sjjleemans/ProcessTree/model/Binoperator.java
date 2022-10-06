package org.processmining.models.workshop.sjjleemans.ProcessTree.model;


public abstract class Binoperator extends Node {
	protected Node childLeft;
	protected Node childRight;
	
	public Binoperator() {
		childLeft = null;
		childRight = null;
	}
	
	protected String toString(String operatorString) {
		if (childLeft != null)	{
			if (childRight != null) {
				return '(' + childLeft.toString() + " " + operatorString + " " + childRight.toString() + ')';
			} else {
				return "(" + childLeft.toString() + " " + operatorString + " .. )";
			}
		} else {
			return "( .. " + operatorString + " .. )";
		}
	}
	
	public void setChildLeft(Node childLeft) {
		this.childLeft = childLeft;
	}
	
	public Node getChildLeft() {
		return childLeft;
	}
	
	public void setChildRight(Node childRight) {
		this.childRight = childRight;
	}
	
	public Node getChildRight() {
		return childRight;
	}
	
}
