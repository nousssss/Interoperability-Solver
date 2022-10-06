package org.processmining.plugins.inductiveminer2.logs;

import java.util.Iterator;

import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier.Transition;

public interface IMEventIterator extends Iterator<IMEvent> {

	public int getActivityIndex();

	public Transition getLifeCycleTransition();

	public void nextFast();

	public void remove();

	/**
	 * see IMLog.splitTrace()
	 * @return the index of the inserted trace.
	 */
	public int split();

}
