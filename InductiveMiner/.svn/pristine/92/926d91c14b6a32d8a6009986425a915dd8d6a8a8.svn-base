package org.processmining.plugins.inductiveminer2.framework.logsplitter;

import java.util.List;

import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.helperclasses.Arrays;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMTraceIterator;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

import gnu.trove.set.TIntSet;

public class LogSplitterOr implements LogSplitter {

	public IMLog[] split(IMLog log, IMLogInfo logInfo, Cut cut, MinerState minerState) {
		return split(log, cut.getPartition(), minerState);
	}

	public static IMLog[] split(IMLog log, List<TIntSet> partition, MinerState minerState) {
		IMLog[] result = new IMLog[partition.size()];
		for (TIntSet sigma : partition) {
			IMLog sublog = log.clone();
			for (IMTraceIterator it = sublog.iterator(); it.hasNext();) {
				it.nextFast();

				if (minerState.isCancelled()) {
					return null;
				}

				while (it.itEventHasNext()) {
					it.itEventNext();
					int c = it.itEventGetActivityIndex();
					if (!sigma.contains(c)) {
						it.itEventRemove();
					}
				}

				//remove empty traces, as this is an or-splitter
				if (it.isEmpty()) {
					it.remove();
				}
			}
			Arrays.add(result, sublog);
		}
		return result;
	}

}
