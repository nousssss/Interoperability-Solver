package org.processmining.plugins.inductiveminer2.withoutlog.basecases;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.withoutlog.InductiveMinerWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class BaseCaseFinderWithoutLogSingleActivityFiltering implements BaseCaseFinderWithoutLog {

	public EfficientTree findBaseCases(DfgMsd graph, MinerStateWithoutLog minerState) {

		if (graph.getNumberOfActivities() == 1) {
			//the log contains just one activity

			//assuming application of the activity follows a geometric distribution, we estimate parameter ^p

			//calculate the event-per-trace size of the log
			double p = graph.getStartActivities().size()
					/ ((graph.getActivities().size() + graph.getStartActivities().size()) * 1.0);

			if (0.5 - minerState.parameters.getNoiseThreshold() <= p
					&& p <= 0.5 + minerState.parameters.getNoiseThreshold()) {
				//^p is close enough to 0.5, consider it as a single activity

				InductiveMinerWithoutLog.debug(" base case: single activity filtering", minerState);

				return InlineTree.leaf(graph.getActivityOfIndex(graph.getActivities().iterator().next()));
			}
			//else, the probability to stop is too low or too high, and we better output a flower model
		}

		return null;
	}
}
