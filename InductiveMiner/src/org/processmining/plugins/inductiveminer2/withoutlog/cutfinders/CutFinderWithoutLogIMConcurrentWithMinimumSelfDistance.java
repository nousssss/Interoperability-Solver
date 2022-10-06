package org.processmining.plugins.inductiveminer2.withoutlog.cutfinders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.processmining.plugins.InductiveMiner.Sets;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntComponents;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class CutFinderWithoutLogIMConcurrentWithMinimumSelfDistance implements CutFinderWithoutLog {
	public Cut findCut(DfgMsd graph, MinerStateWithoutLog minerState) {
		return findCut(graph, true);
	}

	public static Cut findCut(DfgMsd dfg, boolean considerMsd) {
		//noise filtering can have removed all start and end activities.
		//if that is the case, return
		if (!dfg.hasStartActivities() || !dfg.hasEndActivities()) {
			return null;
		}

		//initialise each activity as a component
		IntComponents components = new IntComponents(dfg.getActivities().toSet().toArray());

		//walk through all possible edges; if an edge is missing, then the source and target cannot be in different components.
		for (int e1 : dfg.getActivities()) {
			for (int e2 : dfg.getActivities()) {
				if (e1 < e2) {
					if (!components.areInSameComponent(e1, e2)) {
						if (!dfg.getDirectlyFollowsGraph().containsEdge(e1, e2)
								|| !dfg.getDirectlyFollowsGraph().containsEdge(e2, e1)) {
							components.mergeComponentsOf(e1, e2);
						}
					}
				}
			}
		}

		if (considerMsd) {
			//if wanted, apply an extension to the IM algorithm to account for loops that have the same directly follows graph as a parallel operator would have
			//make sure that activities on the minimum-self-distance-path are not separated by a parallel operator
			for (long edgeIndex : dfg.getMinimumSelfDistanceGraph().getEdges()) {
				int activity = dfg.getMinimumSelfDistanceGraph().getEdgeSource(edgeIndex);
				int activity2 = dfg.getMinimumSelfDistanceGraph().getEdgeTarget(edgeIndex);
				components.mergeComponentsOf(activity, activity2);
			}
		}

		//construct the components
		Collection<TIntSet> connectedComponents = components.getComponents();

		List<TIntSet> connectedComponents2 = ensureStartEndInEach(dfg, connectedComponents);

		if (connectedComponents2 == null) {
			return null;
		} else {
			return new Cut(Operator.concurrent, connectedComponents2);
		}
	}

	public static List<TIntSet> ensureStartEndInEach(IntDfg dfg, Collection<TIntSet> connectedComponents) {
		//not all connected components are guaranteed to have start and end activities. Merge those that do not.
		List<TIntSet> ccsWithStartEnd = new ArrayList<>();
		List<TIntSet> ccsWithStart = new ArrayList<>();
		List<TIntSet> ccsWithEnd = new ArrayList<>();
		List<TIntSet> ccsWithNothing = new ArrayList<>();
		for (TIntSet cc : connectedComponents) {
			Boolean hasStart = true;
			if (Sets.intersection(cc, dfg.getStartActivities().toSet()).isEmpty()) {
				hasStart = false;
			}
			Boolean hasEnd = true;
			if (Sets.intersection(cc, dfg.getEndActivities().toSet()).isEmpty()) {
				hasEnd = false;
			}
			if (hasStart) {
				if (hasEnd) {
					ccsWithStartEnd.add(cc);
				} else {
					ccsWithStart.add(cc);
				}
			} else {
				if (hasEnd) {
					ccsWithEnd.add(cc);
				} else {
					ccsWithNothing.add(cc);
				}
			}
		}

		//if there is no set with both start and end activities, there is no parallel cut
		if (ccsWithStartEnd.size() == 0) {
			return null;
		}

		//add full sets
		List<TIntSet> connectedComponents2 = new ArrayList<>(ccsWithStartEnd);
		//add combinations of end-only and start-only components
		Integer startCounter = 0;
		Integer endCounter = 0;
		while (startCounter < ccsWithStart.size() && endCounter < ccsWithEnd.size()) {
			TIntSet set = new TIntHashSet(10, 0.5f, Integer.MIN_VALUE);
			set.addAll(ccsWithStart.get(startCounter));
			set.addAll(ccsWithEnd.get(endCounter));
			connectedComponents2.add(set);
			startCounter++;
			endCounter++;
		}
		//the start-only components can be added to any set
		while (startCounter < ccsWithStart.size()) {
			TIntSet set = connectedComponents2.get(0);
			set.addAll(ccsWithStart.get(startCounter));
			connectedComponents2.set(0, set);
			startCounter++;
		}
		//the end-only components can be added to any set
		while (endCounter < ccsWithEnd.size()) {
			TIntSet set = connectedComponents2.get(0);
			set.addAll(ccsWithEnd.get(endCounter));
			connectedComponents2.set(0, set);
			endCounter++;
		}
		//the non-start-non-end components can be added to any set
		for (TIntSet cc : ccsWithNothing) {
			TIntSet set = connectedComponents2.get(0);
			set.addAll(cc);
			connectedComponents2.set(0, set);
		}
		return connectedComponents2;
	}
}
