package org.processmining.plugins.inductiveminer2.withoutlog.graphsplitters;

import java.util.Iterator;
import java.util.List;

import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsdImpl;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;

public class SimpleDfgMsdSplitter implements DfgMsdSplitter {

	public DfgMsd[] split(DfgMsd graph, Cut cut, MinerStateWithoutLog minerState) {
		return split(graph, cut.getPartition(), cut.getOperator());
	}

	public static void filterDfg(IntDfg graph, IntDfg subDfg, TIntSet sigma, Operator operator, List<TIntSet> partition,
			int sigmaN) {
		TIntIntHashMap node2sigma = getNode2Sigma(partition);

		//add activities
		{
			for (Iterator<Integer> it = graph.getActivities().iterator(); it.hasNext();) {
				int activity = it.next();
				if (sigma.contains(activity)) {
					long cardinality = graph.getActivities().getCardinalityOf(activity);
					subDfg.addActivity(activity);
					subDfg.getActivities().add(activity, cardinality - 1);
				}
			}
		}

		//add start activities
		{
			for (Iterator<Integer> it = graph.getStartActivities().iterator(); it.hasNext();) {
				int activity = it.next();
				if (sigma.contains(activity)) {
					long cardinality = graph.getStartActivities().getCardinalityOf(activity);
					subDfg.getStartActivities().add(activity, cardinality);
				}
			}
		}

		//add end activities
		{
			for (Iterator<Integer> it = graph.getEndActivities().iterator(); it.hasNext();) {
				int activity = it.next();
				if (sigma.contains(activity)) {
					long cardinality = graph.getEndActivities().getCardinalityOf(activity);
					subDfg.getEndActivities().add(activity, cardinality);
				}
			}
		}

		//walk through the edges (dfg)
		{
			Iterator<Long> it = graph.getDirectlyFollowsGraph().getEdges().iterator();
			while (it.hasNext()) {
				long edge = it.next();
				long cardinality = graph.getDirectlyFollowsGraph().getEdgeWeight(edge);
				int source = graph.getDirectlyFollowsGraph().getEdgeSource(edge);
				int target = graph.getDirectlyFollowsGraph().getEdgeTarget(edge);

				if (!sigma.contains(source)) {
					if (!sigma.contains(target)) {
						//edge not in sigma

						if (operator == Operator.sequence) {
							//add as empty trace
							if (node2sigma.get(source) < sigmaN && node2sigma.get(target) > sigmaN) {
								subDfg.addEmptyTraces(cardinality);
							}
						}
					} else {
						//edge going into sigma
						if (operator == Operator.sequence || operator == Operator.loop) {
							//add as start activity
							subDfg.getStartActivities().add(target, cardinality);
						}
					}
				} else { //source in sigma
					if (!sigma.contains(target)) {
						//edge going out of sigma
						if (operator == Operator.sequence || operator == Operator.loop) {
							//source is an end activity
							subDfg.getEndActivities().add(source, cardinality);
						}
					} else {
						//edge within sigma
						subDfg.getDirectlyFollowsGraph().addEdge(source, target, cardinality);
					}
				}
			}
		}

		if (operator == Operator.sequence) {
			//add empty traces for start activities in sigmas after this one
			for (int sigmaJ = sigmaN + 1; sigmaJ < partition.size(); sigmaJ++) {
				for (TIntIterator it = partition.get(sigmaJ).iterator(); it.hasNext();) {
					int activity = it.next();
					subDfg.addEmptyTraces(graph.getStartActivities().getCardinalityOf(activity));
				}
			}

			//add empty traces for end activities in sigmas before this one
			for (int sigmaJ = 0; sigmaJ < sigmaN; sigmaJ++) {
				for (TIntIterator it = partition.get(sigmaJ).iterator(); it.hasNext();) {
					int activity = it.next();
					subDfg.addEmptyTraces(graph.getEndActivities().getCardinalityOf(activity));
				}
			}
		}
	}

	public static DfgMsd[] split(DfgMsd graph, List<TIntSet> partition, Operator operator) {
		DfgMsd[] subDfgs = new DfgMsd[partition.size()];

		int sigmaN = 0;
		for (TIntSet sigma : partition) {
			DfgMsd subDfg = new DfgMsdImpl(graph.getAllActivities().clone());
			subDfgs[sigmaN] = subDfg;

			filterDfg(graph, subDfg, sigma, operator, partition, sigmaN);

			//walk through the edges (msd)
			{
				for (Iterator<Long> it = graph.getMinimumSelfDistanceGraph().getEdges().iterator(); it.hasNext();) {
					long edge = it.next();
					int source = graph.getMinimumSelfDistanceGraph().getEdgeSource(edge);
					int target = graph.getMinimumSelfDistanceGraph().getEdgeTarget(edge);
					long cardinality = graph.getMinimumSelfDistanceGraph().getEdgeWeight(edge);

					if (sigma.contains(source) && sigma.contains(target)) {
						subDfg.getMinimumSelfDistanceGraph().addEdge(source, target, cardinality);
					}
				}
			}

			sigmaN++;
		}

		return subDfgs;
	}

	private static TIntIntHashMap getNode2Sigma(List<TIntSet> partition) {
		TIntIntHashMap node2sigma = new TIntIntHashMap(10, 0.5f, -1, -1);
		int sigmaN = 0;
		for (TIntSet sigma : partition) {
			TIntIterator it = sigma.iterator();
			while (it.hasNext()) {
				node2sigma.put(it.next(), sigmaN);
			}
			sigmaN++;
		}
		return node2sigma;
	}

}
