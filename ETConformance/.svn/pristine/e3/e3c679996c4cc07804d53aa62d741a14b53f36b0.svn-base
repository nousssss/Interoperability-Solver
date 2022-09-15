/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/
package org.processmining.plugins.etconformance.data;

import java.util.Vector;

/**
 * Class containing all the information about the imprecisions of a system
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class Imprecisions {
	
	/** Vector of the Imprecision/Escaping States */
	private Vector<PrefixAutomatonNode> imp;
	/** Gain of covering each Imprecision/Escaping States */
	private Vector<Integer> gain;
	/** Cost of covering each Imprecision/Escaping States */
	private Vector<Integer> cost;
	
	/**
	 * Create a new imprecisions object.
	 */
	public Imprecisions(){
		imp = new Vector<PrefixAutomatonNode>();
		gain = new Vector<Integer>();
		cost = new Vector<Integer>();
	}
	
	
	//GETTERS and SETTERS
	
	public int numImp(){
		return imp.size();
	}
	
	public PrefixAutomatonNode getImp(int i) {
		return imp.get(i);
	}

	public void addImp(PrefixAutomatonNode imp) {
		this.imp.add(imp);
	}

	public Integer getGain(int i) {
		return gain.get(i);
	}

	public void addGain(Integer gain) {
		this.gain.add(gain);
	}

	public Integer getCost(int i) {
		return cost.get(i);
	}

	public void addCost(Integer cost) {
		this.cost.add(cost);
	}
}
