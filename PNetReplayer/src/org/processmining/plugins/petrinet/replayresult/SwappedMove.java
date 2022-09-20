/**
 * 
 */
package org.processmining.plugins.petrinet.replayresult;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * @author aadrians Sep 14, 2012
 * 
 */
public class SwappedMove {
	private Transition occurTrans = null;
	private Transition insteadOf = null;

	@SuppressWarnings("unused")
	private SwappedMove() {
	}

	public SwappedMove(Transition occurTrans, Transition insteadOf) {
		this.occurTrans = occurTrans;
		this.insteadOf = insteadOf;
	}

	/**
	 * @return the transition
	 */
	public Transition getOccurTrans() {
		return occurTrans;
	}

	/**
	 * @param transition
	 *            the transition to set
	 */
	public void setOccurTrans(Transition transition) {
		this.occurTrans = transition;
	}

	/**
	 * @return the swappedWith
	 */
	public Transition getInsteadOf() {
		return insteadOf;
	}

	/**
	 * @param insteadOf
	 *            the swappedWith to set
	 */
	public void setInsteadOf(Transition insteadOf) {
		this.insteadOf = insteadOf;
	}

	public String toString() {
		return "[" + occurTrans.getLabel() + "]-instead-of-[" + insteadOf.getLabel() + "]";
	}
}
