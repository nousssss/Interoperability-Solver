package org.processmining.plugins.inductiveminer2.framework.basecases;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMTraceIterator;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public class BaseCaseFinderEmptyTraces implements BaseCaseFinder {

	public EfficientTree findBaseCases(IMLog log, IMLogInfo logInfo, MinerState minerState) {
		if (logInfo.getDfg().getNumberOfEmptyTraces() != 0) {
			InductiveMiner.debug(" base case: remove empty traces; xor(tau, ..)", minerState);

			//filter empty traces
			IMLog sublog = removeEpsilonTraces(log, minerState);

			//recurse
			EfficientTree child = InductiveMiner.mineNode(sublog, minerState);

			return InlineTree.xor(child, InlineTree.tau());
		}

		return null;
	}

	public static IMLog removeEpsilonTraces(IMLog log, MinerState minerState) {
		IMLog sublog = log.clone();
		for (IMTraceIterator it = sublog.iterator(); it.hasNext();) {
			if (minerState.isCancelled()) {
				return null;
			}
			it.nextFast();
			if (it.isEmpty()) {
				it.remove();
			}
		}
		return sublog;
	}

}
