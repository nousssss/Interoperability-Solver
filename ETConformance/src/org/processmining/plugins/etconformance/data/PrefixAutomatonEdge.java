/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/
package org.processmining.plugins.etconformance.data;

import org.deckfour.xes.classification.XEventClass;

/**
 * Class representing an edge of the Prefix Automaton.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class PrefixAutomatonEdge {
	
	/** Task in the label of the edge */
	XEventClass event;
	
	/**
	 * Create a new edge with the given task label.
	 * @param e Task label of the edge.
	 */
	public PrefixAutomatonEdge (XEventClass e){
		event = e;
	}

	/**
	 * Get the task of the edge.
	 * @return The label of the edge.
	 */
	public XEventClass getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return event.toString();
	}
}
