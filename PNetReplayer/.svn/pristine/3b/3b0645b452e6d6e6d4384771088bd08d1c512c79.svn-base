/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util.statespaces;

import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * Variation of the CPNCostBasedTreeNodeX, with encoding such that memory
 * requirement is smaller
 * 
 * @author aadrians
 *
 */
public class CPNCostBasedTreeNodeEnc extends AbstractCPNCostBasedTreeNode<CPNCostBasedTreeNodeEnc> implements Comparable<CPNCostBasedTreeNodeEnc>{
	// required to tell position of replay
	private int currIndexOnAutomaton = 0;
	
//	private CPNCostBasedTreeNodeEnc parent;
	
	// required to explore state space
	private double cost = 0;

	public CPNCostBasedTreeNodeEnc(int currIndexOnTrace, int currEncMarking, int currIndexOnAutomaton, StepTypes latestStepTypes, Integer relatedStepTypeObject, double cost, CPNCostBasedTreeNodeEnc parent){
		this.currIndexOnTrace = currIndexOnTrace;
		this.currEncMarking = currEncMarking;
		this.currIndexOnAutomaton = currIndexOnAutomaton;
		this.latestStepType = latestStepTypes;
		this.relatedStepTypeObj = relatedStepTypeObject;
		this.cost = cost;
		this.parent = parent;
	}
	
	
//	/**
//	 * @return the parent
//	 */
//	public CPNCostBasedTreeNodeEnc getParent() {
//		return parent;
//	}
//
//	/**
//	 * @param parent the parent to set
//	 */
//	public void setParent(CPNCostBasedTreeNodeEnc parent) {
//		this.parent = parent;
//	}



	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}



	/**
	 * @param cost the cost to set
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}



	/**
	 * @return the currIndexOnAutomaton
	 */
	public int getCurrIndexOnAutomaton() {
		return currIndexOnAutomaton;
	}

	/**
	 * @param currIndexOnAutomaton the currIndexOnAutomaton to set
	 */
	public void setCurrIndexOnAutomaton(int currIndexOnAutomaton) {
		this.currIndexOnAutomaton = currIndexOnAutomaton;
	}

	/**
	 * @return the currCost
	 */
	public double getCurrCost() {
		return cost;
	}


	/**
	 * @param currCost the currCost to set
	 */
	public void setCurrCost(double currCost) {
		this.cost = currCost;
	}

	@Override
	public int compareTo(CPNCostBasedTreeNodeEnc other) {
		// compare from cost
		return Double.compare(getCurrCost(), other.getCurrCost()); 
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof CPNCostBasedTreeNodeEnc){
			CPNCostBasedTreeNodeEnc otherObj = (CPNCostBasedTreeNodeEnc) other;
			return (Double.compare(getCost(), otherObj.getCost()) == 0) && (getCurrEncMarking() == otherObj.getCurrEncMarking())
					&& (getCurrIndexOnTrace() == otherObj.getCurrIndexOnTrace()) && (getCurrIndexOnAutomaton() == otherObj.getCurrIndexOnAutomaton());
		} else {
			return super.equals(other);
		}
	}
}
