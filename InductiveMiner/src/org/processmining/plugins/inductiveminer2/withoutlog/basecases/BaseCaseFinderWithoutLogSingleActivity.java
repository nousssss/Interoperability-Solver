package org.processmining.plugins.inductiveminer2.withoutlog.basecases;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.withoutlog.InductiveMinerWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class BaseCaseFinderWithoutLogSingleActivity implements BaseCaseFinderWithoutLog {

	public EfficientTree findBaseCases(DfgMsd graph, MinerStateWithoutLog minerState) {

		if (graph.getNumberOfActivities() == 1 && graph.getNumberOfEmptyTraces() == 0
				&& !graph.getDirectlyFollowsGraph().getEdges().iterator().hasNext()) {
			//single activity

			InductiveMinerWithoutLog.debug(" base case: IM single activity", minerState);

			return InlineTree.leaf(graph.getActivityOfIndex(graph.getActivities().iterator().next()));
		}

		return null;
	}

	public long getEdges(DfgMsd graph) {
		long edges = 0;
		for (long edge : graph.getDirectlyFollowsGraph().getEdges()) {
			edges += graph.getDirectlyFollowsGraph().getEdgeWeight(edge);
		}
		return edges;
	}

}
