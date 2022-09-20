/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util.statespaces;

import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * @author aadrians
 * 
 */
public class CPNCostBasedTreeNodeEncFitnessWHeurCost extends AbstractCPNCostBasedTreeNode<CPNCostBasedTreeNodeEncFitnessWHeurCost> implements Comparable<CPNCostBasedTreeNodeEncFitnessWHeurCost> {
	private CPNCostBasedTreeNodeEncFitnessWHeurCost parent;

	// required to explore state space
	private int cost = 0;
	private int heuristicCost = 0;

	public CPNCostBasedTreeNodeEncFitnessWHeurCost(int currIndexOnTrace, int currEncMarking, StepTypes latestStepTypes,
			Integer relatedStepTypeObject, int cost, int heuristicCost, CPNCostBasedTreeNodeEncFitnessWHeurCost parent) {
		this.currIndexOnTrace = currIndexOnTrace;
		this.currEncMarking = currEncMarking;
		this.latestStepType = latestStepTypes;
		this.relatedStepTypeObj = relatedStepTypeObject;
		this.cost = cost;
		this.parent = parent;
		this.heuristicCost = heuristicCost;
	}

	/**
	 * @return the parent
	 */
	public CPNCostBasedTreeNodeEncFitnessWHeurCost getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(CPNCostBasedTreeNodeEncFitnessWHeurCost parent) {
		this.parent = parent;
	}

	/**
	 * @return the heuristicCost
	 */
	public int getHeuristicCost() {
		return heuristicCost;
	}

	/**
	 * @param heuristicCost
	 *            the heuristicCost to set
	 */
	public void setHeuristicCost(int heuristicCost) {
		this.heuristicCost = heuristicCost;
	}

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

	public int compareTo(CPNCostBasedTreeNodeEncFitnessWHeurCost other) {
		if ((getCost() + getHeuristicCost()) > (other.getCost() + other.getHeuristicCost())) {
			return 1;
		} else
			return ((getCost() + getHeuristicCost()) < (other.getCost() + other.getHeuristicCost())) ? -1 : 0;
	}
	
	public boolean equals(Object other) {
		if (other instanceof CPNCostBasedTreeNodeEncFitnessWHeurCost){
			CPNCostBasedTreeNodeEncFitnessWHeurCost otherObj = (CPNCostBasedTreeNodeEncFitnessWHeurCost) other;
			return (getCost() == otherObj.getCost()) && (getCurrEncMarking() == otherObj.getCurrEncMarking())
					&& (getCurrIndexOnTrace() == otherObj.getCurrIndexOnTrace());
		} else {
			return super.equals(other);
		}
	}

}
