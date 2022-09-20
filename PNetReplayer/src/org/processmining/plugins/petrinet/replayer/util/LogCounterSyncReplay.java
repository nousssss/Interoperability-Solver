/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * @author aadrians
 * 
 */
public class LogCounterSyncReplay {
	private Map<List<XEventClass>, SyncReplayResult> inputFreq; // map id to occurrence

	// set of costs
	private int inappropriateTransFireCost;
	private int skippedEventCost;
	private int selfExecInviTaskCost;
	private int selfExecRealTaskCost;
	
	public LogCounterSyncReplay() {
		inputFreq = new HashMap<List<XEventClass>, SyncReplayResult>();
	}

	public void setCosts(int inappropriateTransFireCost, int skippedEventCost, int selfExecInviTaskCost,
			int selfExecRealTaskCost) {
		this.inappropriateTransFireCost = inappropriateTransFireCost;
		this.skippedEventCost = skippedEventCost;
		this.selfExecInviTaskCost = selfExecInviTaskCost;
		this.selfExecRealTaskCost = selfExecRealTaskCost;
	}

	/**
	 * @return the inappropriateTransFireCost
	 */
	public int getInappropriateTransFireCost() {
		return inappropriateTransFireCost;
	}

	/**
	 * @param inappropriateTransFireCost
	 *            the inappropriateTransFireCost to set
	 */
	public void setInappropriateTransFireCost(int inappropriateTransFireCost) {
		this.inappropriateTransFireCost = inappropriateTransFireCost;
	}

	/**
	 * @return the skippedEventCost
	 */
	public int getSkippedEventCost() {
		return skippedEventCost;
	}

	/**
	 * @param skippedEventCost
	 *            the skippedEventCost to set
	 */
	public void setSkippedEventCost(int skippedEventCost) {
		this.skippedEventCost = skippedEventCost;
	}

	/**
	 * @return the selfExecInviTaskCost
	 */
	public int getSelfExecInviTaskCost() {
		return selfExecInviTaskCost;
	}

	/**
	 * @param selfExecInviTaskCost
	 *            the selfExecInviTaskCost to set
	 */
	public void setSelfExecInviTaskCost(int selfExecInviTaskCost) {
		this.selfExecInviTaskCost = selfExecInviTaskCost;
	}

	/**
	 * @return the selfExecRealTaskCost
	 */
	public int getSelfExecRealTaskCost() {
		return selfExecRealTaskCost;
	}

	/**
	 * @param selfExecRealTaskCost
	 *            the selfExecRealTaskCost to set
	 */
	public void setSelfExecRealTaskCost(int selfExecRealTaskCost) {
		this.selfExecRealTaskCost = selfExecRealTaskCost;
	}

	public synchronized void createKey(List<XEventClass> listEvtClass) {
		inputFreq.put(listEvtClass, null);
	}

	public boolean containsKey(List<XEventClass> listEvtClass) {
		return inputFreq.containsKey(listEvtClass);
	}

	public synchronized boolean contains(List<XEventClass> eventClassLst) {
		return inputFreq.containsKey(eventClassLst);
	}

	public synchronized void inc(List<XEventClass> eventClassLst, int traceIndex) {
		inputFreq.get(eventClassLst).addNewCase(traceIndex);
	}

	public synchronized void add(List<XEventClass> listEvtClass, List<Object> nodeInstanceStrLst,
			List<StepTypes> stepTypesLst, int traceIndex, double totalTime) {
		add(listEvtClass, nodeInstanceStrLst, stepTypesLst, traceIndex, true, totalTime);
	}

	public synchronized void add(List<XEventClass> listEvtClass, List<Object> nodeInstanceStrLst,
			List<StepTypes> stepTypesLst, int traceIndex, boolean isReliable, double totalTime) {
		SyncReplayResult newRes = new SyncReplayResult(nodeInstanceStrLst, stepTypesLst, traceIndex);

		// calculate fitness based on node instance
		int boundCost = inappropriateTransFireCost > skippedEventCost ? inappropriateTransFireCost
				: skippedEventCost;
		int cost = 0;
		int costBound = 0;
		for (StepTypes stepType : stepTypesLst) {
			switch (stepType) {
				case L :
					cost += skippedEventCost;
					costBound += boundCost;
					break;
				case LMGOOD :
					costBound += boundCost;
					break;
				case MINVI :
					cost += selfExecInviTaskCost;
					break;
				case MREAL :
					cost += selfExecRealTaskCost;
					break;
				case LMNOGOOD :
					cost += inappropriateTransFireCost;
					costBound += boundCost;
					break;
			}
		}
		Map<String, Double> info = new HashMap<String, Double>(2);
		if (cost > 0){ // case might be empty due ignored events
			info.put(PNRepResult.TRACEFITNESS, 1 - ((double) cost / (double) costBound));	
		} else {
			info.put(PNRepResult.TRACEFITNESS, 1.0000);
		}
		info.put(PNRepResult.TIME, totalTime);
		newRes.setInfo(info);

		// calculate other information
		inputFreq.put(listEvtClass, newRes);
		newRes.setReliable(isReliable);
	}

	public synchronized Collection<SyncReplayResult> getResult() {
		return inputFreq.values();
	}
}
