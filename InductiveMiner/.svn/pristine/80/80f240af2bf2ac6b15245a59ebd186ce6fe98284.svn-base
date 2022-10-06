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

public class FallThroughTauLoop implements FallThrough {

	public EfficientTree fallThrough(IMLog log, IMLogInfo logInfo, MinerState minerState) {

		if (logInfo.getDfg().getActivities().setSize() > 1) {

			//try to find a tau loop
			IMLog sublog = log.clone();
			filterTraces(sublog, logInfo.getDfg());

			if (sublog.size() > log.size()) {
				InductiveMiner.debug(" fall through: tau loop", minerState);
				//making a tau loop split makes sense

				EfficientTree body = InductiveMiner.mineNode(sublog, minerState);

				if (minerState.isCancelled() || body == null) {
					return null;
				}

				return InlineTree.loop(body, InlineTree.tau(), InlineTree.tau());
			}
		}

		return null;
	}

	public static void filterTraces(IMLog log, IntDfg dfg) {
		for (IMTraceIterator it = log.iterator(); it.hasNext();) {
			it.nextFast();
			boolean first = true;

			MultiIntSet openActivityInstances = new MultiIntSet();

			while (it.itEventHasNext()) {
				it.itEventNext();
				int activity = it.itEventGetActivityIndex();

				if (!first && dfg.getStartActivities().contains(activity)) {
					//we discovered a transition body -> body
					//check whether there are no open activity instances
					if (openActivityInstances.size() == 0) {
						it.itEventSplit();
						first = true;
					}
				}

				if (it.itEventGetLifeCycleTransition() == Transition.complete) {
					if (openActivityInstances.getCardinalityOf(activity) > 0) {
						openActivityInstances.remove(activity, 1);
					}
				} else if (it.itEventGetLifeCycleTransition() == Transition.start) {
					openActivityInstances.add(activity);
				}

				first = false;
			}
		}
	}
}
