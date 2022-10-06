package org.processmining.plugins.inductiveminer2.loginfo;

import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.IntDfgImpl;
import org.processmining.plugins.inductiveminer2.helperclasses.MultiIntSet;
import org.processmining.plugins.inductiveminer2.logs.IMEventIterator;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMLogImplPartialTraces;
import org.processmining.plugins.inductiveminer2.logs.IMTrace;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;

public class IMLog2IMLogInfoPartialTraces implements IMLog2IMLogInfo {

	public IMLogInfo createLogInfo(IMLog log) {
		return log2logInfo(log);
	}

	public static IMLogInfo log2logInfo(IMLog log) {

		assert (log instanceof IMLogImplPartialTraces);

		//initialise, read the log
		IntDfg dfg = new IntDfgImpl();
		TIntIntHashMap minimumSelfDistances = IMLogInfo.createEmptyMinimumSelfDistancesMap();
		TIntObjectMap<MultiIntSet> minimumSelfDistancesBetween = IMLogInfo.createEmptyMinimumSelfDistancesBetweenMap();
		long numberOfEvents = 0;
		long numberOfActivityInstances = 0;

		int fromEventClass;
		int toEventClass;

		//walk trough the log
		TIntIntMap eventSeenAt;
		TIntArrayList readTrace;

		int removedEmptyTraces = 0;
		for (IMTrace trace : log) {
			toEventClass = -1;
			fromEventClass = -1;

			int traceSize = 0;
			eventSeenAt = new TIntIntHashMap(10, 0.5f, Integer.MIN_VALUE, Integer.MIN_VALUE);
			readTrace = new TIntArrayList();

			for (IMEventIterator it = trace.iterator(); it.hasNext();) {
				it.nextFast();
				int ec = it.getActivityIndex();

				dfg.addActivity(ec);

				fromEventClass = toEventClass;
				toEventClass = ec;

				readTrace.add(toEventClass);

				if (eventSeenAt.containsKey(toEventClass)) {
					//we have detected an activity for the second time
					//check whether this is shorter than what we had already seen
					int oldDistance = Integer.MAX_VALUE;
					if (minimumSelfDistances.containsKey(toEventClass)) {
						oldDistance = minimumSelfDistances.get(toEventClass);
					}

					if (!minimumSelfDistances.containsKey(toEventClass)
							|| traceSize - eventSeenAt.get(toEventClass) <= oldDistance) {
						//keep the new minimum self distance
						int newDistance = traceSize - eventSeenAt.get(toEventClass);
						if (oldDistance > newDistance) {
							//we found a shorter minimum self distance, record and restart with a new multiset
							minimumSelfDistances.put(toEventClass, newDistance);

							minimumSelfDistancesBetween.put(toEventClass, new MultiIntSet());
						}

						//store the minimum self-distance activities
						MultiIntSet mb = minimumSelfDistancesBetween.get(toEventClass);
						mb.addAll(readTrace.subList(eventSeenAt.get(toEventClass) + 1, traceSize));
					}
				}
				eventSeenAt.put(toEventClass, traceSize);
				{
					if (fromEventClass >= 0) {
						//add edge to directly follows graph
						dfg.getDirectlyFollowsGraph().addEdge(fromEventClass, toEventClass, 1);
					} else {
						//add edge to start activities

						if (((IMLogImplPartialTraces) log).isStartReliable(trace.getTraceIndex())) {
							dfg.getStartActivities().add(toEventClass, 1);
						}
					}
				}

				traceSize += 1;
			}

			numberOfEvents += trace.size();
			numberOfActivityInstances += trace.size();

			//process end activities
			if (toEventClass >= 0) {
				if (((IMLogImplPartialTraces) log).isEndReliable(trace.getTraceIndex())) {
					dfg.getEndActivities().add(toEventClass, 1);
				}
			}

			//process empty traces
			if (traceSize == 0) {
				if (((IMLogImplPartialTraces) log).isStartReliable(trace.getTraceIndex())
						&& ((IMLogImplPartialTraces) log).isEndReliable(trace.getTraceIndex())) {
					dfg.addEmptyTraces(1);
				} else {
					removedEmptyTraces++;
				}
			}
		}

		return new IMLogInfo(dfg, minimumSelfDistancesBetween, minimumSelfDistances, numberOfEvents,
				numberOfActivityInstances, log.size() - removedEmptyTraces);
	}

}
