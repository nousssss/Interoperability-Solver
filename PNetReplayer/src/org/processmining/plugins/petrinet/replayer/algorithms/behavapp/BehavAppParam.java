/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.behavapp;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayer.algorithms.AbstractDefaultPNReplayParam;

/**
 * @author aadrians
 * Oct 21, 2011
 *
 */
public class BehavAppParam extends AbstractDefaultPNReplayParam {
	private Integer maxNumStates = null;
	private Boolean useLogWeight = null;
	private Map<XEventClass, Integer> xEventClassWeightMap = null;
	private Marking initialMarking = null;
	private Marking[] finalMarkings = null;

	public BehavAppParam(){
		maxNumStates = 200000;
		useLogWeight = false;
		xEventClassWeightMap = new HashMap<XEventClass, Integer>();
		initialMarking = new Marking();
		finalMarkings = new Marking[0];
	}
	
	/**
	 * @return the initialMarking
	 */
	public Marking getInitialMarking() {
		return initialMarking;
	}

	/**
	 * @param initialMarking
	 *            the initialMarking to set
	 */
	public void setInitialMarking(Marking initialMarking) {
		this.initialMarking = initialMarking;
	}

	/**
	 * @return the finalMarkings
	 */
	public Marking[] getFinalMarkings() {
		return finalMarkings;
	}

	/**
	 * @param finalMarkings
	 *            the finalMarkings to set
	 */
	public void setFinalMarkings(Marking[] finalMarkings) {
		this.finalMarkings = finalMarkings;
	}

	/**
	 * @return the maxNumStates
	 */
	public Integer getMaxNumStates() {
		return maxNumStates;
	}

	/**
	 * @param maxNumStates
	 *            the maxNumStates to set
	 */
	public void setMaxNumStates(Integer maxNumStates) {
		this.maxNumStates = maxNumStates;
	}

	/**
	 * @return the useLogWeight
	 */
	public Boolean getUseLogWeight() {
		return useLogWeight;
	}

	/**
	 * @param useLogWeight
	 *            the useLogWeight to set
	 */
	public void setUseLogWeight(Boolean useLogWeight) {
		this.useLogWeight = useLogWeight;
	}

	/**
	 * @return the xEventClassWeightMap
	 */
	public Map<XEventClass, Integer> getxEventClassWeightMap() {
		return xEventClassWeightMap;
	}

	/**
	 * @param xEventClassWeightMap
	 *            the xEventClassWeightMap to set
	 */
	public void setxEventClassWeightMap(Map<XEventClass, Integer> xEventClassWeightMap) {
		this.xEventClassWeightMap = xEventClassWeightMap;
	}

	public void replaceTransitions(Map<Transition, Transition> configuration, boolean keepNonReplacedMapping) {
		// nothing needs to be done here
	}

//	/**
//	 * @return the log
//	 */
//	public XLog getLog() {
//		return log;
//	}
//
//	/**
//	 * @param log
//	 *            the log to set
//	 */
//	public void setLog(XLog log) {
//		this.log = log;
//	}

//	public boolean isCorrectAlgorithm(IPNLogReplayAlgorithm replayAlgorithm) {
//		return (replayAlgorithm instanceof BehavioralCompleteReplayAlgorithmPrune);
//	}
//
//	public boolean checkCompletenessOfParameters() {
//		return ((maxNumStates != null) && (useLogWeight != null) && (xEventClassWeightMap != null) && (log != null)
//				&& (initialMarking != null) && (finalMarkings != null));
//	}
}
