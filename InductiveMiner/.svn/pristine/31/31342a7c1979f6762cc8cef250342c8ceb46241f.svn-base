package org.processmining.plugins.inductiveminer2.withoutlog.basecases;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.withoutlog.InductiveMinerWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class BaseCaseFinderWithoutLogSemiFlowerModel implements BaseCaseFinderWithoutLog {

	public EfficientTree findBaseCases(DfgMsd graph, MinerStateWithoutLog minerState) {

		if (graph.getNumberOfActivities() == 1 && graph.getNumberOfEmptyTraces() == 0) {
			//single activity in semi-flower model

			InductiveMinerWithoutLog.debug(" base case: single activity semi-flower model", minerState);

			EfficientTree activity = InlineTree.leaf(graph.getActivityOfIndex(graph.getActivities().iterator().next()));
			return InlineTree.loop(activity, InlineTree.tau(), InlineTree.tau());
		}

		return null;
	}
}
