package org.processmining.plugins.inductiveminer2.logs;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogStartEndComplete;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier;

/**
 * This log class keeps track of extra information, being whether the start and
 * end of the trace are reliable. Used for partial trace mining.
 * 
 * @author sander
 *
 */
public class IMLogImplPartialTraces extends IMLogImpl {

	boolean[] isStartReliable;
	boolean[] isEndReliable;

	public IMLogImplPartialTraces(XLog xLog, XEventClassifier classifier, XLifeCycleClassifier lifeCycleClassifier) {
		super(xLog, classifier, lifeCycleClassifier);

		isStartReliable = new boolean[xLog.size()];
		isEndReliable = new boolean[xLog.size()];

		int traceNr = 0;
		for (XTrace trace : xLog) {
			Boolean startComplete = IMLogStartEndComplete.getBooleanAttrFromTrace(trace, "startReliable");
			isStartReliable[traceNr] = startComplete == null || startComplete;

			Boolean endComplete = IMLogStartEndComplete.getBooleanAttrFromTrace(trace, "endReliable");
			isEndReliable[traceNr] = endComplete == null || endComplete;

			traceNr++;
		}
	}

	public boolean isStartReliable(int traceIndex) {
		return isStartReliable[traceIndex];
	}

	public void setStartReliable(int traceIndex, boolean b) {
		isStartReliable[traceIndex] = b;
	}

	public boolean isEndReliable(int traceIndex) {
		return isEndReliable[traceIndex];
	}

	public void setEndReliable(int traceIndex, boolean b) {
		isEndReliable[traceIndex] = b;
	}

	@Override
	public void removeTrace(int traceIndex) {
		super.removeTrace(traceIndex);

		{
			boolean[] copied = new boolean[isStartReliable.length - 1];
			System.arraycopy(isStartReliable, 0, copied, 0, traceIndex);
			System.arraycopy(isStartReliable, traceIndex + 1, copied, traceIndex,
					isStartReliable.length - traceIndex - 1);
			isStartReliable = copied;
		}

		{
			boolean[] copied = new boolean[isEndReliable.length - 1];
			System.arraycopy(isEndReliable, 0, copied, 0, traceIndex);
			System.arraycopy(isEndReliable, traceIndex + 1, copied, traceIndex, isEndReliable.length - traceIndex - 1);
			isEndReliable = copied;
		}
	}

	@Override
	public int splitTrace(int traceIndex, int eventIndex) {
		int newTraceIndex = super.splitTrace(traceIndex, eventIndex);
		assert (newTraceIndex == 0);

		//create an extra trace
		{
			boolean[] copied = new boolean[isStartReliable.length + 1];
			System.arraycopy(isStartReliable, 0, copied, 1, isStartReliable.length);
			isStartReliable = copied;
		}
		{
			boolean[] copied = new boolean[isEndReliable.length + 1];
			System.arraycopy(isEndReliable, 0, copied, 1, isEndReliable.length);
			isEndReliable = copied;
		}

		traceIndex++;

		//set the new trace
		isStartReliable[newTraceIndex] = isStartReliable[traceIndex];
		isEndReliable[newTraceIndex] = isEndReliable[traceIndex];

		return newTraceIndex;
	}

	@Override
	public IMLogImplPartialTraces clone() {
		IMLogImplPartialTraces result = (IMLogImplPartialTraces) super.clone();
		result.isStartReliable = isStartReliable.clone();
		result.isEndReliable = isEndReliable.clone();
		return result;
	}

	@Override
	protected void toString(StringBuilder result, int traceIndex) {
		result.append(isStartReliable(traceIndex));
		super.toString(result, traceIndex);
		result.append(isEndReliable(traceIndex));
	}
}
