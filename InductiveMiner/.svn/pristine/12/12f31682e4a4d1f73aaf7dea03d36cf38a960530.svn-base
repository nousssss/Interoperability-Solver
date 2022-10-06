package org.processmining.plugins.inductiveminer2.withoutlog.cutfinders;

import java.util.Iterator;

import org.processmining.plugins.inductiveminer2.framework.cutfinders.Filter;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntGraph;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class FilterWithoutLog {
	public static DfgMsd filterNoise(DfgMsd graph, float threshold) {
		DfgMsd newDfg = graph.clone();

		Filter.filterStartActivities(newDfg, threshold);
		Filter.filterEndActivities(newDfg, threshold);
		Filter.filterDirectlyFollowsGraph(newDfg, threshold);
		Filter.filterConcurrencyGraph(newDfg, threshold);
		filterMinimumSelfDistanceGraph(newDfg, threshold);
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
	public static void filterMinimumSelfDistanceGraph(DfgMsd dfg, float threshold) {
		IntGraph graph = dfg.getMinimumSelfDistanceGraph();

		for (int activity = 0; activity < dfg.getNumberOfActivities(); activity++) {
			//find the maximum outgoing weight of this node
			long maxWeightOut = 0;
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
}
