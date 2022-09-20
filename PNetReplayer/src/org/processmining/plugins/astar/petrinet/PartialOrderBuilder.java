package org.processmining.plugins.astar.petrinet;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tue.astar.AStarThread;
import nl.tue.astar.util.PartiallyOrderedTrace;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.astar.petrinet.impl.AbstractPDelegate;

public interface PartialOrderBuilder {

	public static final PartialOrderBuilder DEFAULT = new PartialOrderBuilder() {
		public PartiallyOrderedTrace getPartiallyOrderedTrace(XLog log, int trace, AbstractPDelegate<?> delegate,
				TIntList unUsedIndices, TIntIntMap trace2orgTrace) {
			int s = log.get(trace).size();
			int[] idx = new int[s];
			String name = XConceptExtension.instance().extractName(log.get(trace));
			if (name == null || name.isEmpty()) {
				name = "Trace " + trace;
			}

			TIntList activities = new TIntArrayList(s);
			List<int[]> predecessors = new ArrayList<int[]>();
			Date lastTime = null;
			TIntList pre = new TIntArrayList();
			int previousIndex = -1;
			int currentIdx = 0;
			for (int i = 0; i < s; i++) {
				int act = delegate.getActivityOf(trace, i);
				if (act != AStarThread.NOMOVE) {
					trace2orgTrace.put(currentIdx, i);
					idx[i] = currentIdx;
					XEvent event = log.get(trace).get(i);
					Date timestamp = XTimeExtension.instance().extractTimestamp(event);

					activities.add(act);

					if (lastTime == null) {
						// first event
						predecessors.add(null);
					} else if (timestamp.equals(lastTime)) {
						// timestamp is the same as the last event.
						if (previousIndex >= 0) {
							predecessors.add(new int[] { previousIndex });
						} else {
							predecessors.add(null);
						}
					} else {
						// timestamp is different from the last event.
						predecessors.add(pre.toArray());
						previousIndex = idx[i - 1];
						pre = new TIntArrayList();
					}
					pre.add(currentIdx);
					lastTime = timestamp;
					currentIdx++;
				} else {
					unUsedIndices.add(i);
				}
			}

			PartiallyOrderedTrace result;
			// predecessors[i] holds all predecessors of event at index i
			result = new PartiallyOrderedTrace(name, activities.toArray(), predecessors.toArray(new int[0][]));
			return result;
		}

	};

	public PartiallyOrderedTrace getPartiallyOrderedTrace(XLog log, int trace, AbstractPDelegate<?> delegate,
			TIntList unUsedIndices, TIntIntMap trace2orgTrace);
}
