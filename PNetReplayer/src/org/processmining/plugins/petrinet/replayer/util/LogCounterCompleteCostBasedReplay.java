/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * @author aadrians
 * 
 */
public class LogCounterCompleteCostBasedReplay {
	private Map<List<XEventClass>, SyncReplayResult> mapLstEvClass2Res;

	// set of costs
	private Map<Transition, Integer> mapTrans2Cost;
	private Map<XEventClass, Integer> mapEvClass2Cost;
	private int maxCostForMvOnLog = 0;
	private int baselineIfAllMvOnModel = -1;

	/**
	 * @return the baselineIfAllMvOnModel
	 */
	public int getBaselineIfAllMvOnModel() {
		return baselineIfAllMvOnModel;
	}

	/**
	 * @param baselineIfAllMvOnModel the baselineIfAllMvOnModel to set
	 */
	public void setBaselineIfAllMvOnModel(int baselineIfAllMvOnModel) {
		this.baselineIfAllMvOnModel = baselineIfAllMvOnModel;
	}

	/**
	 * @return the mapTrans2Cost
	 */
	public Map<Transition, Integer> getMapTrans2Cost() {
		return mapTrans2Cost;
	}

	/**
	 * @param mapTrans2Cost
	 *            the mapTrans2Cost to set
	 */
	public void setMapTrans2Cost(Map<Transition, Integer> mapTrans2Cost) {
		this.mapTrans2Cost = mapTrans2Cost;
	}

	/**
	 * @return the mapEvClass2Cost
	 */
	public Map<XEventClass, Integer> getMapEvClass2Cost() {
		return mapEvClass2Cost;
	}

	/**
	 * @param mapEvClass2Cost
	 *            the mapEvClass2Cost to set
	 */
	public void setMapEvClass2Cost(Map<XEventClass, Integer> mapEvClass2Cost) {
		this.mapEvClass2Cost = mapEvClass2Cost;

		// find the maximum cost
		for (XEventClass evClass : mapEvClass2Cost.keySet()) {
			int cost = mapEvClass2Cost.get(evClass);
			if (cost > maxCostForMvOnLog) {
				maxCostForMvOnLog = cost;
			}
		}
	}

	public LogCounterCompleteCostBasedReplay() {
		mapLstEvClass2Res = new HashMap<List<XEventClass>, SyncReplayResult>();
	}

	public synchronized void createKey(List<XEventClass> listEvtClass) {
		mapLstEvClass2Res.put(listEvtClass, null);
	}

	public boolean containsKey(List<XEventClass> listEvtClass) {
		return mapLstEvClass2Res.containsKey(listEvtClass);
	}

	public synchronized boolean contains(List<XEventClass> eventClassLst) {
		return mapLstEvClass2Res.containsKey(eventClassLst);
	}

	public synchronized void inc(List<XEventClass> eventClassLst, int traceIndex) {
		mapLstEvClass2Res.get(eventClassLst).addNewCase(traceIndex);
	}

	public synchronized void add(List<XEventClass> listEvtClass, List<Object> nodeInstanceStrLst,
			List<StepTypes> stepTypesLst, int traceIndex, int statesGenerated) {
		add(listEvtClass, nodeInstanceStrLst, stepTypesLst, traceIndex, true, statesGenerated);
	}

	public synchronized void add(List<XEventClass> listEvtClass, List<Object> nodeInstanceStrLst,
			List<StepTypes> stepTypesLst, int traceIndex, boolean isReliable, int statesGenerated) {
		SyncReplayResult newRes = new SyncReplayResult(nodeInstanceStrLst, stepTypesLst, traceIndex);

		// Fitness is divided into two: move on log ratio, move on model ratio
		// for move on log ratio
		int totalMoveOnLogCost = 0;
		int boundMoveOnLogCost = 0;

		// for move on model ratio
		int totalMoveOnModelCost = 0;
		int boundMoveOnModelCost = 0;

		int counter = 0;
		Iterator<Object> it = nodeInstanceStrLst.iterator();
		for (StepTypes stepType : stepTypesLst) {
			Object obj = it.next();
			switch (stepType) {
				case L :
					Integer costlog = mapEvClass2Cost.get(obj);
					if (costlog != null) {
						counter++; // move on log for mapped activity
					} else {
						costlog = maxCostForMvOnLog; // move on log for unmapped activity
					}
					totalMoveOnLogCost += costlog;
					boundMoveOnLogCost += costlog;
					
					break;
				case LMGOOD :
					Integer costMvLog = mapEvClass2Cost.get(listEvtClass.get(counter));
					if (costMvLog == null) {
						costMvLog = maxCostForMvOnLog;
					}
					boundMoveOnLogCost += costMvLog;
					boundMoveOnModelCost += mapTrans2Cost.get(obj);
					counter++;
					break;
				case MINVI :
					int costinvi = mapTrans2Cost.get(obj);
					totalMoveOnModelCost += costinvi;
					boundMoveOnModelCost += costinvi;
					break;
				case MREAL :
					int costreal = mapTrans2Cost.get(obj);
					totalMoveOnModelCost += costreal;
					boundMoveOnModelCost += costreal;
					break;
				case LMNOGOOD :
					// ignore from calculation
					counter++;
					break;
			}
		}
		Map<String, Double> info = new HashMap<String, Double>(3);
		if (boundMoveOnLogCost > 0) { // case might be empty due ignored events
			info.put(PNRepResult.MOVELOGFITNESS, 1 - ((double) totalMoveOnLogCost / (double) boundMoveOnLogCost));
		} else {
			info.put(PNRepResult.MOVELOGFITNESS, 1.0000);
		}
		if (boundMoveOnModelCost > 0) { // case might be empty due ignored events
			info.put(PNRepResult.MOVEMODELFITNESS,
					1 - ((double) totalMoveOnModelCost / (double) boundMoveOnModelCost));
		} else {
			info.put(PNRepResult.MOVEMODELFITNESS, 1.0000);
		}
		if (baselineIfAllMvOnModel > 0){
			info.put(PNRepResult.TRACEFITNESS, 1 - (((double)(totalMoveOnLogCost + totalMoveOnModelCost)) / ((double)(baselineIfAllMvOnModel + boundMoveOnLogCost))));
		}
		
		info.put(PNRepResult.RAWFITNESSCOST, (double)(totalMoveOnLogCost + totalMoveOnModelCost));
		info.put(PNRepResult.NUMSTATEGENERATED, (double) statesGenerated);
		newRes.setInfo(info);

		// calculate other information
		mapLstEvClass2Res.put(listEvtClass, newRes);
		newRes.setReliable(isReliable);
	}

	public synchronized Collection<SyncReplayResult> getResult() {
		return mapLstEvClass2Res.values();
	}
}
