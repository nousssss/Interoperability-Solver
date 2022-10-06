package org.processmining.plugins.inductiveminer2.framework.logsplitter;

import java.util.Collection;

import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMTraceIterator;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

import gnu.trove.set.TIntSet;

public class LogSplitterConcurrent implements LogSplitter {

	public IMLog[] split(IMLog log, IMLogInfo logInfo, Cut cut, MinerState minerState) {
		return split(log, cut.getPartition(), minerState);
	}

	public static IMLog[] split(IMLog log, Collection<TIntSet> partition, MinerState minerState) {
		IMLog[] result = new IMLog[partition.size()];
		int logIndex = 0;
		for (TIntSet sigma : partition) {
			IMLog sublog = log.clone();
			for (IMTraceIterator it = sublog.iterator(); it.hasNext();) {
				it.nextFast();

				if (minerState.isCancelled()) {
					return null;
				}

				while (it.itEventHasNext()) {
					it.itEventNext();
					if (!sigma.contains(it.itEventGetActivityIndex())) {
						it.itEventRemove();
					}
				}
			}
			result[logIndex] = sublog;
			logIndex++;
		}
		return result;
	}
}
