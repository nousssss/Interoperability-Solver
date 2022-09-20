/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util.statespaces;

import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * @author aadrians
 * 
 */
public class CPNCostBasedTreeNodeEncFitness extends AbstractCPNCostBasedTreeNode<CPNCostBasedTreeNodeEncFitness> implements Comparable<CPNCostBasedTreeNodeEncFitness> {
//	private CPNCostBasedTreeNodeEncFitness parent;

	// required to explore state space
	private int cost = 0;

	public CPNCostBasedTreeNodeEncFitness(int currIndexOnTrace, int currEncMarking, StepTypes latestStepTypes,
			Integer relatedStepTypeObject, Integer cost, CPNCostBasedTreeNodeEncFitness parent) {
		this.currIndexOnTrace = currIndexOnTrace;
		this.currEncMarking = currEncMarking;
		this.latestStepType = latestStepTypes;
		this.relatedStepTypeObj = relatedStepTypeObject;
		this.cost = cost;
		this.parent = parent;
	}

//	/**
//	 * @return the parent
//	 */
//	public CPNCostBasedTreeNodeEncFitness getParent() {
//		return parent;
//	}
//
//	/**
//	 * @param parent
//	 *            the parent to set
//	 */
//	public void setParent(CPNCostBasedTreeNodeEncFitness parent) {
//		this.parent = parent;
//	}

	/**
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * @param cost
	 *            the cost to set
	 */
	public void setCost(int cost) {
		this.cost = cost;
	}

	public int compareTo(CPNCostBasedTreeNodeEncFitness other) {
		if (getCost() > other.getCost()){
			return 1;
		} else {
			return ((getCost() == other.getCost()) ? 0 : -1);
		}
	}
	
	public boolean equals(Object other) {
		if (other instanceof CPNCostBasedTreeNodeEncFitness){
			CPNCostBasedTreeNodeEncFitness otherObj = (CPNCostBasedTreeNodeEncFitness) other;
			return (getCost() == otherObj.getCost()) && (getCurrEncMarking() == otherObj.getCurrEncMarking())
					&& (getCurrIndexOnTrace() == otherObj.getCurrIndexOnTrace());
		} else {
			return super.equals(other);
		}
	}

}
