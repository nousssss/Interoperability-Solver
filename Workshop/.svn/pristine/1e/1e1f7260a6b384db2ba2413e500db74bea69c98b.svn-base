package org.processmining.models.workshop.fbetancor.listcreators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

/**
 * Class that fills all the lists with traces and their events, such that
 * structure can be analyzed. Only the event names are logged, such that this
 * can be used for analyzing orders of events, sub-traces etc.
 * 
 * @author R. Verhulst
 *
 */
public class TraceLists {
	/**
	 * Counter that keeps track of the number of events in a trace. Needed for
	 * the check if the current trace being analyzed is done.
	 */
	private int eventCounter;
	
	/**
	 * List of all the events per trace. Used for storage.
	 */
	private ArrayList<String> traceEvents;
	
	/**
	 * List of all the events per trace. Used for storage-copy. 
	 */
	private ArrayList<String> traceEvents2;
	
	/**
	 * Bag that keeps track of all the traces and its events.
	 */
	private Bag<List<String>> traceList;
	
	/**
	 * List that keeps track of all the traces and its events.
	 */
	private List<List<String>> traceArrayList;

	/**
	 * Initializing all the lists and bags.
	 */
	public void initialize() {
		eventCounter = 0;
		traceEvents = new ArrayList<String>();
		traceEvents2 = new ArrayList<String>();
		traceList = new HashBag<List<String>>();
		traceArrayList = new ArrayList<List<String>>();
	}

	/**
	 * Fill the lists and bags with the required information for each trace and
	 * its events.
	 * 
	 * @param trace
	 * @param event
	 */
	public void fillTraceList(XTrace trace, XEvent event) {
		eventCounter++;

		/*
		 * The traceList doesn't contain all events yet.
		 */
		if (!(trace.size() == eventCounter)) {
			traceEvents.add(XConceptExtension.instance().extractName(event));
		} else {
			/*
			 * TraceList contains all the events.
			 */
			traceEvents.add(XConceptExtension.instance().extractName(event));
			traceList.add((List<String>) traceEvents.clone());
			traceEvents2.add(XConceptExtension.instance().extractName(trace));
			traceEvents2.addAll(traceEvents);
			traceArrayList.add((List<String>) traceEvents2.clone());

			/*
			 * New Trace, so clear the current Events and the counter.
			 */
			eventCounter = 0;
			traceEvents.clear();
			traceEvents2.clear();
		}
	}

	/**
	 * Get method for the List that keeps track of all the traces and its
	 * events.
	 * 
	 * @return traceArrayList
	 */
	public List<List<String>> getTraceArray() {
		return traceArrayList;
	}

	/**
	 * Get method for the bag that keeps track of all the traces and its events.
	 * 
	 * @return traceList
	 */
	public Bag<List<String>> getTraceList() {
		return traceList;
	}
}
