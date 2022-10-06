package org.processmining.models.workshop.fbetancor.listcreators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

/**
 * Class for filling lists and bags with information about events. The class
 * fills a logBag: Bag with all the events over the whole log and their
 * attribute values. The class also fill a list of TraceBags, with for each
 * trace the events and their related attribute values. This is used for
 * checking duplicate events within a single trace, and duplicate events over
 * the whole log.
 * 
 * @author R. Verhulst
 *
 */
public class EventLists {

	/**
	 * List for each trace a bag of events and their related attributes.
	 */
	private List<Bag<List<String>>> listedTraceBag;

	/**
	 * Bag with all the events of the event log and their related attributes.
	 */
	private Bag<List<String>> logBag;

	/**
	 * List of all the event attributes. Is being cleared after having the event
	 * attributes added to the temporary bag.
	 */
	private ArrayList<String> eventAttributes;

	/**
	 * Temporary storage for an event and all its attributes.
	 */
	private Bag<List<String>> tempBag = new HashBag<List<String>>();

	public void initialize() {
		logBag = new HashBag<List<String>>();
		eventAttributes = new ArrayList<String>();
		listedTraceBag = new ArrayList<Bag<List<String>>>();
		tempBag = new HashBag<List<String>>();
	}

	/**
	 * Fills the list and bag with events and their related attribute values.
	 * 
	 * @param trace
	 * @param event
	 * @param att
	 */
	public void fillList(XTrace trace, XEvent event, XAttribute att) {
		if (event.getAttributes().size() == (eventAttributes.size() + 1)) {
			/*
			 * Last attribute of this Event, so add the Attribute and add to the
			 * Bags.
			 */
			eventAttributes.add(att.toString());
			tempBag.add((List<String>) eventAttributes.clone());

			/*
			 * Want to add no duplicate events within a trace, since we want to
			 * check for duplicates over the log (So over other traces).
			 */
			if (trace.size() == tempBag.size()) {
				listedTraceBag.add(new HashBag<List<String>>(tempBag));

				tempBag.uniqueSet();
				for (List<String> ele : tempBag) {
					logBag.add(ele);
				}

				tempBag.clear();
			}

			eventAttributes.clear();
		} else {
			// Same Event
			eventAttributes.add(att.toString());
		}
	}

	public Bag<List<String>> getLogBag() {
		return logBag;
	}

	public List<Bag<List<String>>> getTraceBagList() {
		return listedTraceBag;
	}

}
