/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util.statespaces;

import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * @author aadrians
 * Oct 24, 2011
 *
 */
public abstract class AbstractCPNCostBasedTreeNode<T extends AbstractCPNCostBasedTreeNode<T>> {
	protected T parent;
	
	// required to tell position of replay
	protected int currIndexOnTrace = 0;
	protected int currEncMarking;

	// required to trace back
	protected StepTypes latestStepType;

	/**
	 * the following attribute (relatedStepTypeObj) holds: 1. An encoded
	 * transition (integer) if the step is move on model only 2. null if the
	 * step is move on log only, because it can be traced from automaton 3. An
	 * encoded transition (integer) if the step is move on model/log
	 */
	protected Integer relatedStepTypeObj;

	/**
	 * @return the parent
	 */
	public T getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(T parent) {
		this.parent = parent;
	}
	
	/**
	 * @return the currIndexOnTrace
	 */
	public int getCurrIndexOnTrace() {
		return currIndexOnTrace;
	}

	/**
	 * @param currIndexOnTrace
	 *            the currIndexOnTrace to set
	 */
	public void setCurrIndexOnTrace(int currIndexOnTrace) {
		this.currIndexOnTrace = currIndexOnTrace;
	}

	/**
	 * @return the currEncMarking
	 */
	public int getCurrEncMarking() {
		return currEncMarking;
	}

	/**
	 * @param currEncMarking
	 *            the currEncMarking to set
	 */
	public void setCurrEncMarking(int currEncMarking) {
		this.currEncMarking = currEncMarking;
	}

	/**
	 * @return the latestStepType
	 */
	public StepTypes getLatestStepType() {
		return latestStepType;
	}

	/**
	 * @param latestStepType
	 *            the latestStepType to set
	 */
	public void setLatestStepType(StepTypes latestStepType) {
		this.latestStepType = latestStepType;
	}

	/**
	 * @return the relatedStepTypeObj
	 */
	public Integer getRelatedStepTypeObj() {
		return relatedStepTypeObj;
	}

	/**
	 * @param relatedStepTypeObj
	 *            the relatedStepTypeObj to set
	 */
	public void setRelatedStepTypeObj(Integer relatedStepTypeObj) {
		this.relatedStepTypeObj = relatedStepTypeObj;
	}
}
