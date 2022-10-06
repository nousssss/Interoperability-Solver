package org.processmining.plugins.inductiveminer2.framework.fallthroughs;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier.Transition;
import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.MultiIntSet;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMTraceIterator;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public class FallThroughTauLoopStrict implements FallThrough {

	public EfficientTree fallThrough(IMLog log, IMLogInfo logInfo, MinerState minerState) {

		if (logInfo.getDfg().getActivities().setSize() > 1) {

			//try to find a tau loop
			IMLog sublog = log.clone();
			if (filterLog(sublog, logInfo.getDfg())) {

				InductiveMiner.debug(" fall through: tau loop strict", minerState);

				//making a tau loop, as split makes sense
				EfficientTree body = InductiveMiner.mineNode(sublog, minerState);

				if (minerState.isCancelled() || body == null) {
					return null;
				}

				return InlineTree.loop(body, InlineTree.tau(), InlineTree.tau());
			}
		}

		return null;
	}

	/**
	 * Split the trace on a crossing end to start
	 * 
	 * @param log
	 * @param dfg
	 * @return
	 */
	public static boolean filterLog(IMLog log, IntDfg dfg) {
		boolean somethingSplit = false;

		for (IMTraceIterator it = log.iterator(); it.hasNext();) {
			it.nextFast();

			MultiIntSet openActivityInstances = new MultiIntSet();

			boolean lastEnd = false;
			while (it.itEventHasNext()) {
				it.itEventNext();

				int activity = it.itEventGetActivityIndex();

				if (lastEnd && dfg.getStartActivities().contains(activity) && openActivityInstances.size() == 0) {
					it.itEventSplit();
					somethingSplit = true;
				}

				if (it.itEventGetLifeCycleTransition() == Transition.complete) {
					if (openActivityInstances.getCardinalityOf(activity) > 0) {
						openActivityInstances.remove(activity, 1);
					}
				} else if (it.itEventGetLifeCycleTransition() == Transition.start) {
					openActivityInstances.add(activity);
				}

				lastEnd = dfg.getEndActivities().contains(activity);
			}
		}
		return somethingSplit;
	}
}
