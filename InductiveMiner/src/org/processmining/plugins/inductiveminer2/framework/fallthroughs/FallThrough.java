package org.processmining.plugins.inductiveminer2.framework.fallthroughs;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public interface FallThrough {

	EfficientTree fallThrough(IMLog log, IMLogInfo logInfo, MinerState minerState);

}
