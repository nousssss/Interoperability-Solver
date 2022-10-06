package org.processmining.plugins.inductiveminer2.withoutlog.cutfinders;

import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntComponents;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class CutFinderWithoutLogIMLoopWithMinimumSelfDistance implements CutFinderWithoutLog {

	public Cut findCut(DfgMsd graph, MinerStateWithoutLog minerState) {
		
		/*
		 * 1: all activities should have an outgoing msd-edge
		 */
		for (int node : graph.getActivities()) {
			if (!graph.getMinimumSelfDistanceGraph().getOutgoingEdgesOf(node).iterator().hasNext()) {
				return null;
			}
		}

		Cut cut = org.processmining.plugins.inductiveminer2.framework.cutfinders.CutFinderIMLoop.findCut(graph);
		if (cut == null || !cut.isValid()) {
			return null;
		}

		IntComponents components = new IntComponents(cut.getPartition());

		//check the msd-footprint

		/*
		 * 4: redo-activities cannot have connections
		 * 
		 * We do this one first, as 2 and 3 might change when 4 is applied, but
		 * 4 will not change when 2 or 3 is applied.
		 */
		{
			for (long edgeIndex : graph.getMinimumSelfDistanceGraph().getEdges()) {
				int source = graph.getMinimumSelfDistanceGraph().getEdgeSource(edgeIndex);
				int target = graph.getMinimumSelfDistanceGraph().getEdgeTarget(edgeIndex);

				int componentSource = components.getComponentOf(source);
				int componentTarget = components.getComponentOf(target);

				if (componentSource != 0 && componentTarget != 0 && componentSource != componentTarget) {
					components.mergeComponentsOf(source, target);
				}
			}
		}

		return new Cut(Operator.loop, components.getComponents());
	}

}
