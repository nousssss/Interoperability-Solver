package org.processmining.plugins.inductiveminer2.framework.logsplitter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.helperclasses.Arrays;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMEventIterator;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMLogImplPartialTraces;
import org.processmining.plugins.inductiveminer2.logs.IMTrace;
import org.processmining.plugins.inductiveminer2.logs.IMTraceIterator;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

import com.google.common.collect.Lists;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class LogSplitterSequenceFilteringPartialTraces implements LogSplitter {

	public IMLog[] split(IMLog log, IMLogInfo logInfo, Cut cut, MinerState minerState) {
		assert (log instanceof IMLogImplPartialTraces);
		return split((IMLogImplPartialTraces) log, cut.getPartition(), minerState);
	}

	public static IMLog[] split(IMLogImplPartialTraces log, List<TIntSet> partition, MinerState minerState) {

		//initialise
		IMLog[] result = new IMLog[partition.size()];
		Map<TIntSet, IMLogImplPartialTraces> mapSigma2Sublog = new THashMap<>();

		TIntObjectMap<TIntSet> mapActivity2sigma = new TIntObjectHashMap<>(10, 0.5f, Integer.MIN_VALUE);
		Map<TIntSet, IMTraceIterator> mapSigma2TraceIterator = new THashMap<>();
		for (TIntSet sigma : partition) {
			IMLogImplPartialTraces sublog = log.clone();
			Arrays.add(result, sublog);
			mapSigma2Sublog.put(sigma, sublog);
			mapSigma2TraceIterator.put(sigma, sublog.iterator());
			for (TIntIterator it = sigma.iterator(); it.hasNext();) {
				int activity = it.next();
				mapActivity2sigma.put(activity, sigma);
			}
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
					it.next();
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
					it.next();
					it.remove();
				}
			}

			//if the start is not complete
			if (!log.isStartReliable(trace.getTraceIndex())) {
				boolean isStartComplete = false;
				//walk over the sigmas until the first non-empty one
				for (TIntSet sigma : partition) {
					if (isStartComplete) {
						//any subsequent ones are "start complete"
						mapSigma2Sublog.get(sigma).setStartReliable(trace.getTraceIndex(), true);
					}

					isStartComplete |= !subtraces.get(sigma).isEmpty();
				}
			}

			//if the end is not complete
			if (!log.isEndReliable(trace.getTraceIndex())) {

				//walk over the sigmas until the first non-empty one
				List<TIntSet> partitionReverse = new ArrayList<>(partition);
				boolean isEndComplete = false;
				for (TIntSet sigma : Lists.reverse(partitionReverse)) {
					if (isEndComplete) {
						//any subsequent ones are "start complete"
						mapSigma2Sublog.get(sigma).setEndReliable(trace.getTraceIndex(), true);
					}

					isEndComplete |= !subtraces.get(sigma).isEmpty();
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

		int event;
		while (it.hasNext()) {
			it.nextFast();
			event = it.getActivityIndex();
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
