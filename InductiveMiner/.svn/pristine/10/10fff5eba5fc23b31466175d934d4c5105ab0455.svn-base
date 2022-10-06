package org.processmining.plugins.inductiveminer2.framework.logsplitter;

import java.util.List;

import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier.Transition;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.helperclasses.Arrays;
import org.processmining.plugins.inductiveminer2.helperclasses.MultiIntSet;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMEventIterator;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMTrace;
import org.processmining.plugins.inductiveminer2.logs.IMTraceIterator;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

import gnu.trove.set.TIntSet;

public class LogSplitterLoop implements LogSplitter {

	public IMLog[] split(IMLog log, IMLogInfo logInfo, Cut cut, MinerState minerState) {
		return split(log, cut.getPartition(), minerState);
	}

	public static IMLog[] split(IMLog log, List<TIntSet> list, MinerState minerState) {

		IMLog[] result = new IMLog[list.size()];
		boolean firstSigma = true;
		//walk through the partition
		for (TIntSet sigma : list) {
			IMLog sublog = log.clone();

			//			System.out.println("sigma " + sigma);

			//walk through traces
			for (IMTraceIterator itTrace = sublog.iterator(); itTrace.hasNext();) {

				if (minerState.isCancelled()) {
					return null;
				}

				IMTrace trace = itTrace.next();

				//				System.out.println(" trace " + trace);
				boolean lastIn = firstSigma; //whether the last seen event was in sigma
				boolean anyIn = false; //whether there is any event in this subtrace
				MultiIntSet openActivityInstances = new MultiIntSet();

				//walk through the events
				for (IMEventIterator itEvent = trace.iterator(); itEvent.hasNext();) {
					itEvent.nextFast();
					int eventClass = itEvent.getActivityIndex();
					Transition transition = itEvent.getLifeCycleTransition();

					//keep track of open activity instances (by consistency assumption, should work out)
					switch (transition) {
						case start :
							openActivityInstances.add(eventClass);
							break;
						case complete :
							openActivityInstances.remove(eventClass, 1);
							break;
						default :
							break;
					}

					if (sigma.contains(eventClass)) {
						//event of the sigma under consideration

						if (!lastIn && (firstSigma || anyIn)) {
							//this is the first activity of a new subtrace, so the part up till now is a completed subtrace

							itEvent.split();
							//							System.out.println("   split trace " + newTrace + " | " + trace);
						}
						lastIn = true;
						anyIn = true;

					} else {
						//event of another sigma

						//remove
						itEvent.remove();

						//the last seen event was not in sigma
						if (openActivityInstances.isEmpty()) {
							//if there are no activity instances open, we can split the trace further ahead
							lastIn = false;
						}
					}
				}

				if (!firstSigma && !anyIn) {
					itTrace.remove();
				}
			}
			firstSigma = false;
			Arrays.add(result, sublog);
		}

		return result;
	}

}
