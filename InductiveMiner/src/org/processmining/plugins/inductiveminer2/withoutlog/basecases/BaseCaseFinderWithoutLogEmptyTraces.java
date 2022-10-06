package org.processmining.plugins.inductiveminer2.withoutlog.basecases;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.withoutlog.InductiveMinerWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class BaseCaseFinderWithoutLogEmptyTraces implements BaseCaseFinderWithoutLog {

	public EfficientTree findBaseCases(DfgMsd graph, MinerStateWithoutLog minerState) {
		if (graph.getNumberOfEmptyTraces() != 0) {
			InductiveMinerWithoutLog.debug(" base case: remove empty traces; xor(tau, ..)", minerState);

			//filter empty traces
			graph = graph.clone();
			graph.setNumberOfEmptyTraces(0);

			//recurse
			EfficientTree child = InductiveMinerWithoutLog.mineNode(graph, minerState);

			return InlineTree.xor(child, InlineTree.tau());
		}

		return null;
	}

}
