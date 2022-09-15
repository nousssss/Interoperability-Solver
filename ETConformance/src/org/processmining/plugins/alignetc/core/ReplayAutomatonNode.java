package org.processmining.plugins.alignetc.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.alignetc.core.ReplayAutomatonArc.ArcType;

/**
 * Node of the Replay Automaton.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class ReplayAutomatonNode {
	
	private Map<Transition,ReplayAutomatonArc> outArcs;
	private float weight;
	
	public ReplayAutomatonNode(float w){
		weight = w;
		outArcs = new HashMap<Transition,ReplayAutomatonArc>();
	}

	public Set<Transition> getOutTransitions() {
		return outArcs.keySet();
	}

	public void addArc(Transition trans, ReplayAutomatonNode target, ArcType type) {
		outArcs.put(trans, new ReplayAutomatonArc(type,target));
	}

	public Set<Entry<Transition, ReplayAutomatonArc>> getOutInfo() {
		return outArcs.entrySet();
	}
	
	public float getWeight(){
		return this.weight;
	}

	public ReplayAutomatonArc getArc(Transition t) {
		return outArcs.get(t);
	}
	
	public void incWeight(float inc){
		weight += inc;
	}
	
}

