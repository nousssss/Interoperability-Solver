/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util;

import java.util.ArrayList;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Log automaton without id
 * 
 * @author aadrians Oct 14, 2011
 * 
 */
public class LogAutomatonNodeWOID {
	private Transition transition;
	private int frequency = 0;
	private LogAutomatonNodeWOID parent = null;
	private ArrayList<LogAutomatonNodeWOID> successors = null;

	public LogAutomatonNodeWOID(Transition transition) {
		this.transition = transition;
	}

	/**
	 * @return the transition
	 */
	public Transition getTransition() {
		return transition;
	}

	/**
	 * @param transition
	 *            the transition to set
	 */
	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency
	 *            the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * Add the frequency of this node
	 * 
	 * @param addition
	 */
	public void addFrequency(int addition) {
		this.frequency += addition;
	}
	
	/**
	 * @return the successors
	 */
	public ArrayList<LogAutomatonNodeWOID> getSuccessors() {
		return successors;
	}

	/**
	 * @param successors
	 *            the successors to set
	 */
	public void setSuccessors(ArrayList<LogAutomatonNodeWOID> successors) {
		this.successors = successors;
	}

	/**
	 * Get the child that refer to the given transition create one if its not
	 * exist yet
	 * 
	 * @param nodeInstance
	 * @return
	 */
	public LogAutomatonNodeWOID getOrCreateChild(Transition transition) {
		if (successors == null) {
			successors = new ArrayList<LogAutomatonNodeWOID>();
		} else {
			// try to find one in successors array
			for (LogAutomatonNodeWOID node : successors) {
				if (node.getTransition().equals(transition)) {
					return node;
				}
			}
		}

		// only when no successor point to transition
		LogAutomatonNodeWOID res = new LogAutomatonNodeWOID(transition);
		res.setParent(this);
		successors.add(res);
		return res;
	}

	/**
	 * @return the parent
	 */
	public LogAutomatonNodeWOID getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(LogAutomatonNodeWOID parent) {
		this.parent = parent;
	}

	
	/**
	 * Return successor that refer to trans
	 * 
	 * @param trans
	 * @return
	 */
	public LogAutomatonNodeWOID getSuccReferTo(Transition trans) {
		for (LogAutomatonNodeWOID succ : successors) {
			if (succ.getTransition().equals(trans)) {
				return succ;
			}
		}
		return null;
	}
}
