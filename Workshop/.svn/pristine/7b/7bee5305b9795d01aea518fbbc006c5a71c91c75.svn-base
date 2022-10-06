package org.processmining.models.workshop.fbetancor.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.workshop.fbetancor.listcreators.AttributeLists;
import org.processmining.models.workshop.fbetancor.listcreators.EventLists;
import org.processmining.models.workshop.fbetancor.listcreators.TimestampLists;
import org.processmining.models.workshop.fbetancor.listcreators.TraceLists;

/**
 * MEMORYcentralRegistryInterface. This class defines the interface for the
 * centralRegistry, which keeps track of all the lists that are needed to
 * contain specific information for different quality checks. All these lists
 * and bags are being filled with the use of this interface.
 * 
 * @author R. Verhulst
 *
 */
public class CentralRegistryInterface {

	private List<ArrayList<String>> attributeList;
	private Bag<List<String>> logBag;
	private List<Bag<List<String>>> traceBag;
	private List<XAttributeTimestamp> startTimesTraces;
	private List<XAttributeTimestamp> eventTimeStamps;
	private List<List<XAttributeTimestamp>> traceEventTimestamp;
	private Bag<List<String>> bagStructureTraces;
	private List<List<String>> traceStructureList;
	private Bag<String> attributeNameBag;
	private AttributeLists fillAttList;
	private EventLists fillEventList;
	private TimestampLists fillTimeList;
	private TraceLists fillTraceList;
	private int totalEvents;

	/**
	 * Initializes all the different lists and bags that are being used for the
	 * plug-in.
	 */
	public void initialize() {
		List<ArrayList<String>> attributeList = new ArrayList<ArrayList<String>>();
		List<ArrayList<String>> attributeTypes = new ArrayList<ArrayList<String>>();
		Bag<List<String>> logBag = new HashBag<List<String>>();
		List<Bag<List<String>>> traceBag = new ArrayList<Bag<List<String>>>();
		List<XAttributeTimestamp> startTimesTraces = new ArrayList<XAttributeTimestamp>();
		List<XAttributeTimestamp> eventTimeStamps = new ArrayList<XAttributeTimestamp>();
		List<List<XAttributeTimestamp>> traceEventTimestamp = new ArrayList<List<XAttributeTimestamp>>();
		Bag<List<String>> bagStructureTraces = new HashBag<List<String>>();
		List<List<String>> traceStructureList = new ArrayList<List<String>>();
		Bag<String> attributeNameBag = new HashBag<String>();

		fillAttList = new AttributeLists();
		fillEventList = new EventLists();
		fillTimeList = new TimestampLists();
		fillTraceList = new TraceLists();

		fillAttList.initialize();
		fillEventList.initialize();
		fillTimeList.initialize();
		fillTraceList.initialize();

		totalEvents = 0;
	}

	/**
	 * Fill the lists and bags that require only the XLog as input.
	 * 
	 * @param log
	 */
	public void fill(XLog log) {

	}

	/**
	 * Fill the lists and bags that require only the XLog and XTrace as input.
	 * 
	 * @param log
	 * @param trace
	 */
	public void fill(XLog log, XTrace trace) {

	}

	/**
	 * Fill the lists and bags that require only the XLog, XTrace and the
	 * XAttribute of the Trace.
	 * 
	 * @param log
	 * @param trace
	 * @param att
	 */
	public void fill(XLog log, XTrace trace, XAttribute att) {

	}

	/**
	 * Fill the lists and bags that require only the XLog, XTrace and XEvent as
	 * input.
	 * 
	 * @param log
	 * @param trace
	 * @param event
	 */
	public void fill(XLog log, XTrace trace, XEvent event) {
		fillTraceList.fillTraceList(trace, event);
		totalEvents++;
	}

	/**
	 * Fill the lists and bags that require only the XLog, XTrace, XEvent and
	 * the XAttribute of that Event.
	 * 
	 * @param log
	 * @param trace
	 * @param event
	 * @param att
	 */
	public void fill(XLog log, XTrace trace, XEvent event, XAttribute att) {
		fillAttList.fillList(att);
		fillEventList.fillList(trace, event, att);
		fillTimeList.fillTimeList(trace, att);
	}

	/**
	 * Get method for the list that is filled with all the attributes.
	 * 
	 * @return attributeList
	 */
	public List<ArrayList<String>> getAttributeList() {
		return fillAttList.getAttributeValuesList();
	}
	
	/**
	 * Get method for the list that is filled with all the attribute types.
	 * 
	 * @return attributeTypeList
	 */
	public List<ArrayList<String>> getAttributeTypes() {
		return fillAttList.getAttributeTypeList();
	}


	/**
	 * Get method for the bag that is filled with the events over the whole log.
	 * 
	 * @return logBag
	 */
	public Bag<List<String>> getLogBag() {
		return fillEventList.getLogBag();
	}

	/**
	 * Get method for the bag that is filled with the events within each single
	 * trace of the log.
	 * 
	 * @return traceBag
	 */
	public List<Bag<List<String>>> getTraceBag() {
		return fillEventList.getTraceBagList();
	}

	/**
	 * Get method for all the start times of the traces.
	 * 
	 * @return startTimesTraces
	 */
	public List<XAttributeTimestamp> getStartTimesTraces() {
		return fillTimeList.getStartTimesTraces();
	}
	
	/**
	 * Get method for all the start times of the traces.
	 * 
	 * @return startTimesTraces
	 */
	public List<XAttributeTimestamp> getEndTimesTraces() {
		return fillTimeList.getStartTimesTraces();
	}

	/**
	 * Get method for all the start times of the events of the whole log.
	 * 
	 * @return eventTimeStamps
	 */
	public List<XAttributeTimestamp> getEventTimeStamps() {
		return fillTimeList.getEventTimeStamps();
	}

	/**
	 * Get method for a list of all the timestamps of the events of each single
	 * trace of the log.
	 * 
	 * @return traceEventTimestamp
	 */
	public List<List<XAttributeTimestamp>> getTraceEventTimestamps() {
		return fillTimeList.getTraceTimeStamps();
	}
	
	/**
	 * Get method for the bag of all the traces and their corresponding events.
	 * Not specifically ordered.
	 * 
	 * @return bagStructureTraces
	 */
	public Bag<List<String>> getBagStructureTraces() {
		return fillTraceList.getTraceList();
	}

	/**
	 * Get method for the list of all the traces and their corresponding events.
	 * Ordered as in the event log.
	 * 
	 * @return traceStructureList
	 */
	public List<List<String>> getTraceStructureList() {
		return fillTraceList.getTraceArray();
	}

	/**
	 * Get method for the bag of all attribute names that are present in the
	 * event log.
	 * 
	 * @return attributeNameBag
	 */
	public Bag<String> getAttributeNameBag() {
		return fillAttList.getAttributeNameBag();
	}

	/**
	 * Get method for the total number of events.
	 * 
	 * @return totalEvents
	 */
	public int getTotalEvents() {
		return totalEvents;
	}
}
