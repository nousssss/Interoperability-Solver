/**
 * 
 */
package org.processmining.plugins.replayer.replayresult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * @author aadrians
 * 
 */
public class SyncReplayResult {
	private List<Object> nodeInstance = null;
	private List<StepTypes> stepTypes = null;	
	private SortedSet<Integer> traceIndex = new TreeSet<Integer>();
	private boolean isReliable = false;

	/**
	 * Information and key to stored information
	 */
	private Map<String, Double> info = new HashMap<String, Double>(3);
	
	@SuppressWarnings("unused")
	private SyncReplayResult() {
	};

	public SyncReplayResult(List<Object> nodeInstance, List<StepTypes> stepTypes, int traceIndex) {
		this.nodeInstance = nodeInstance;
		this.stepTypes = stepTypes;
		this.traceIndex.add(traceIndex);
	}

	/**
	 * @return the info
	 */
	public Map<String, Double> getInfo() {
		return info;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(Map<String, Double> info) {
		this.info = info;
	}
	
	/**
	 * add additional info to sync replay result
	 * @param property
	 * @param value
	 */
	public void addInfo(String property, Double value){
		info.put(property, value);
	}

	public void addNewCase(int traceIndex) {
		this.traceIndex.add(traceIndex);
	}

	/**
	 * @return the traceIndex
	 */
	public SortedSet<Integer> getTraceIndex() {
		return traceIndex;
	}

	/**
	 * @param traceIndex
	 *            the traceIndex to set
	 */
	public void setTraceIndex(SortedSet<Integer> traceIndex) {
		this.traceIndex = traceIndex;
	}

	/**
	 * @return the nodeInstance
	 */
	public List<Object> getNodeInstance() {
		return nodeInstance;
	}

	/**
	 * @param nodeInstance
	 *            the nodeInstance to set
	 */
	public void setNodeInstance(List<Object> nodeInstance) {
		this.nodeInstance = nodeInstance;
	}

	/**
	 * @return the stepTypes
	 */
	public List<StepTypes> getStepTypes() {
		return stepTypes;
	}

	/**
	 * @param stepTypes
	 *            the stepTypes to set
	 */
	public void setStepTypes(List<StepTypes> stepTypes) {
		this.stepTypes = stepTypes;
	}

	public void setReliable(boolean isReliable) {
		this.isReliable = isReliable;
	}

	public boolean isReliable() {
		return isReliable;
	}

}