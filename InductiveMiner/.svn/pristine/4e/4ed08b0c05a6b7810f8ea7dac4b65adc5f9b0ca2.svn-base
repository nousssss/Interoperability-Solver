package org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd;

import java.util.Iterator;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier;
import org.processmining.plugins.inductiveminer2.helperclasses.MultiIntSet;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMEventIterator;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMLogImpl;
import org.processmining.plugins.inductiveminer2.logs.IMTrace;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;

public class Log2DfgMsd {
	public static DfgMsd convert(XLog xLog, XEventClassifier activityClassifier,
			XLifeCycleClassifier lifeCycleClassifier) {

		IMLog log = new IMLogImpl(xLog, activityClassifier, lifeCycleClassifier);
		return convert(log);
	}

	public static DfgMsd convert(IMLog log) {
		DfgMsdImpl dfg = new DfgMsdImpl(log.getActivities());
		TIntIntHashMap minimumSelfDistances = IMLogInfo.createEmptyMinimumSelfDistancesMap();
		TIntObjectMap<MultiIntSet> minimumSelfDistancesBetween = IMLogInfo.createEmptyMinimumSelfDistancesBetweenMap();

		//walk trough the log
		for (IMTrace trace : log) {

			int toEventClass = -1;
			int fromEventClass = -1;

			int traceSize = 0;
			TIntIntHashMap eventSeenAt = new TIntIntHashMap();
			TIntList readTrace = new TIntArrayList();

			for (IMEventIterator it = trace.iterator(); it.hasNext();) {
				it.nextFast();
				int eventClass = it.getActivityIndex();

				dfg.addActivity(eventClass);

				fromEventClass = toEventClass;
				toEventClass = eventClass;

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
					if (fromEventClass != -1) {
						//add edge to directly follows graph
						dfg.getDirectlyFollowsGraph().addEdge(fromEventClass, toEventClass, 1);
					} else {
						//add edge to start activities
						dfg.getStartActivities().add(toEventClass, 1);
					}
				}

				traceSize += 1;
			}

			if (toEventClass != -1) {
				dfg.getEndActivities().add(toEventClass, 1);
			}

			if (traceSize == 0) {
				dfg.addEmptyTraces(1);
			}
		}

		for (TIntObjectIterator<MultiIntSet> it = minimumSelfDistancesBetween.iterator(); it.hasNext();) {
			it.advance();
			MultiIntSet tos = it.value();
			for (Iterator<Integer> it2 = tos.iterator(); it2.hasNext();) {
				int to = it2.next();
				long cardinality = tos.getCardinalityOf(to);
				dfg.getMinimumSelfDistanceGraph().addEdge(it.key(), to, cardinality);
			}
		}

		return dfg;
	}
}
