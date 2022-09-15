/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/
package org.processmining.plugins.etconformance.data;

/**
 * Class representing a node of the Prefix Automaton.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class PrefixAutomatonNode {
	
	/** Number of instances seen for the given node */
	private int instances;
	/** Type of Node */
	private PrefixAutomatonNodeType type;
	/** Number of direct children escaping states */
	private int numEscaping;
	/** Number of children */
	private int numChildren;
	
	/**
	 * Constructor of a new node with the given number of instances.
	 */
	public PrefixAutomatonNode(int inst){
		instances = inst;
	}
	
	/**
	 * Increment the instances number of the node.
	 * @param increment Increment to add to the instances of the node.
	 */
	public void incIntances(int increment){
		instances += increment;
	}
	
	
	
	//GETTERS and SETTERS
	
	public void setType(PrefixAutomatonNodeType t){
		type = t;
	}

	public PrefixAutomatonNodeType getType() {
		return type;
	}

	public int getInstances() {
		return instances;
	}
	
	public int getNumEscaping() {
		return numEscaping;
	}

	public void setNumEscaping(int numEscaping) {
		this.numEscaping = numEscaping;
	}

	public int getNumChildren() {
		return numChildren;
	}

	public void setNumChildren(int numChildren) {
		this.numChildren = numChildren;
	}

	@Override
	public String toString() {
		return String.valueOf(instances);
	}
	
	

}
