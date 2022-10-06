package org.processmining.plugins.inductiveminer2.framework.logsplitter;

import java.util.Collection;

import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier.Transition;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.helperclasses.Arrays;
import org.processmining.plugins.inductiveminer2.helperclasses.MultiIntSet;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMLogImplPartialTraces;
import org.processmining.plugins.inductiveminer2.logs.IMTraceIterator;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

import gnu.trove.set.TIntSet;

public class LogSplitterLoopPartialTraces implements LogSplitter {

	public IMLog[] split(IMLog log, IMLogInfo logInfo, Cut cut, MinerState minerState) {
		assert (log instanceof IMLogImplPartialTraces);
		return split((IMLogImplPartialTraces) log, cut.getPartition(), minerState);
	}

	public static IMLog[] split(IMLogImplPartialTraces log, Collection<TIntSet> partition, MinerState minerState) {

		//System.out.println("==before==");
		//System.out.print(log);
		//System.out.println("--");
		IMLog[] result = new IMLog[partition.size()];
		boolean firstSigma = true;
		//walk through the partition
		for (TIntSet sigma : partition) {
			IMLogImplPartialTraces sublog = log.clone();
			//			System.out.println("sigma " + sigma);

			//walk through traces
			for (IMTraceIterator it = sublog.iterator(); it.hasNext();) {
				it.nextFast();

				//System.out.println(" trace " + trace + " sigma " + sigma);

				if (minerState.isCancelled()) {
					return null;
				}

				boolean lastIn = firstSigma; //whether the last seen event was in sigma
				boolean anyIn = false; //whether there is any event in this subtrace
				MultiIntSet openActivityInstances = new MultiIntSet();

				boolean lastEventRemoved = false;

				//walk through the events
				while (it.itEventHasNext()) {
					it.itEventNext();
					int eventClass = it.itEventGetActivityIndex();
					Transition transition = it.itEventGetLifeCycleTransition();
					lastEventRemoved = false;

					//System.out.println(" trace " + trace + " sigma " + sigma);

					//keep track of open activity instances (by consistency assumption, should work out)
					switch (transition) {
						case start :
							openActivityInstances.add(eventClass);
							break;
						case complete :
							openActivityInstances.remove(eventClass, 1);
							break;
						case other :
							break;
					}

					if (sigma.contains(eventClass)) {
						//event of the sigma under consideration

						if (!lastIn && (firstSigma || anyIn)) {
							//this is the first activity of a new subtrace, so the part up till now is a completed subtrace

							int oldTrace = it.itEventSplit();
							//System.out.println("   split trace " + oldTrace + " # " + trace);

							sublog.setStartReliable(it.getTraceIndex(), true);
							sublog.setEndReliable(oldTrace, true);

						}
						lastIn = true;
						anyIn = true;

					} else {
						//event of another sigma

						//remove
						it.itEventRemove();
						lastEventRemoved = true;

						//the last seen event was not in sigma
						if (openActivityInstances.isEmpty()) {
							//if there are no activity instances open, we can split the trace further ahead
							lastIn = false;
						}
					}
				}

				//check if we are not introducing an empty trace
				if (!anyIn && !firstSigma) {
					it.remove();
				} else if (lastEventRemoved) {
					sublog.setEndReliable(it.getTraceIndex(), true);
				}
			}
			firstSigma = false;
			//System.out.println("--");
			//System.out.print(sublog);
			//System.out.println("--");
			Arrays.add(result, sublog);
		}

		return result;

		/*
		 * //old (copying) log splitter List<XLog> result = new ArrayList<>();
		 * Map<Set<XEventClass>, XLog> mapSigma2Sublog = new THashMap<>();
		 * Map<XEventClass, Set<XEventClass>> mapActivity2sigma = new
		 * THashMap<>(); for (Set<XEventClass> sigma : partition) { XLog sublog
		 * = new XLogImpl(new XAttributeMapImpl()); result.add(sublog);
		 * mapSigma2Sublog.put(sigma, sublog); for (XEventClass activity :
		 * sigma) { mapActivity2sigma.put(activity, sigma); } }
		 * 
		 * //loop through the traces for (IMTrace trace : log) { XTrace
		 * partialTrace = new XTraceImpl(new XAttributeMapImpl());
		 * 
		 * //keep track of the last sigma we were in Set<XEventClass> lastSigma
		 * = partition.iterator().next();
		 * 
		 * for (XEvent event : trace) { XEventClass c = log.classify(event); if
		 * (!lastSigma.contains(c)) {
		 * mapSigma2Sublog.get(lastSigma).add(partialTrace); partialTrace = new
		 * XTraceImpl(new XAttributeMapImpl()); lastSigma =
		 * mapActivity2sigma.get(c); } partialTrace.add(event); }
		 * mapSigma2Sublog.get(lastSigma).add(partialTrace);
		 * 
		 * //add an empty trace if the last event was not of sigma_1 if
		 * (lastSigma != partition.iterator().next()) {
		 * mapSigma2Sublog.get(lastSigma).add(new XTraceImpl(new
		 * XAttributeMapImpl())); } }
		 * 
		 * //wrap in IMLog objects List<IMLog> result2 = new ArrayList<>(); for
		 * (XLog xLog : result) { result2.add(new IMLog(xLog,
		 * minerState.parameters.getClassifier())); } return result2;
		 */
	}

}
