package org.processmining.plugins.inductiveminer2.withoutlog.cutfinders;

import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class CutFinderWithoutLogIMConcurrent implements CutFinderWithoutLog {

	public Cut findCut(DfgMsd graph, MinerStateWithoutLog minerState) {
		return CutFinderWithoutLogIMConcurrentWithMinimumSelfDistance.findCut(graph, false);
	}

}
