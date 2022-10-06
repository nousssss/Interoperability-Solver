package org.processmining.models.workshop.fbetancor.listcreators;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XTrace;

/**
 * Class for filling different lists with timestamp values. These lists consist
 * of the timestamps for all the events and for all the start times of the
 * traces.
 * 
 * @author R. Verhulst
 *
 */
public class TimestampLists {

	/**
	 * List of all the timestamps for the events of the event log.
	 */
	private List<XAttributeTimestamp> eventTimeStamps;

	/**
	 * List of all the timestamps of the start of every trace.
	 */
	private List<XAttributeTimestamp> startTraceTimestamps;
	
	private XAttributeTimestamp startTimestamp;
	
	private XAttributeTimestamp endTimestamp;

	/**
	 * List of all the timestamps of the start of every trace.
	 */
	private List<XAttributeTimestamp> endTraceTimestamps;
	
	/**
	 * List that keeps track of the current trace being analyzed for getting the
	 * information from.
	 */
	private List<XAttributeTimestamp> currentTraceList;

	/**
	 * List that keeps track for each trace the events and their timestamps.
	 */
	private List<List<XAttributeTimestamp>> traceEventsTimestamps;

	/**
	 * String that keeps track of the name of the current trace being checked.
	 * Used such that it is known when the next trace has been started for
	 * analysis.
	 */
	private String currentTrace;

	/**
	 * Initializing all the different lists.
	 */
	public void initialize() {
		eventTimeStamps = new ArrayList<XAttributeTimestamp>();
		startTraceTimestamps = new ArrayList<XAttributeTimestamp>();
		endTraceTimestamps = new ArrayList<XAttributeTimestamp>();
		currentTraceList = new ArrayList<XAttributeTimestamp>();
		traceEventsTimestamps = new ArrayList<List<XAttributeTimestamp>>();
		currentTrace = "";
	}

	/**
	 * Fill the lists with the required information.
	 * 
	 * @param trace
	 * @param att
	 */
	public void fillTimeList(XTrace trace, XAttribute att) {
		if (att.getKey().equals("time:timestamp")) {
			if (!currentTrace.equals(XConceptExtension.instance().extractName(trace))) {
				if (!currentTraceList.isEmpty()) {
					List<XAttributeTimestamp> copy = new ArrayList<XAttributeTimestamp>(currentTraceList);
					traceEventsTimestamps.add(copy);
					addStartTimestampTrace(copy);
				}
				currentTraceList = new ArrayList<XAttributeTimestamp>();
				currentTrace = XConceptExtension.instance().extractName(trace);
				currentTraceList.add((XAttributeTimestamp) att);
			}
			eventTimeStamps.add((XAttributeTimestamp) att);
			currentTraceList.add((XAttributeTimestamp) att);
		}
	}

	/**
	 * Sorts the timestamps within a trace, such that the start-time can be
	 * extracted. This makes sure that if a trace is unordered, the start time
	 * is still extracted. Simply taking the first value of a timestamp in a
	 * trace does not ensure the start-time.
	 * 
	 * @param timeStampsTrace
	 */
	private void addStartTimestampTrace(List<XAttributeTimestamp> timeStampsTrace) {
		int nrSwaps = -1;
		if (!timeStampsTrace.isEmpty()) {
			while (nrSwaps != 0) {
				nrSwaps = 1;
				nrSwaps--;

				XAttributeTimestamp temporary = timeStampsTrace.get(0);

				for (int i = 0; i < timeStampsTrace.size(); i++) {
					XAttributeTimestamp current = timeStampsTrace.get(i);
					if (temporary.getValueMillis() <= current.getValueMillis()) {
						temporary = current;
					} else {
						timeStampsTrace.get(i).setValue(temporary.getValue());
						timeStampsTrace.get(i - 1).setValue(current.getValue());
						nrSwaps++;
					}
				}
			}
			startTraceTimestamps.add(timeStampsTrace.get(0));
		}
	}
	
	/**
	 * Get method for the list of the start times of traces.
	 * 
	 * @return startTraceTimestamps
	 */
	public List<XAttributeTimestamp> getStartTimesTraces() {
		return startTraceTimestamps;
	}
	
	/**
	 * Get method for the list of the start times of traces.
	 * 
	 * @return startTraceTimestamps
	 */
	public List<XAttributeTimestamp> getEndTimesTraces() {
		return endTraceTimestamps;
	}
	
	/**
	 * Get method for the list of all the event timestamps.
	 * 
	 * @return eventTimeStamps
	 */
	public List<XAttributeTimestamp> getEventTimeStamps() {
		return eventTimeStamps;
	}

	/**
	 * Get method for the list that has each trace and its related event
	 * timestamps.
	 * 
	 * @return traceEventsTimestamps
	 */
	public List<List<XAttributeTimestamp>> getTraceTimeStamps() {
		return traceEventsTimestamps;
	}

}
