package org.processmining.plugins.inductiveminer2.framework.cutfinders;

import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public class DfgCutFinderSimple {

	public Cut findCut(IntDfg dfg, MinerState minerState) {
		Cut cut = CutFinderIMExclusiveChoice.findCut(dfg);

		if (cut != null) {
			return cut;
		}

		if (minerState.isCancelled()) {
			return null;
		}

		cut = CutFinderIMSequence.findCut(dfg);

		if (cut != null) {
			return cut;
		}

		if (minerState.isCancelled()) {
			return null;
		}

		cut = CutFinderIMConcurrent.findCut(dfg, null);

		if (cut != null) {
			return cut;
		}

		if (minerState.isCancelled()) {
			return null;
		}

		return CutFinderIMLoop.findCut(dfg);
	}

}
