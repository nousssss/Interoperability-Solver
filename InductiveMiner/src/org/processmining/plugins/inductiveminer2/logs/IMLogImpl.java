package org.processmining.plugins.inductiveminer2.logs;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.util.ArrayUtils;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier.Transition;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.custom_hash.TObjectIntCustomHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.strategy.HashingStrategy;

public class IMLogImpl implements IMLog {

	private TObjectIntMap<String> activity2index;
	private String[] index2activity;
	private long[][] events;

	public IMLogImpl(XLog xLog, XEventClassifier classifier, XLifeCycleClassifier lifeCycleClassifier) {
		this(xLog.size());
		int traceIndex = 0;
		for (XTrace trace : xLog) {
			events[traceIndex] = new long[trace.size()];
			int eventIndex = 0;
			for (XEvent event : trace) {
				String activity = classifier.getClassIdentity(event);
				int activityIndex = activity2index.putIfAbsent(activity, activity2index.size());
				if (activityIndex == activity2index.getNoEntryValue()) {
					//new activity
					activityIndex = activity2index.size() - 1;
				}

				Transition lifeCycleTransition = lifeCycleClassifier.getLifeCycleTransition(event);
				int lifeCycleTransitionIndex = lifeCycleTransition.ordinal();

				events[traceIndex][eventIndex] = getEvent(activityIndex, lifeCycleTransitionIndex);
				eventIndex++;
			}
			traceIndex++;
		}
		finalise();
	}

	private IMLogImpl(int numberOfTraces) {
		events = new long[numberOfTraces][];
		HashingStrategy<String> strategy = new HashingStrategy<String>() {
			private static final long serialVersionUID = 1613251400608549656L;

			public int computeHashCode(String object) {
				return object.hashCode();
			}

			public boolean equals(String o1, String o2) {
				return o1.equals(o2);
			}
		};
		activity2index = new TObjectIntCustomHashMap<String>(strategy, 10, 0.5f, -1);
	}

	private void finalise() {
		index2activity = new String[activity2index.size()];
		for (String activity : activity2index.keySet()) {
			index2activity[activity2index.get(activity)] = activity;
		}
	}

	public int size() {
		return events.length;
	}

	public IMTraceIterator iterator() {
		return new IMTraceIterator() {

			private int now = -1;
			private int nowEvent = -1;

			public IMTrace next() {
				nextFast();
				return new IMTraceImpl(now);
			}

			public void reset() {
				now = -1;
				nowEvent = -1;
			}

			public boolean hasNext() {
				return now < events.length - 1;
			}

			public void nextFast() {
				now++;
				nowEvent = -1;
			}

			public void itEventReset() {
				nowEvent = -1;
			}

			public void itEventResetEnd() {
				nowEvent = events[now].length;
			}

			public void itEventNext() {
				nowEvent++;
			}

			public boolean itEventHasNext() {
				return nowEvent < events[now].length - 1;
			}

			public void itEventPrevious() {
				nowEvent--;
			}

			public boolean itEventHasPrevious() {
				return nowEvent > 0;
			}

			public int getTraceIndex() {
				return now;
			}

			public int itEventGetActivityIndex() {
				return getActivityIndex(events[now][nowEvent]);
			}

			public Transition itEventGetLifeCycleTransition() {
				return getLifeCycleTransition(events[now][nowEvent]);
			}

			public boolean isEmpty() {
				return events[now].length == 0;
			}

			public void remove() {
				removeTrace(now);
				now--;
			}

			public void itEventRemove() {
				removeEvent(now, nowEvent);
				nowEvent--;
			}

			public int itEventSplit() {
				int newTraceIndex = splitTrace(now, nowEvent);
				now++;
				nowEvent = 0;

				return newTraceIndex;
			}

			public IMTraceIterator clone() throws CloneNotSupportedException {
				return (IMTraceIterator) super.clone();
			}

			public void itEventSetActivityIndex(int activity) {
				events[now][nowEvent] = getEvent(activity, getLifeCycleTransition(events[now][nowEvent]).ordinal());
			}

			public void itEventSetLifeCycleTransition(Transition transition) {
				events[now][nowEvent] = getEvent(getActivityIndex(events[now][nowEvent]), transition.ordinal());
			}

			public int itEventGetEventIndex() {
				return nowEvent;
			}
		};
	}

	@Override
	public IMLogImpl clone() {
		IMLogImpl result;
		try {
			result = (IMLogImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

		result.events = new long[events.length][];
		for (int i = 0; i < events.length; i++) {
			result.events[i] = new long[events[i].length];
			System.arraycopy(events[i], 0, result.events[i], 0, events[i].length);
		}

		result.index2activity = ArrayUtils.copyOf(index2activity, index2activity.length);
		result.activity2index = new TObjectIntHashMap<>(10, 0.5f, -1); 
		result.activity2index.putAll(activity2index);

		return result;
	}

	public int getNumberOfActivities() {
		return index2activity.length;
	}

	public String getActivity(int index) {
		return index2activity[index];
	}

	public String[] getActivities() {
		return index2activity.clone();
	}

	public void setTrace(long[] trace, int index) {
		events[index] = trace;
	}

	public int addActivity(String activityName) {
		int activityIndex = activity2index.putIfAbsent(activityName, activity2index.size());
		if (activityIndex == activity2index.getNoEntryValue()) {
			//new activity
			activityIndex = activity2index.size() - 1;

			index2activity = ArrayUtils.copyOf(index2activity, index2activity.length + 1);
			index2activity[index2activity.length - 1] = activityName;
		}
		return activityIndex;
	}

	public static long getEvent(int activityIndex, int lifeCycleTransitionIndex) {
		return (((long) activityIndex) << 32) | ((lifeCycleTransitionIndex) & 0xffffffffL);
	}

	public static int getActivityIndex(long event) {
		return (int) (event >> 32);
	}

	public static Transition getLifeCycleTransition(long event) {
		return Transition.values()[(int) event];
	}

	public String toString() {
		int numberOfTraces = 10;
		StringBuilder result = new StringBuilder();
		for (int trace = 0; trace < events.length && trace < numberOfTraces; trace++) {
			toString(result, trace);
			result.append("\n");
		}
		if (events.length > numberOfTraces) {
			result.append(" ...\n");
		}
		return result.toString();
	}

	protected void toString(StringBuilder result, int traceIndex) {
		result.append(" <");
		for (int event = 0; event < events[traceIndex].length; event++) {
			//result.append(events[trace][event]);
			//result.append(" ");
			result.append(getActivityIndex(events[traceIndex][event]));
			result.append(" ");
			result.append(getActivity(getActivityIndex(events[traceIndex][event])));
			result.append(" ");
			result.append(getLifeCycleTransition(events[traceIndex][event]) == Transition.complete ? "co" : "st");
			if (event < events[traceIndex].length - 1) {
				result.append(", ");
			}
		}
		result.append(">");
	}

	public class IMTraceImpl implements IMTrace {

		private int traceIndex;

		public IMTraceImpl(int traceIndex) {
			this.traceIndex = traceIndex;
		}

		public IMEventIterator iterator() {
			return new IMEventIterator() {
				private int now = -1;

				public boolean hasNext() {
					return now < events[traceIndex].length - 1;
				}

				public IMEvent next() {
					now++;
					return new IMEventImpl(events[traceIndex][now]);
				}

				public int getActivityIndex() {
					return IMLogImpl.getActivityIndex(events[traceIndex][now]);
				}

				public Transition getLifeCycleTransition() {
					return IMLogImpl.getLifeCycleTransition(events[traceIndex][now]);
				}

				public void nextFast() {
					now++;
				}

				public void remove() {
					removeEvent(traceIndex, now);
					now--;
				}

				public int split() {
					int newTraceIndex = splitTrace(traceIndex, now);
					traceIndex++;
					now = 0;

					return newTraceIndex;
				}
			};
		}

		public int size() {
			return events[traceIndex].length;
		}

		public int getTraceIndex() {
			return traceIndex;
		}

		public int getActivityIndex(int eventIndex) {
			return IMLogImpl.getActivityIndex(events[traceIndex][eventIndex]);
		}

		public boolean isEmpty() {
			return events[traceIndex].length == 0;
		}

	}

	public void removeTrace(int traceIndex) {
		long[][] copied = new long[events.length - 1][];
		System.arraycopy(events, 0, copied, 0, traceIndex);
		System.arraycopy(events, traceIndex + 1, copied, traceIndex, events.length - traceIndex - 1);
		events = copied;
	}

	public void removeEvent(int traceIndex, int eventIndex) {
		long[] copied = new long[events[traceIndex].length - 1];
		System.arraycopy(events[traceIndex], 0, copied, 0, eventIndex);
		System.arraycopy(events[traceIndex], eventIndex + 1, copied, eventIndex,
				events[traceIndex].length - eventIndex - 1);
		events[traceIndex] = copied;
	}

	public int splitTrace(int traceIndex, int eventIndex) {
		//create an extra trace
		long[][] copied = new long[events.length + 1][];
		System.arraycopy(events, 0, copied, 1, events.length);
		events = copied;
		traceIndex++;

		//copy the part up till and excluding 'now' to the new trace
		events[0] = new long[eventIndex];
		System.arraycopy(events[traceIndex], 0, events[0], 0, eventIndex);

		//remove the part up till and excluding 'now' from this trace
		long[] newTrace = new long[events[traceIndex].length - eventIndex];
		System.arraycopy(events[traceIndex], eventIndex, newTrace, 0, events[traceIndex].length - eventIndex);
		events[traceIndex] = newTrace;

		return 0;
	}

}
