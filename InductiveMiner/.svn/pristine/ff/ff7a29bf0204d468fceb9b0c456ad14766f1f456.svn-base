package org.processmining.plugins.inductiveminer2.framework.cutfinders;

import java.util.Iterator;

import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntGraph;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;

public class Filter {
	public static IMLogInfo filterNoise(IMLogInfo logInfo, float threshold) {
		return new IMLogInfo(filterNoise(logInfo.getDfg(), threshold), logInfo.getMinimumSelfDistancesBetween(),
				logInfo.getMinimumSelfDistances(), logInfo.getNumberOfEvents(), logInfo.getNumberOfActivityInstances(),
				logInfo.getNumberOfTraces());
	}

	public static IntDfg filterNoise(IntDfg dfg, float threshold) {
		IntDfg newDfg = dfg.clone();

		filterStartActivities(newDfg, threshold);
		filterEndActivities(newDfg, threshold);
		filterDirectlyFollowsGraph(newDfg, threshold);
		filterConcurrencyGraph(newDfg, threshold);
		return newDfg;
	}

	/**
	 * Filter a graph. Only keep the edges that occur often enough, compared
	 * with other outgoing edges of the source. 0 <= threshold <= 1.
	 * 
	 * @param graph
	 * @param threshold
	 * @return
	 */
	public static void filterDirectlyFollowsGraph(IntDfg dfg, float threshold) {
		IntGraph graph = dfg.getDirectlyFollowsGraph();

		for (int activity = 0; activity < dfg.getNumberOfActivities(); activity++) {
			//find the maximum outgoing weight of this node
			long maxWeightOut = dfg.getEndActivities().getCardinalityOf(activity);
			for (long edge : graph.getOutgoingEdgesOf(activity)) {
				maxWeightOut = Math.max(maxWeightOut, (int) graph.getEdgeWeight(edge));
			}

			//remove all edges that are not strong enough
			Iterator<Long> it = graph.getOutgoingEdgesOf(activity).iterator();
			while (it.hasNext()) {
				long edge = it.next();
				if (graph.getEdgeWeight(edge) < maxWeightOut * threshold) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Filter a graph. Only keep the edges that occur often enough, compared
	 * with other outgoing edges of the source. 0 <= threshold <= 1.
	 * 
	 * @param graph
	 * @param threshold
	 * @return
	 */
	public static void filterConcurrencyGraph(IntDfg dfg, float threshold) {
		IntGraph graph = dfg.getConcurrencyGraph();

		for (int activity = 0; activity < dfg.getNumberOfActivities(); activity++) {
			//find the maximum outgoing weight of this node
			long maxWeightOut = dfg.getEndActivities().getCardinalityOf(activity);
			for (long edge : graph.getOutgoingEdgesOf(activity)) {
				maxWeightOut = Math.max(maxWeightOut, (int) graph.getEdgeWeight(edge));
			}

			//remove all edges that are not strong enough
			Iterator<Long> it = graph.getOutgoingEdgesOf(activity).iterator();
			while (it.hasNext()) {
				long edge = it.next();
				if (graph.getEdgeWeight(edge) < maxWeightOut * threshold) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Filter start activities. Only keep those occurring more times than
	 * threshold * the most occurring activity. 0 <= threshold <= 1.
	 * 
	 * @param activities
	 * @param threshold
	 * @return
	 */
	public static void filterStartActivities(IntDfg dfg, float threshold) {
		long max = dfg.getStartActivities()
				.getCardinalityOf(dfg.getStartActivities().getElementWithHighestCardinality());

		for (Iterator<Integer> it = dfg.getStartActivities().iterator(); it.hasNext();) {
			int activity = it.next();
			if (dfg.getStartActivities().getCardinalityOf(activity) < threshold * max) {
				it.remove();
			}
		}
	}

	/**
	 * Filter start activities. Only keep those occurring more times than
	 * threshold * the most occurring activity. 0 <= threshold <= 1.
	 * 
	 * @param activities
	 * @param threshold
	 * @return
	 */
	public static void filterEndActivities(IntDfg dfg, float threshold) {
		long max = dfg.getEndActivities().getCardinalityOf(dfg.getEndActivities().getElementWithHighestCardinality());
		for (Iterator<Integer> it = dfg.getEndActivities().iterator(); it.hasNext();) {
			int activity = it.next();
			if (dfg.getEndActivities().getCardinalityOf(activity) < threshold * max) {
				it.remove();
			}
		}
	}
}
