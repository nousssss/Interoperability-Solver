package org.processmining.plugins.inductiveminer2.framework.logsplitter;

import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public interface LogSplitter {

	IMLog[] split(IMLog log, IMLogInfo logInfo, Cut cut, MinerState minerState);

}
