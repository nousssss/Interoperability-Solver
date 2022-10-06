package org.processmining.plugins.inductiveminer2.logs;

public interface IMTrace extends Iterable<IMEvent> {

	@Override
	public IMEventIterator iterator();

	/**
	 * 
	 * @return The number of events in the trace.
	 */
	public int size();

	public int getTraceIndex();

	public int getActivityIndex(int eventIndex);

	public boolean isEmpty();

}
