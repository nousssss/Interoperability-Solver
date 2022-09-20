package org.processmining.plugins.workshop.Khanhlv;
/**
 * 
 * 
 * @author khanhlv
 */

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class TraceLogs {

	private Map<Integer, Trace> traces;
	private Set<String> baseVector;
	private int countTrace = 0;
	private int distance = 1;

	/**
	 * Constructor TraceLogs and will initialize base vector by call the function
	 * makeBaseVt and with default distance = 1.
	 * 
	 * @param log
	 */
	public TraceLogs(XLog log, int dis) {
		distance = dis;
		traces = new TreeMap<Integer, Trace>();
		baseVector = new TreeSet<String>();
		makeEvent(log);
		makeBaseVt();
	}

	/**
	 * This function will return a list of trace, each trace just have name of event
	 * from log file.
	 * 
	 * @return TraceList
	 */
	public Map<Integer, Trace> getListTrace() {
		return traces;

	}

	//each event will have format:
	//{org:resource=UNDEFINED, concept:name=g, lifecycle:transition=complete, time:timestamp=2010-11-09T19:07:41.903+07:00}

	/**
	 * This function will take all name event from log and put it on a list for each
	 * trace.
	 * 
	 * @param log
	 */
	private void makeEvent(XLog log) {
		Iterator<XTrace> traceIter = log.iterator();
		while (traceIter.hasNext()) {
			countTrace++;
			traces.put(countTrace, new Trace(traceIter.next(), distance));
		}
	}

	/**
	 * This function will return the base vector of all event from log file.
	 * 
	 * @return BaseVecto
	 */
	public Set<String> getBaseVecto() {
		return baseVector;
	}

	/**
	 * This function will make a base vector from log input file
	 * 
	 */
	private void makeBaseVt() {
		for (int i = 1; i <= traces.size(); i++) {
			Iterator<String> eventIter = traces.get(i).listEvent().iterator();
			while (eventIter.hasNext()) {
				baseVector.add(eventIter.next());
			}
		}
	}

	/**
	 * 
	 * 
	 * @return number traces
	 */
	public int getCount() {
		return this.countTrace;
	}

	public void setDistance(int dis) {
		this.distance = dis;
	}

}
