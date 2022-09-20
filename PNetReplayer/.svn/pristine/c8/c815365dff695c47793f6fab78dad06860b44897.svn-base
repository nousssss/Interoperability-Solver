/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedprefix;

import java.util.Map;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayer.algorithms.AbstractDefaultPNReplayParam;

/**
 * @author aadrians
 * Oct 21, 2011
 *
 */
public class CostBasedPrefixParam extends AbstractDefaultPNReplayParam{
	private Integer maxNumOfStates = null;
	private Integer inappropriateTransFireCost = null;
	private Integer replayedEventCost = null;
	private Integer skippedEventCost = null;
	private Integer heuristicDistanceCost = null;
	private Integer selfExecInviTaskCost = null;
	private Integer selfExecRealTaskCost = null;
	private Boolean allowInviTaskMove = null;
	private Boolean allowRealTaskMove = null;
	private Boolean allowEventSkip = null;
	private Boolean allowExecWOTokens = null;
	private Boolean allowExecViolating = null;

	private Marking initialMarking = null;

	/**
	 * Default parameter
	 */
	public CostBasedPrefixParam(){
		maxNumOfStates = 1000000;
		inappropriateTransFireCost = null;
		replayedEventCost = 1;
		skippedEventCost = 5;
		heuristicDistanceCost = 1;
		selfExecInviTaskCost = 0;
		selfExecRealTaskCost = 2;
		allowInviTaskMove = true;
		allowRealTaskMove = true;
		allowEventSkip = true;
		allowExecWOTokens = false;
		allowExecViolating = false;
		initialMarking = new Marking();
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
	 * @return the maxNumOfStates
	 */
	public Integer getMaxNumOfStates() {
		return maxNumOfStates;
	}

	/**
	 * @param maxNumOfStates
	 *            the maxNumOfStates to set
	 */
	public void setMaxNumOfStates(Integer maxNumOfStates) {
		this.maxNumOfStates = maxNumOfStates;
	}

	/**
	 * @return the inappropriateTransFireCost
	 */
	public Integer getInappropriateTransFireCost() {
		return inappropriateTransFireCost;
	}

	/**
	 * @param inappropriateTransFireCost
	 *            the inappropriateTransFireCost to set
	 */
	public void setInappropriateTransFireCost(Integer inappropriateTransFireCost) {
		this.inappropriateTransFireCost = inappropriateTransFireCost;
	}

	/**
	 * @return the replayedEventCost
	 */
	public Integer getReplayedEventCost() {
		return replayedEventCost;
	}

	/**
	 * @param replayedEventCost
	 *            the replayedEventCost to set
	 */
	public void setReplayedEventCost(Integer replayedEventCost) {
		this.replayedEventCost = replayedEventCost;
	}

	/**
	 * @return the skippedEventCost
	 */
	public Integer getSkippedEventCost() {
		return skippedEventCost;
	}

	/**
	 * @param skippedEventCost
	 *            the skippedEventCost to set
	 */
	public void setSkippedEventCost(Integer skippedEventCost) {
		this.skippedEventCost = skippedEventCost;
	}

	/**
	 * @return the heuristicDistanceCost
	 */
	public Integer getHeuristicDistanceCost() {
		return heuristicDistanceCost;
	}

	/**
	 * @param heuristicDistanceCost
	 *            the heuristicDistanceCost to set
	 */
	public void setHeuristicDistanceCost(Integer heuristicDistanceCost) {
		this.heuristicDistanceCost = heuristicDistanceCost;
	}

	/**
	 * @return the selfExecInviTaskCost
	 */
	public Integer getSelfExecInviTaskCost() {
		return selfExecInviTaskCost;
	}

	/**
	 * @param selfExecInviTaskCost
	 *            the selfExecInviTaskCost to set
	 */
	public void setSelfExecInviTaskCost(Integer selfExecInviTaskCost) {
		this.selfExecInviTaskCost = selfExecInviTaskCost;
	}

	/**
	 * @return the selfExecRealTaskCost
	 */
	public Integer getSelfExecRealTaskCost() {
		return selfExecRealTaskCost;
	}

	/**
	 * @param selfExecRealTaskCost
	 *            the selfExecRealTaskCost to set
	 */
	public void setSelfExecRealTaskCost(Integer selfExecRealTaskCost) {
		this.selfExecRealTaskCost = selfExecRealTaskCost;
	}

	/**
	 * @return the allowInviTaskMove
	 */
	public Boolean getAllowInviTaskMove() {
		return allowInviTaskMove;
	}

	/**
	 * @param allowInviTaskMove
	 *            the allowInviTaskMove to set
	 */
	public void setAllowInviTaskMove(Boolean allowInviTaskMove) {
		this.allowInviTaskMove = allowInviTaskMove;
	}

	/**
	 * @return the allowRealTaskMove
	 */
	public Boolean getAllowRealTaskMove() {
		return allowRealTaskMove;
	}

	/**
	 * @param allowRealTaskMove
	 *            the allowRealTaskMove to set
	 */
	public void setAllowRealTaskMove(Boolean allowRealTaskMove) {
		this.allowRealTaskMove = allowRealTaskMove;
	}

	/**
	 * @return the allowEventSkip
	 */
	public Boolean getAllowEventSkip() {
		return allowEventSkip;
	}

	/**
	 * @param allowEventSkip
	 *            the allowEventSkip to set
	 */
	public void setAllowEventSkip(Boolean allowEventSkip) {
		this.allowEventSkip = allowEventSkip;
	}

	/**
	 * @return the allowExecWOTokens
	 */
	public Boolean getAllowExecWOTokens() {
		return allowExecWOTokens;
	}

	/**
	 * @param allowExecWOTokens
	 *            the allowExecWOTokens to set
	 */
	public void setAllowExecWOTokens(Boolean allowExecWOTokens) {
		this.allowExecWOTokens = allowExecWOTokens;
	}

	/**
	 * @return the allowExecViolating
	 */
	public Boolean getAllowExecViolating() {
		return allowExecViolating;
	}

	/**
	 * @param allowExecViolating
	 *            the allowExecViolating to set
	 */
	public void setAllowExecViolating(Boolean allowExecViolating) {
		this.allowExecViolating = allowExecViolating;
	}

	public void replaceTransitions(Map<Transition, Transition> configuration, boolean keepNonReplacedMapping) {
		// nothing needs to be done here
	}

	public void setFinalMarkings(Marking[] finalMarkings) {
		// nothing needs to be done here		
	}

}
