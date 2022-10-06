package org.processmining.plugins.inductiveminer2.framework.cutfinders;

import java.util.List;

import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntConnectedComponents;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

import gnu.trove.set.TIntSet;

public class CutFinderIMExclusiveChoice implements CutFinder {

	public Cut findCut(final IMLog log, final IMLogInfo logInfo, final MinerState minerState) {
		return findCut(logInfo.getDfg());
	}

	public static Cut findCut(IntDfg dfg) {
		List<TIntSet> connectedComponents = IntConnectedComponents.compute(dfg.getDirectlyFollowsGraph());
		return new Cut(Operator.xor, connectedComponents);
	}
}
