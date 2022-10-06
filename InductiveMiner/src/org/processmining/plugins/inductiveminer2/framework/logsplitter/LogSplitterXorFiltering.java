package org.processmining.plugins.inductiveminer2.framework.logsplitter;

import java.util.List;

import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMTraceIterator;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;

public class LogSplitterXorFiltering implements LogSplitter {

	public IMLog[] split(IMLog log, IMLogInfo logInfo, Cut cut, MinerState minerState) {
		return split(log, cut.getPartition(), minerState);
	}

	public static IMLog[] split(IMLog log, List<TIntSet> partition, MinerState minerState) {

		//map activities to sigmas
		final TIntIntHashMap eventclass2sigmaIndex = new TIntIntHashMap(10, 0.5f, Integer.MIN_VALUE, Integer.MIN_VALUE);
		TIntObjectHashMap<TIntSet> sigmaIndex2sigma = new TIntObjectHashMap<>(10, 0.5f, Integer.MIN_VALUE);
		{
			int p = 0;
			for (TIntSet sigma : partition) {
				sigmaIndex2sigma.put(p, sigma);
				final int p2 = p;
				sigma.forEach(new TIntProcedure() {
					public boolean execute(int activity) {
						eventclass2sigmaIndex.put(activity, p2);
						return true;
					}
				});
				p++;
			}
		}

		IMLog[] result = new IMLog[partition.size()];
		int partNr = 0;
		for (TIntSet sigma : partition) {
			IMLog sublog = log.clone();
			for (IMTraceIterator it = sublog.iterator(); it.hasNext();) {

				if (minerState.isCancelled()) {
					return null;
				}

				it.nextFast();

				//walk through the events and count how many go in each sigma
				int[] sigmaEventCounters = new int[partition.size()];
				int maxCounter = 0;
				TIntSet maxSigma = null;
				while (it.itEventHasNext()) {
					it.itEventNext();
					int sigmaIndex = eventclass2sigmaIndex.get(it.itEventGetActivityIndex());
					sigmaEventCounters[sigmaIndex]++;
					if (sigmaEventCounters[sigmaIndex] > maxCounter) {
						maxCounter = sigmaEventCounters[sigmaIndex];
						maxSigma = sigmaIndex2sigma.get(sigmaIndex);
					}
				}
				
				it.itEventReset();

				//determine whether this trace should go in this sublog
				if (it.isEmpty()) {
					/*
					 * An empty trace should have been filtered out before
					 * reaching here. We have no information what trace could
					 * have produced it, so we keep it in all sublogs.
					 */
				} else if (maxSigma != sigma) {
					//remove trace
					it.remove();
				} else {
					//keep trace, remove all events not from sigma
					while (it.itEventHasNext()) {
						it.itEventNext();
						if (!sigma.contains(it.itEventGetActivityIndex())) {
							it.itEventRemove();
						}
					}
				}
			}
			result[partNr] = sublog;
			partNr++;
		}

		return result;
	}
}
