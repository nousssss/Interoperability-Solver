package org.processmining.plugins.inductiveminer2.framework.cutfinders;

import java.util.List;

import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntComponents;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class CutFinderIMLoop implements CutFinder {

	public Cut findCut(IMLog log, IMLogInfo logInfo, MinerState minerState) {
		return findCut(logInfo.getDfg());
	}

	public static Cut findCut(IntDfg dfg) {
		//initialise the components: each activity gets its own
		IntComponents components = new IntComponents(dfg.getActivities().toSet().toArray());

		if (!dfg.hasStartActivities() || !dfg.hasEndActivities()) {
			return null;
		}

		//merge all start and end activities into one component
		{
			int pivot = dfg.getStartActivities().iterator().next();
			for (int e : dfg.getStartActivities()) {
				components.mergeComponentsOf(pivot, e);
			}
			for (int e : dfg.getEndActivities()) {
				components.mergeComponentsOf(pivot, e);
			}
		}

		//merge the other connected components
		for (long edgeIndex : dfg.getDirectlyFollowsGraph().getEdges()) {
			int source = dfg.getDirectlyFollowsGraph().getEdgeSource(edgeIndex);
			int target = dfg.getDirectlyFollowsGraph().getEdgeTarget(edgeIndex);
			if (!dfg.getStartActivities().contains(source)) {
				if (!dfg.getEndActivities().contains(source)) {
					if (!dfg.getStartActivities().contains(target)) {
						//if (!dfg.isEndActivity(target)) { //optimisation: do not perform this check
						//this is an edge inside a sub-component
						components.mergeComponentsOf(source, target);
						//} else {
						//target is an end but not a start activity
						//a redo cannot reach an end activity that is not a start activity
						//	components.mergeComponentsOf(source, target);
						//}
					}
				}
			} else {
				if (!dfg.getEndActivities().contains(source)) {
					//source is a start but not an end activity
					//a redo cannot be reachable from a start activity that is not an end activity
					components.mergeComponentsOf(source, target);
				}
			}
		}

		/*
		 * We have merged all sub-components. We only have to find out whether
		 * each sub-component belongs to the body or the redo.
		 */

		//make a list of sub-start and sub-endactivities
		TIntSet subStartActivities = new TIntHashSet();
		TIntSet subEndActivities = new TIntHashSet();
		for (long edgeIndex : dfg.getDirectlyFollowsGraph().getEdges()) {
			int source = dfg.getDirectlyFollowsGraph().getEdgeSource(edgeIndex);
			int target = dfg.getDirectlyFollowsGraph().getEdgeTarget(edgeIndex);

			if (!components.areInSameComponent(source, target)) {
				//target is an sub-end activity and source is a sub-start activity
				subEndActivities.add(source);
				subStartActivities.add(target);
			}
		}

		//a sub-end activity of a redo should have connections to all start activities
		for (int subEndActivity : subEndActivities.toArray()) {
			for (int startActivity : dfg.getStartActivities()) {
				if (components.areInSameComponent(subEndActivity, startActivity)) {
					//this subEndActivity is already in the body
					break;
				}
				if (!dfg.getDirectlyFollowsGraph().containsEdge(subEndActivity, startActivity)) {
					components.mergeComponentsOf(subEndActivity, startActivity);
					break;
				}
			}
		}

		//a sub-start activity of a redo should be connections from all end activities
		for (int subStartActivity : subStartActivities.toArray()) {
			for (int endActivity : dfg.getEndActivities()) {
				if (components.areInSameComponent(subStartActivity, endActivity)) {
					//this subStartActivity is already in the body
					break;
				}
				if (!dfg.getDirectlyFollowsGraph().containsEdge(endActivity, subStartActivity)) {
					components.mergeComponentsOf(subStartActivity, endActivity);
					break;
				}
			}
		}

		//put the start and end activity component first
		List<TIntSet> partition = components.getComponents();
		int pivot = dfg.getStartActivities().iterator().next();
		for (int i = 0; i < partition.size(); i++) {
			if (partition.get(i).contains(pivot)) {
				TIntSet swap = partition.get(0);
				partition.set(0, partition.get(i));
				partition.set(i, swap);
				break;
			}
		}

		return new Cut(Operator.loop, partition);
	}

}
