package org.processmining.plugins.inductiveminer2.framework.fallthroughs;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public class FallThroughFlowerWithoutEpsilon implements FallThrough {

	public EfficientTree fallThrough(IMLog log, IMLogInfo logInfo, MinerState minerState) {
		String[] activities = logInfo.getActivityNames(log);
		if (activities.length < 1) {
			return null;
		} else if (activities.length == 1) {
			return InlineTree.loop(InlineTree.leaf(activities[0]), InlineTree.tau(), InlineTree.tau());
		} else {
			List<EfficientTree> children = new ArrayList<>(activities.length);
			for (int i = 0; i < activities.length; i++) {
				children.add(InlineTree.leaf(activities[i]));
			}
			return InlineTree.loop(InlineTree.xor(children), InlineTree.tau(), InlineTree.tau());
		}
	}

}
