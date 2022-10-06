package org.processmining.plugins.inductiveminer2.loginfo;

import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier.Transition;
import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.IntDfgImpl;
import org.processmining.plugins.inductiveminer2.helperclasses.MultiIntSet;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMTrace;
import org.processmining.plugins.inductiveminer2.logs.IMTraceIterator;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;

public class IMLog2IMLogInfoLifeCycle implements IMLog2IMLogInfo {

	private static class Count {
		IntDfg dfg = new IntDfgImpl();
		TIntIntHashMap minimumSelfDistances = IMLogInfo.createEmptyMinimumSelfDistancesMap();
		TIntObjectMap<MultiIntSet> minimumSelfDistancesBetween = IMLogInfo.createEmptyMinimumSelfDistancesBetweenMap();
		long numberOfEvents = 0;
		long numberOfActivityInstances = 0;
	}

	public IMLogInfo createLogInfo(IMLog log) {
		return log2logInfo(log);
	}

	public static IMLogInfo log2logInfo(IMLog log) {
		Count count = new Count();
		log2Dfg(log, count);

		count.dfg.collapseParallelIntoDirectly();

		return new IMLogInfo(count.dfg, count.minimumSelfDistancesBetween, count.minimumSelfDistances,
				count.numberOfEvents, count.numberOfActivityInstances, log.size());
	}

	private static void log2Dfg(IMLog log, Count count) {
		count.dfg = new IntDfgImpl();
		for (IMTraceIterator it = log.iterator(); it.hasNext();) {
			IMTrace trace = it.next();

			processTrace(log, it, count, trace.size());

			//count the number of completes (= the number of activity instances)
			for (it.itEventReset(); it.itEventHasNext();) {
				it.itEventNext();

				if (it.itEventGetLifeCycleTransition() == Transition.complete) {
					count.numberOfActivityInstances += 1;
				}
			}
			count.numberOfEvents += trace.size();
		}
	}

	private static void processTrace(IMLog log, IMTraceIterator it, Count count, int traceSize) {
		if (traceSize == 0) {
			count.dfg.addEmptyTraces(1);
			return;
		}

		//directly follows relation
		processDirectlyFollows(log, it, count, traceSize);

		//parallelism
		processParallel(log, it, count);

		//start/end activities
		processStartEnd(log, it, count);
	}

	private static void processStartEnd(IMLog log, IMTraceIterator it, Count count) {
		boolean activityOccurrenceCompleted = false;
		MultiIntSet activityOccurrencesEndedSinceLastStart = new MultiIntSet();
		MultiIntSet openActivityOccurrences = new MultiIntSet();
		for (it.itEventReset(); it.itEventHasNext();) {
			it.itEventNext();
			int activity = it.itEventGetActivityIndex();
			Transition lifeCycle = it.itEventGetLifeCycleTransition();

			if (lifeCycle == Transition.start) {
				//start event
				openActivityOccurrences.add(activity);
				if (!activityOccurrenceCompleted) {
					//no activity occurrence has been completed yet. Add to start events.
					count.dfg.getStartActivities().add(activity, 1);
				}
				activityOccurrencesEndedSinceLastStart.clear();
			} else if (lifeCycle == Transition.complete) {
				//complete event
				if (openActivityOccurrences.contains(activity)) {
					//this activity occurrence was open; close it
					openActivityOccurrences.remove(activity, 1);
					activityOccurrencesEndedSinceLastStart.add(activity);
				} else {
					//next front is non-started but complete

					if (!activityOccurrenceCompleted) {
						//no activity occurrence has been completed yet. Add to start events.
						count.dfg.getStartActivities().add(activity, 1);
					}
					activityOccurrenceCompleted = true;

					activityOccurrencesEndedSinceLastStart.clear();
					activityOccurrencesEndedSinceLastStart.add(activity);
				}
			}

			activityOccurrenceCompleted = activityOccurrenceCompleted || lifeCycle == Transition.complete;
		}
		count.dfg.getEndActivities().addAll(activityOccurrencesEndedSinceLastStart);
	}

	private static void processParallel(IMLog log, IMTraceIterator it, Count count) {
		MultiIntSet openActivityOccurrences = new MultiIntSet();

		for (it.itEventReset(); it.itEventHasNext();) {
			it.itEventNext();
			int activity = it.itEventGetActivityIndex();
			Transition lifeCycle = it.itEventGetLifeCycleTransition();

			switch (lifeCycle) {
				case complete :
					//this is a completion event
					openActivityOccurrences.remove(activity, 1);

					//this activity occurrence is parallel to all open activity occurrences
					for (int eventClass2 : openActivityOccurrences) {
						count.dfg.getConcurrencyGraph().addEdge(activity, eventClass2,
								openActivityOccurrences.getCardinalityOf(eventClass2));
					}
					break;
				case start :
					//this is a start event
					openActivityOccurrences.add(activity);
					break;
				default :
					break;

			}
		}
	}

	private static void processDirectlyFollows(IMLog log, IMTraceIterator it, Count count, int traceSize) {
		MultiIntSet openActivityInstances = new MultiIntSet();

		boolean isStart[] = new boolean[traceSize];

		int i = 0;
		for (it.itEventReset(); it.itEventHasNext();) {
			it.itEventNext();
			int activity = it.itEventGetActivityIndex();
			Transition lifeCycle = it.itEventGetLifeCycleTransition();
			
			count.dfg.touchActivity(activity);

			//this is a start event if the log says so, or if we see a complete without corresponding preceding start event. 
			boolean isStartEvent = lifeCycle == Transition.start || !openActivityInstances.contains(activity);
			boolean isCompleteEvent = lifeCycle == Transition.complete;
			isStart[i] = isStartEvent;

			if (isStartEvent) {
				//this is a start event, which means that it could have predecessors
				walkBack(it, isStart, log, i, count, activity);
			}
			if (isCompleteEvent && count != null) {
				//this is a complete event, add it to the activities
				count.dfg.addActivity(activity);
			}

			//update the open activity instances
			if (isCompleteEvent && !isStartEvent) {
				//if this ends an activity instance (and it was open already), remove it 
				openActivityInstances.remove(activity);
			}
			if (isStartEvent && !isCompleteEvent) {
				//if this starts an activity instance (and does not immediately close it), it is left open for now
				openActivityInstances.add(activity);
			}
			i++;
		}
	}

	private static void walkBack(IMTraceIterator it, boolean[] isStart, IMLog log, int i, Count count, int target) {
		try {
			it = it.clone();
		} catch (CloneNotSupportedException e) {
			//should never happen
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		MultiIntSet completes = new MultiIntSet();
		while (it.itEventHasPrevious()) {
			i--;
			it.itEventPrevious();
			int activity = it.itEventGetActivityIndex();
			Transition lifeCycle = it.itEventGetLifeCycleTransition();

			if (lifeCycle == Transition.complete) {
				completes.add(activity);
				count.dfg.getDirectlyFollowsGraph().addEdge(activity, target, 1);
			}
			if (isStart[i] && completes.contains(activity)) {
				return;
			}
		}
	}

}
