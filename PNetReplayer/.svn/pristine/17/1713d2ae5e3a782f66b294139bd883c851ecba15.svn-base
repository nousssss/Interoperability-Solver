/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util.statespaces;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
import org.processmining.framework.util.Pair;

/**
 * @author aadrians
 *
 */
public class CPNCostBasedTreeNode implements Comparable<CPNCostBasedTreeNode>{
	// helping variables
	private int currIndexOnTrace = 0; // index of the next event to be replayed
	
	// util variables
	private Bag<Short> currMarking;
	private List<Pair<Integer, Short>> duplicatesOnlyStep;
	private List<Pair<Integer, Short>> modelOnlyStep;
	private List<Pair<Integer, Short>> traceModelViolatingStep;
	private List<Integer> moveTraceOnlyStep;
	
	private int cost = 0; // cost
	
	public CPNCostBasedTreeNode (){
		currMarking = new HashBag<Short>();
		duplicatesOnlyStep = new LinkedList<Pair<Integer, Short>>();
		modelOnlyStep = new LinkedList<Pair<Integer,Short>>();
		traceModelViolatingStep = new LinkedList<Pair<Integer,Short>>();
		moveTraceOnlyStep = new LinkedList<Integer>();
	}
	
	public CPNCostBasedTreeNode (CPNCostBasedTreeNode otherNode){
		currMarking = new HashBag<Short>(otherNode.getCurrMarking());
		duplicatesOnlyStep = new LinkedList<Pair<Integer, Short>>(otherNode.getDuplicatesOnlyStep());
		modelOnlyStep = new LinkedList<Pair<Integer,Short>>(otherNode.getModelOnlyStep());
		traceModelViolatingStep = new LinkedList<Pair<Integer,Short>>(otherNode.getTraceModelViolatingStep());
		moveTraceOnlyStep = new LinkedList<Integer>(otherNode.getMoveTraceOnlyStep());
		cost = otherNode.getCost();
		currIndexOnTrace = otherNode.getCurrIndexOnTrace();
	}
	
	/**
	 * @return the currIndexOnTrace
	 */
	public int getCurrIndexOnTrace() {
		return currIndexOnTrace;
	}
		
	/**
	 * @param currIndexOnTrace the currIndexOnTrace to set
	 */
	public void setCurrIndexOnTrace(int currIndexOnTrace) {
		this.currIndexOnTrace = currIndexOnTrace;
	}
	/**
	 * @return the currMarking
	 */
	public Bag<Short> getCurrMarking() {
		return currMarking;
	}
	/**
	 * @param currMarking the currMarking to set
	 */
	public void setCurrMarking(Bag<Short> currMarking) {
		this.currMarking = currMarking;
	}
	/**
	 * @return the moveTraceOnlyStep
	 */
	public List<Integer> getMoveTraceOnlyStep() {
		return moveTraceOnlyStep;
	}
	/**
	 * @param moveTraceOnlyStep the moveTraceOnlyStep to set
	 */
	public void setMoveTraceOnlyStep(List<Integer> moveTraceOnlyStep) {
		this.moveTraceOnlyStep = moveTraceOnlyStep;
	}
	/**
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}
	/**
	 * @param cost the cost to set
	 */
	public void setCost(int cost) {
		this.cost = cost;
	}
	/**
	 * @return the duplicatesOnlyStep
	 */
	public List<Pair<Integer, Short>> getDuplicatesOnlyStep() {
		return duplicatesOnlyStep;
	}
	/**
	 * @param duplicatesOnlyStep the duplicatesOnlyStep to set
	 */
	public void setDuplicatesOnlyStep(List<Pair<Integer, Short>> duplicatesOnlyStep) {
		this.duplicatesOnlyStep = duplicatesOnlyStep;
	}
	/**
	 * @return the modelOnlyStep
	 */
	public List<Pair<Integer, Short>> getModelOnlyStep() {
		return modelOnlyStep;
	}
	/**
	 * @param modelOnlyStep the modelOnlyStep to set
	 */
	public void setModelOnlyStep(List<Pair<Integer, Short>> modelOnlyStep) {
		this.modelOnlyStep = modelOnlyStep;
	}
	/**
	 * @return the traceModelViolatingStep
	 */
	public List<Pair<Integer, Short>> getTraceModelViolatingStep() {
		return traceModelViolatingStep;
	}
	/**
	 * @param traceModelViolatingStep the traceModelViolatingStep to set
	 */
	public void setTraceModelViolatingStep(
			List<Pair<Integer, Short>> traceModelViolatingStep) {
		this.traceModelViolatingStep = traceModelViolatingStep;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof CPNCostBasedTreeNode){
			CPNCostBasedTreeNode nodeX = (CPNCostBasedTreeNode) o;
			int nodeXCost = nodeX.getCost();
			if (getCost() != nodeXCost){
				return false;
			} else {
				if (currIndexOnTrace != nodeX.getCurrIndexOnTrace()){
					return false;
				} else {
					return currMarking.equals(nodeX.getCurrMarking());
				}
			}
		} else {
			return false;
		}
	}
	
	@Override
	public int compareTo(CPNCostBasedTreeNode nodeX) {
		// instances are sorted based on cost and marking size.  
		int nodeXCost = nodeX.getCost();
		if (getCost() < nodeXCost){
			return -1;
		} else if (getCost() == nodeXCost){
			int currIndexOnTrace = getCurrIndexOnTrace(); 
			int nodeXcurrIndexOnTrace = nodeX.getCurrIndexOnTrace();
			if (currIndexOnTrace > nodeXcurrIndexOnTrace){
				return -1;
			} else if (currIndexOnTrace == nodeXcurrIndexOnTrace){
				int currMarkingSize = currMarking.size();
				int nodeXcurrMarkingSize = nodeX.getCurrMarking().size();
				if (currMarkingSize < nodeXcurrMarkingSize){ // less marking
					return -1;
				} else if (currMarkingSize > nodeXcurrMarkingSize){
					return 1;
				} else {
					if (currMarking.equals(nodeX.getCurrMarking())){ 
						return 0;
					} else {
						return -1;
					}
				}
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}
	
}
