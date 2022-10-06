package org.processmining.plugins.inductiveminer2.framework.basecases;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public class BaseCaseFinderSingleActivity implements BaseCaseFinder {

	public EfficientTree findBaseCases(IMLog log, IMLogInfo logInfo, MinerState minerState) {

		if (logInfo.getDfg().getNumberOfActivities() == 1 && logInfo.getDfg().getNumberOfEmptyTraces() == 0
				&& logInfo.getNumberOfActivityInstances() == logInfo.getNumberOfTraces()) {
			//single activity

			InductiveMiner.debug(" base case: IM single activity", minerState);

			return InlineTree.leaf(log.getActivity(logInfo.getDfg().getActivities().iterator().next()));
		}

		return null;
	}

}
