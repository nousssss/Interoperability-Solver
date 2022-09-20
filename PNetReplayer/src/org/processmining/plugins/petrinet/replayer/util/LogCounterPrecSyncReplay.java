/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

/**
 * @author aadrians
 *
 */
public class LogCounterPrecSyncReplay {
	private Map<List<XEventClass>, AllSyncReplayResult> mapResult; 
	
	public LogCounterPrecSyncReplay(){
		mapResult = new HashMap<List<XEventClass>, AllSyncReplayResult>();
	}
	
	public synchronized void createKey(List<XEventClass> listEvtClass) {
		mapResult.put(listEvtClass, null);
	}

	public boolean containsKey(List<XEventClass> listEvtClass) {
		return mapResult.containsKey(listEvtClass);
	}

	public synchronized boolean contains(List<XEventClass> eventClassLst) {
		return mapResult.containsKey(eventClassLst);
	}

	public synchronized void inc(List<XEventClass> eventClassLst, int traceIndex) {
		mapResult.get(eventClassLst).addNewCase(traceIndex);
	}

	public synchronized void add(List<XEventClass> listEvtClass, List<List<Object>> nodeInstanceStrLst, 
			List<List<StepTypes>> stepTypesLst, int traceIndex){
		add(listEvtClass, nodeInstanceStrLst, stepTypesLst, traceIndex, true);
	}

	public synchronized void add(List<XEventClass> listEvtClass, List<List<Object>> nodeInstanceStrLst, 
			List<List<StepTypes>> stepTypesLst, int traceIndex, boolean isReliable){
		AllSyncReplayResult newRes = new AllSyncReplayResult(nodeInstanceStrLst, stepTypesLst, traceIndex, isReliable);
		mapResult.put(listEvtClass, newRes);
	}

	public synchronized Collection<AllSyncReplayResult> getResult() {
		return mapResult.values();
	}
}
