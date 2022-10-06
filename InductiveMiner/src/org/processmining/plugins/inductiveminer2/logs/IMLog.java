package org.processmining.plugins.inductiveminer2.logs;

public interface IMLog extends Iterable<IMTrace>, Cloneable {

	/**
	 * 
	 * @return The number of traces in the log.
	 */
	public int size();

	/**
	 * Do not mix iterators when removing events.
	 */
	@Override
	public IMTraceIterator iterator();

	public int getNumberOfActivities();

	public String getActivity(int index);

	public String[] getActivities();

	/**
	 * Add an activity (if it was not yet added yet).
	 * 
	 * @param activityName
	 * @return the (possibly new) index of the activity.
	 */
	public int addActivity(String activityName);

	/**
	 * 
	 * @return a completely independent copy. This is the only method that
	 *         should be used in log splitting, such that extra information can
	 *         be preserved by the log implementation.
	 */
	public IMLog clone();

	public void removeTrace(int traceIndex);

	public void removeEvent(int traceIndex, int eventIndex);

	/**
	 * Split a trace: add a new trace at the start of the log, containing all
	 * events up till (excluding) eventIndex. Furthermore, remove all events up
	 * to (excluding) eventIndex from the trace at traceIndex.
	 * 
	 * @param traceIndex
	 * @param eventIndex
	 * @return the index of the inserted trace
	 */
	public int splitTrace(int traceIndex, int eventIndex);

}
