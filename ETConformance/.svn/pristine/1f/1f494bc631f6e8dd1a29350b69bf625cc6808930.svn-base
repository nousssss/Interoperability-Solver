package org.processmining.plugins.alignetc.core;

/**
 * Arc of the Replay Automaton.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class ReplayAutomatonArc {
	
	public enum ArcType { NORMAL, ESCAPING, CUT }
	
	public ArcType type;
	public ReplayAutomatonNode target;
	
	public ReplayAutomatonArc(ArcType type, ReplayAutomatonNode target){
		this.type = type;
		this.target = target;
	}

}
