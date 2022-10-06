package org.processmining.plugins.inductiveminer2.withoutlog.basecases;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.withoutlog.InductiveMinerWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class BaseCaseFinderWithoutLogEmptyLog implements BaseCaseFinderWithoutLog {

	public EfficientTree findBaseCases(DfgMsd graph, MinerStateWithoutLog minerState) {
		if (graph.getNumberOfActivities() == 0) {
			//empty log

			InductiveMinerWithoutLog.debug(" base case: empty log", minerState);

			return InlineTree.tau();
		}

		return null;
	}

}
