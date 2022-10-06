package org.processmining.plugins.inductiveminer2.withoutlog.fallthroughs;

import java.util.Iterator;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.withoutlog.InductiveMinerWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class FallThroughWithoutLogTauLoopStrict implements FallThroughWithoutLog {

	public EfficientTree fallThrough(DfgMsd graph, MinerStateWithoutLog minerState) {

		if (graph.getActivities().setSize() > 1 && graph.hasEndActivities() && graph.hasStartActivities()) {

			//try to find a tau loop
			DfgMsd sublog = graph.clone();
			if (filterGraph(sublog)) {

				InductiveMinerWithoutLog.debug(" fall through: tau loop strict", minerState);

				//making a tau loop, as split makes sense
				EfficientTree body = InductiveMinerWithoutLog.mineNode(sublog, minerState);

				if (minerState.isCancelled() || body == null) {
					return null;
				}

				return InlineTree.loop(body, InlineTree.tau(), InlineTree.tau());
			}
		}

		return null;
	}

	public static boolean filterGraph(DfgMsd graph) {
		boolean removed = false;
		for (Iterator<Long> it = graph.getDirectlyFollowsGraph().getEdges().iterator(); it.hasNext();) {
			long edgeIndex = it.next();
			int source = graph.getDirectlyFollowsGraph().getEdgeSource(edgeIndex);
			int target = graph.getDirectlyFollowsGraph().getEdgeTarget(edgeIndex);

			if (graph.getStartActivities().contains(target) && graph.getEndActivities().contains(source)) {
				it.remove();
				removed = true;
			}
		}
		return removed;
	}
}
