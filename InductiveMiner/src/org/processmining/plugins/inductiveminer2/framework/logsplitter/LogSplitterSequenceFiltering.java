package org.processmining.plugins.inductiveminer2.framework.logsplitter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMEventIterator;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMTrace;
import org.processmining.plugins.inductiveminer2.logs.IMTraceIterator;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class LogSplitterSequenceFiltering implements LogSplitter {

	public IMLog[] split(IMLog log, IMLogInfo logInfo, Cut cut, MinerState minerState) {
		return split(log, cut.getPartition(), minerState);
	}

	public static IMLog[] split(IMLog log, List<TIntSet> partition, MinerState minerState) {

		//initialise
		IMLog[] result = new IMLog[partition.size()];
		Map<TIntSet, IMLog> mapSigma2Sublog = new THashMap<>();

		final TIntObjectMap<TIntSet> mapActivity2sigma = new TIntObjectHashMap<>(10, 0.5f, Integer.MIN_VALUE);
		Map<TIntSet, IMTraceIterator> mapSigma2TraceIterator = new THashMap<>();
		int i = 0;
		for (final TIntSet sigma : partition) {
			IMLog sublog = log.clone();
			result[i] = sublog;
			mapSigma2Sublog.put(sigma, sublog);
			mapSigma2TraceIterator.put(sigma, sublog.iterator());
			sigma.forEach(new TIntProcedure() {
				public boolean execute(int value) {
					mapActivity2sigma.put(value, sigma);
					return true;
				}
			});
			i++;
		}

		//walk through the traces (in all sublogs and the original log)
		for (IMTrace trace : log) {

			if (minerState.isCancelled()) {
				return null;
			}

			Map<TIntSet, IMTrace> subtraces = progress(mapSigma2TraceIterator);
			TIntSet ignore = new TIntHashSet(10, 0.5f, Integer.MIN_VALUE);

			//for each trace, fit each sigma
			int atPosition = 0; //we start before the first event
			for (Iterator<TIntSet> itSigma = partition.iterator(); itSigma.hasNext();) {
				TIntSet sigma = itSigma.next();
				IMTrace subtrace = subtraces.get(sigma);
				IMEventIterator it = subtrace.iterator();

				//remove all events before atPosition
				int atPositionInSubtrace = 0;
				while (atPositionInSubtrace < atPosition) {
					it.nextFast();
					it.remove();
					atPositionInSubtrace++;
				}

				//find where this sigma's subtrace will end
				if (itSigma.hasNext()) {
					atPosition = findOptimalSplit(log, trace, sigma, atPosition, ignore);
				} else {
					//if this is the last sigma, this sigma must finish the trace
					atPosition = trace.size();
				}
				ignore.addAll(sigma);

				//walk over this subtrace, remove all events not from sigma
				while (atPositionInSubtrace < atPosition) {
					it.nextFast();
					int c = it.getActivityIndex();
					if (!sigma.contains(c)) {
						it.remove();
					}
					atPositionInSubtrace++;
				}

				//remove the remaining part of this subtrace
				while (it.hasNext()) {
					it.nextFast();
					it.remove();
				}
			}
		}

		return result;
	}

	/**
	 * Progress all trace iterators
	 * 
	 * @param mapSigma2TraceIterator
	 * @return
	 */
	public static Map<TIntSet, IMTrace> progress(Map<TIntSet, IMTraceIterator> mapSigma2TraceIterator) {
		Map<TIntSet, IMTrace> result = new THashMap<>();
		for (Entry<TIntSet, IMTraceIterator> e : mapSigma2TraceIterator.entrySet()) {
			result.put(e.getKey(), e.getValue().next());
		}
		return result;
	}

	private static int findOptimalSplit(IMLog log, IMTrace trace, TIntSet sigma, int startPosition, TIntSet ignore) {
		int positionLeastCost = 0;
		int leastCost = 0;
		int cost = 0;
		int position = 0;

		IMEventIterator it = trace.iterator();

		//debug("find optimal split in " + trace.toString() + " for " + sigma.toString());

		//move to the start position
		while (position < startPosition && it.hasNext()) {
			position = position + 1;
			positionLeastCost = positionLeastCost + 1;
			it.next();
		}

		while (it.hasNext()) {
			it.nextFast();
			int event = it.getActivityIndex();
			if (ignore.contains(event)) {
				//skip
			} else if (sigma.contains(event)) {
				cost = cost - 1;
			} else {
				cost = cost + 1;
			}

			position = position + 1;

			if (cost < leastCost) {
				leastCost = cost;
				positionLeastCost = position;
			}
		}

		return positionLeastCost;
	}
}
