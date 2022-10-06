package org.processmining.plugins.inductiveminer2.framework.cutfinders;

import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.plugins.inductiveminer2.helperclasses.MultiIntSet;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public class CutFinderIMConcurrentWithMinimumSelfDistance implements CutFinder {
	public Cut findCut(final IMLog log, final IMLogInfo logInfo, final MinerState minerState) {
		return findCutImpl(log, logInfo, minerState);
	}

	public static Cut findCutImpl(IMLog log, final IMLogInfo logInfo, MinerState minerState) {
		return CutFinderIMConcurrent.findCut(logInfo.getDfg(), new Function<Integer, MultiIntSet>() {
			public MultiIntSet call(Integer input) throws Exception {
				return logInfo.getMinimumSelfDistanceBetween(input);
			}
		});
	}
}
