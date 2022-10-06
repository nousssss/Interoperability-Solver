package org.processmining.plugins.inductiveminer2.framework.cutfinders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.processmining.plugins.InductiveMiner.ArrayUtilities;
import org.processmining.plugins.InductiveMiner.Sets;
import org.processmining.plugins.InductiveMiner.graphs.Components;
import org.processmining.plugins.InductiveMiner.graphs.Graph;
import org.processmining.plugins.InductiveMiner.graphs.GraphFactory;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.InductiveMiner.mining.cuts.IM.CutFinderIMSequenceReachability;
import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntStronglyConnectedComponents;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;

public class CutFinderIMSequence implements CutFinder {

	public Cut findCut(IMLog log, IMLogInfo logInfo, MinerState minerState) {
		return findCut(logInfo.getDfg());
	}

	public static Cut findCut(IntDfg dfg) {

		//compute and merge the strongly connected components of the directly follows graph
		Set<TIntSet> SCCs = IntStronglyConnectedComponents.compute(dfg.getDirectlyFollowsGraph());
		Graph<TIntSet> condensedGraph1 = condenseGraph(dfg, SCCs);
		
		if (SCCs.size() == 1) {
			return null;
		}

		//InductiveMiner.debug("  nodes in condensed graph 1 " + condensedGraph1.getVertices(), minerState);

		//condense the pairwise unreachable nodes
		Collection<Set<TIntSet>> xorCondensedNodes = condenseXor(condensedGraph1);

		//InductiveMiner.debug("  sccs before xormerge " + xorCondensedNodes.toString(), minerState);

		//make a new condensed graph
		final Graph<TIntSet> condensedGraph2 = GraphFactory.create(TIntSet.class, xorCondensedNodes.size());
		for (Set<TIntSet> node : xorCondensedNodes) {

			//we need to flatten this s to get a new list of nodes
			condensedGraph2.addVertex(Sets.flattenInt(node));
		}

		//InductiveMiner.debug("  sccs after xormerge " + condensedGraph2.getVertices().toString(), minerState);

		//add the edges
		Set<TIntSet> set = ArrayUtilities.toSet(condensedGraph2.getVertices());
		for (long edge : condensedGraph1.getEdges()) {
			//find the condensed node belonging to this activity
			TIntSet u = condensedGraph1.getEdgeSource(edge);
			TIntSet SCCu = Sets.findComponentWith(set, u.iterator().next());
			TIntSet v = condensedGraph1.getEdgeTarget(edge);
			TIntSet SCCv = Sets.findComponentWith(set, v.iterator().next());

			//add an edge if it is not internal
			if (SCCv != SCCu) {
				condensedGraph2.addEdge(SCCu, SCCv, 1); //this returns null if the edge was already present
			}
		}

		//now we have a condensed graph. we need to return a sorted list of condensed nodes.
		final CutFinderIMSequenceReachability scr2 = new CutFinderIMSequenceReachability(condensedGraph2);
		List<TIntSet> result = new ArrayList<TIntSet>();
		result.addAll(Arrays.asList(condensedGraph2.getVertices()));
		Collections.sort(result, new Comparator<TIntSet>() {
			public int compare(TIntSet arg0, TIntSet arg1) {
				if (scr2.getReachableFrom(condensedGraph2.getIndexOfVertex(arg0))
						.contains(condensedGraph2.getIndexOfVertex(arg1))) {
					return 1;
				} else {
					return -1;
				}
			}

		});

		if (result.size() <= 1) {
			return null;
		}

		//InductiveMiner.debug("  sccs after sorting " + result, minerState);

		/**
		 * Optimisation 4-8-2015: do not greedily use the maximal cut, but
		 * choose the one that minimises the introduction of taus.
		 * 
		 * This solves the case {<a, b, c>, <c>}, where choosing the cut {a,
		 * b}{c} increases precision over choosing the cut {a}{b}{c}.
		 * 
		 * Correction 11-7-2016: identify optional sub sequences and merge them.
		 */
		Cut newCut = new Cut(Operator.sequence, CutFinderIMSequenceStrict.merge(dfg, result));

		//InductiveMiner.debug("  sccs after pivot merge " + newCut.getPartition().toString(), minerState);

		if (newCut.isValid()) {
			return newCut;
		} else {
			return new Cut(Operator.sequence, result);
		}
	}

	private static Collection<Set<TIntSet>> condenseXor(Graph<TIntSet> condensedGraph1) {
		Collection<Set<TIntSet>> xorCondensedNodes;
		{
			Components<TIntSet> components = new Components<>(condensedGraph1.getVertices());
			CutFinderIMSequenceReachability scr1 = new CutFinderIMSequenceReachability(condensedGraph1);

			for (int node : condensedGraph1.getVertexIndices()) {
				TIntSet reachableFromTo = scr1.getReachableFromTo(node);

				//InductiveMiner.debug("  nodes pairwise reachable from/to " + node + ": " + reachableFromTo.toString(),minerState);

				for (int node2 : condensedGraph1.getVertexIndices()) {
					if (node != node2 && !reachableFromTo.contains(node2)) {
						components.mergeComponentsOf(node, node2);
					}
				}

			}

			//find the connected components to find the condensed xor nodes
			xorCondensedNodes = components.getComponents();
		}
		return xorCondensedNodes;
	}

	private static Graph<TIntSet> condenseGraph(IntDfg dfg, Set<TIntSet> SCCs) {
		Graph<TIntSet> result = GraphFactory.create(TIntSet.class, SCCs.size());
		{

			//15-3-2016: optimisation to look up strongly connected components faster
			final TIntIntMap node2sccIndex = new TIntIntHashMap();
			{
				int i = 0;
				for (TIntSet scc : SCCs) {
					final int i2 = i;
					scc.forEach(new TIntProcedure() {
						public boolean execute(int value) {
							node2sccIndex.put(value, i2);
							return true;
						}
					});
					i++;
				}
			}

			//add vertices (= components)
			for (TIntSet SCC : SCCs) {
				result.addVertex(SCC);
			}
			//add edges
			for (long edge : dfg.getDirectlyFollowsGraph().getEdges()) {
				if (dfg.getDirectlyFollowsGraph().getEdgeWeight(edge) >= 0) {
					//find the connected components belonging to these nodes
					int u = dfg.getDirectlyFollowsGraph().getEdgeSource(edge);
					int SCCu = node2sccIndex.get(u);
					int v = dfg.getDirectlyFollowsGraph().getEdgeTarget(edge);
					int SCCv = node2sccIndex.get(v);

					//add an edge if it is not internal
					if (SCCv != SCCu) {
						result.addEdge(SCCu, SCCv, 1); //this returns null if the edge was already present
					}
				}
			}
		}
		return result;
	}
}
