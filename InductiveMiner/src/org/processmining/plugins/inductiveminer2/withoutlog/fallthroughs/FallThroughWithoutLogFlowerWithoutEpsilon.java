package org.processmining.plugins.inductiveminer2.withoutlog.fallthroughs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class FallThroughWithoutLogFlowerWithoutEpsilon implements FallThroughWithoutLog {

	public EfficientTree fallThrough(DfgMsd graph, MinerStateWithoutLog minerState) {
		if (graph.getNumberOfActivities() < 1) {
			return null;
		} else if (graph.getNumberOfActivities() == 1) {
			return InlineTree.loop(InlineTree.leaf(graph.getActivityOfIndex(graph.getActivities().iterator().next())),
					InlineTree.tau(), InlineTree.tau());
		} else {
			List<EfficientTree> children = new ArrayList<>(graph.getNumberOfActivities());
			Iterator<Integer> it = graph.getActivities().iterator();
			while (it.hasNext()) {
				children.add(InlineTree.leaf(graph.getActivityOfIndex(it.next())));
			}
			return InlineTree.loop(InlineTree.xor(children), InlineTree.tau(), InlineTree.tau());
		}
	}
}