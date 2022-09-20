/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.tue.astar.AStarThread;
import nl.tue.astar.Tail;
import nl.tue.astar.Trace;
import nl.tue.astar.util.LinearTrace;
import nl.tue.astar.util.PartiallyOrderedTrace;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.impl.AbstractPDelegate;

/**
 * @author aadrians
 * Feb 27, 2013
 *
 */
public abstract class AbstractAllOptAlignmentsAlg<D extends AbstractPDelegate<T>,T extends Tail> implements IPNMatchInstancesLogReplayAlgorithm {
	/**
	 * Pointers to parameters in array of objects
	 */
	// static reference for GUI
	public static final int MAPTRANSTOCOST = 0;
	public static final int MAXEXPLOREDINSTANCES = 1;
	public static final int MAPXEVENTCLASSTOCOST = 2;

	// compied from express replay 
	protected int visitedStates = 0;
	protected int queuedStates = 0;
	protected int traversedArcs = 0;

	/**
	 * Imported parameters
	 */
	// required parameters for replay
	protected Map<Transition, Integer> mapTrans2Cost;
	protected Map<XEventClass, Integer> mapEvClass2Cost;
	protected XEventClassifier classifier;
	protected int maxNumOfStates;
	protected Marking initMarking;
	protected Marking[] finalMarkings;

	protected boolean compareEventClassList(D d, XTrace t1, XTrace t2) {
		if (t1.size() != t2.size()) {
			return false;
		}
		Iterator<XEvent> it = t2.iterator();
		for (XEvent e : t1) {
			if (!d.getClassOf(e).equals(d.getClassOf(it.next()))) {
				return false;
			}
		}
		return true;
	}


	/**
	 * get list of event class. Record the indexes of non-mapped event classes.
	 * 
	 * @param trace
	 * @param classes
	 * @param mapEvClass2Trans
	 * @param listMoveOnLog
	 * @return
	 */
	protected Trace getLinearTrace(XLog log, int trace, AbstractPDelegate<?> delegate) {
		int s = log.get(trace).size();
		String name = XConceptExtension.instance().extractName(log.get(trace));
		if (name == null || name.isEmpty()) {
			name = "Trace " + trace;
		}
		TIntList activities = new TIntArrayList(s);
		for (int i = 0; i < s; i++) {
			int act = delegate.getActivityOf(trace, i);
			if (act != AStarThread.NOMOVE) {
				activities.add(act);
			}
		}

		LinearTrace result = new LinearTrace(name, activities);

		return result;
	}

	protected Trace getPartiallyOrderedTrace(XLog log, int trace, AbstractPDelegate<?> delegate) {
		int s = log.get(trace).size();
		String name = XConceptExtension.instance().extractName(log.get(trace));
		if (name == null || name.isEmpty()) {
			name = "Trace " + trace;
		}

		TIntList activities = new TIntArrayList(s);
		List<int[]> predecessors = new ArrayList<int[]>();
		Date lastTime = null;
		TIntList pre = new TIntArrayList();
		int previousIndex = -1;
		for (int i = 0; i < s; i++) {
			int act = delegate.getActivityOf(trace, i);
			if (act != AStarThread.NOMOVE) {

				XEvent event = log.get(trace).get(i);
				Date timestamp = XTimeExtension.instance().extractTimestamp(event);

				activities.add(act);

				if (lastTime == null) {
					// first event
					predecessors.add(null);
					pre.add(i);
				} else if (timestamp.equals(lastTime)) {
					// timestamp is the same as the last event.
					pre.add(i);
					if (previousIndex >= 0) {
						predecessors.add(new int[] { previousIndex });
					} else {
						predecessors.add(null);
					}
				} else {
					// timestamp is different from the last event.
					predecessors.add(pre.toArray());
					previousIndex = i - 1;
					pre = new TIntArrayList();
					pre.add(i);
				}
				lastTime = timestamp;

			}
		}

		Trace result;
		// predecessors[i] holds all predecessors of event at index i
		result = new PartiallyOrderedTrace(name, activities.toArray(), predecessors.toArray(new int[0][]));
		return result;
	}

}
