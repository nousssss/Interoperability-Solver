package org.processmining.plugins.inductiveminer2.framework.basecases;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public class BaseCaseFinderSingleActivityFiltering implements BaseCaseFinder {

	public EfficientTree findBaseCases(IMLog log, IMLogInfo logInfo, MinerState minerState) {

		if (logInfo.getDfg().getNumberOfActivities() == 1) {
			//the log contains just one activity

			//assuming application of the activity follows a geometric distribution, we estimate parameter ^p

			//calculate the event-per-trace size of the log
			double p = logInfo.getNumberOfTraces()
					/ ((logInfo.getNumberOfActivityInstances() + logInfo.getNumberOfTraces()) * 1.0);

			if (0.5 - minerState.parameters.getNoiseThreshold() <= p
					&& p <= 0.5 + minerState.parameters.getNoiseThreshold()) {
				//^p is close enough to 0.5, consider it as a single activity

				InductiveMiner.debug(" base case: single activity filtering", minerState);

				return InlineTree.leaf(log.getActivity(logInfo.getDfg().getActivities().iterator().next()));
			}
			//else, the probability to stop is too low or too high, and we better output a flower model
		}

		return null;
	}
}
