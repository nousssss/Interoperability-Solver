package org.processmining.plugins.inductiveminer2.framework.cutfinders;

import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public interface CutFinder {

	Cut findCut(IMLog log, IMLogInfo logInfo, MinerState minerState);

}
