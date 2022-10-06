package org.processmining.plugins.inductiveminer2.framework.basecases;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public class BaseCaseFinderEmptyLog implements BaseCaseFinder {

	public EfficientTree findBaseCases(IMLog log, IMLogInfo logInfo, MinerState minerState) {
		if (logInfo.getDfg().getNumberOfActivities() == 0) {
			//empty log

			InductiveMiner.debug(" base case: empty log", minerState);

			return InlineTree.tau();
		}

		return null;
	}

}
