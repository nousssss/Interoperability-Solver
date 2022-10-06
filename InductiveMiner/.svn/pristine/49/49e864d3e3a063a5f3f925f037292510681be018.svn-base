package org.processmining.plugins.inductiveminer2.framework.basecases;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public class BaseCaseFinderEmptyTracesFiltering implements BaseCaseFinder {

	public EfficientTree findBaseCases(IMLog log, IMLogInfo logInfo, MinerState minerState) {
		if (logInfo.getDfg().getNumberOfEmptyTraces() != 0) {
			//the log contains empty traces

			if (logInfo.getDfg().getNumberOfEmptyTraces() < logInfo.getNumberOfTraces()
					* minerState.parameters.getNoiseThreshold()) {
				//there are not enough empty traces, the empty traces are considered noise

				InductiveMiner.debug(" base case: leave empty traces out", minerState);

				//filter the empty traces from the log and recurse
				IMLog sublog = BaseCaseFinderEmptyTraces.removeEpsilonTraces(log, minerState);

				if (minerState.isCancelled() || sublog == null) {
					return null;
				}

				return InductiveMiner.mineNode(sublog, minerState);
			}
		}
		return null;
	}
}
