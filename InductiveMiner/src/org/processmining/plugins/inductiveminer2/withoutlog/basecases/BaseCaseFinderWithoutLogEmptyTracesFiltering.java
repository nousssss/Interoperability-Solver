package org.processmining.plugins.inductiveminer2.withoutlog.basecases;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.inductiveminer2.withoutlog.InductiveMinerWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class BaseCaseFinderWithoutLogEmptyTracesFiltering implements BaseCaseFinderWithoutLog {

	public EfficientTree findBaseCases(DfgMsd graph, MinerStateWithoutLog minerState) {
		if (graph.getNumberOfEmptyTraces() != 0) {
			//the log contains empty traces

			if (graph.getNumberOfEmptyTraces() < graph.getStartActivities().size()
					* minerState.parameters.getNoiseThreshold()) {
				//there are not enough empty traces, the empty traces are considered noise

				InductiveMinerWithoutLog.debug(" base case: leave empty traces out", minerState);

				//filter the empty traces from the log and recurse
				DfgMsd subgraph = graph.clone();
				subgraph.setNumberOfEmptyTraces(0);

				if (minerState.isCancelled() || subgraph == null) {
					return null;
				}

				return InductiveMinerWithoutLog.mineNode(subgraph, minerState);
			}
		}
		return null;
	}
}
