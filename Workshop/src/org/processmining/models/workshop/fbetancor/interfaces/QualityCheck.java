package org.processmining.models.workshop.fbetancor.interfaces;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.workshop.fbetancor.constructors.Output;

/**
 * Interface for each quality check. Consists for check methods on each level of
 * input, and a 'closing' check after all the lists and bags have been filled.
 * Also the results of the quality checks needs to be accessed.
 * 
 * @author R. Verhulst
 *
 */
public interface QualityCheck {
	public void initialize();

	/**
	 * Check quality on event-log level.
	 * 
	 * @param eventlog
	 */
	public void checkQuality(XLog eventlog);

	/**
	 * Check quality on the trace level.
	 * 
	 * @param eventlog
	 * @param trace
	 */
	public void checkQuality(XLog eventlog, XTrace trace);

	/**
	 * Check quality on the trace-attribute level.
	 * 
	 * @param eventlog
	 * @param trace
	 * @param att
	 */
	public void checkQuality(XLog eventlog, XTrace trace, XAttribute att);

	/**
	 * Check quality on the event level.
	 * 
	 * @param eventlog
	 * @param trace
	 * @param event
	 */
	public void checkQuality(XLog eventlog, XTrace trace, XEvent event);

	/**
	 * Check quality on the event-attribute level.
	 * 
	 * @param eventlog
	 * @param trace
	 * @param event
	 * @param att
	 */
	public void checkQuality(XLog eventlog, XTrace trace, XEvent event, XAttribute att);

	/**
	 * Check quality on the specified lists that are required as input for the
	 * quality check.
	 * 
	 * @param list
	 */
	public void checkClear(CentralRegistryInterface list);

	/**
	 * Get method for the results of the quality check.
	 * 
	 * @return
	 */
	public Output getResult();
}
