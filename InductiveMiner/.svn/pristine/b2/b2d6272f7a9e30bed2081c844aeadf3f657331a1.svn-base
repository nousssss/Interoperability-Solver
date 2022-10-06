package org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd;

import java.util.Iterator;

import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.directlyfollowsgraph.DirectlyFollowsGraph;
import org.processmining.plugins.graphviz.colourMaps.ColourMap;
import org.processmining.plugins.graphviz.colourMaps.ColourMaps;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.DotEdge;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntGraph;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class DfgMsd2Dot {

	public static Dot visualise(DirectlyFollowsGraph dfgMsd) {

		Quadruple<Long, Long, Long, Long> q = getExtremes(dfgMsd);
		long startMax = q.getB();
		long endMax = q.getD();

		Dot result = new Dot();

		TIntObjectMap<DotNode> activity2dotNode = new TIntObjectHashMap<>(10, 0.5f, -1);
		for (Iterator<Integer> it = dfgMsd.getActivities().iterator(); it.hasNext();) {
			int activityIndex = it.next();
			DotNode node = result.addNode(dfgMsd.getActivityOfIndex(activityIndex));
			activity2dotNode.put(activityIndex, node);

			node.setOption("shape", "box");

			//determine node colour using start and end activities
			if (dfgMsd.getStartActivities().contains(activityIndex)
					&& dfgMsd.getEndActivities().contains(activityIndex)) {
				node.setOption("style", "filled");
				node.setOption("fillcolor",
						ColourMap
								.toHexString(ColourMaps.colourMapGreen(
										dfgMsd.getStartActivities().getCardinalityOf(activityIndex), startMax))
								+ ":" + ColourMap.toHexString(ColourMaps.colourMapRed(
										dfgMsd.getEndActivities().getCardinalityOf(activityIndex), endMax)));
			} else if (dfgMsd.getStartActivities().contains(activityIndex)) {
				node.setOption("style", "filled");
				node.setOption("fillcolor",
						ColourMap
								.toHexString(ColourMaps.colourMapGreen(
										dfgMsd.getStartActivities().getCardinalityOf(activityIndex), startMax))
								+ ":white");
			} else if (dfgMsd.getEndActivities().contains(activityIndex)) {
				node.setOption("style", "filled");
				node.setOption("fillcolor", "white:" + ColourMap.toHexString(
						ColourMaps.colourMapRed(dfgMsd.getEndActivities().getCardinalityOf(activityIndex), endMax)));
			}
		}

		IntGraph dfg = dfgMsd.getDirectlyFollowsGraph();
		for (long edgeIndex : dfg.getEdges()) {
			int source = dfg.getEdgeSource(edgeIndex);
			int target = dfg.getEdgeTarget(edgeIndex);
			long weight = dfg.getEdgeWeight(edgeIndex);
			result.addEdge(activity2dotNode.get(source), activity2dotNode.get(target), weight + "");
		}

		if (dfgMsd instanceof DfgMsd) {
			IntGraph msd = ((DfgMsd) dfgMsd).getMinimumSelfDistanceGraph();
			for (long edgeIndex : msd.getEdges()) {
				int source = msd.getEdgeSource(edgeIndex);
				int target = msd.getEdgeTarget(edgeIndex);
				long weight = msd.getEdgeWeight(edgeIndex);
				DotEdge edge = result.addEdge(activity2dotNode.get(source), activity2dotNode.get(target), weight + "");
				edge.setOption("style", "dashed");
			}
		}

		return result;

	}

	public static Quadruple<Long, Long, Long, Long> getExtremes(DirectlyFollowsGraph dfg) {
		long startMin = Long.MAX_VALUE;
		long startMax = Long.MIN_VALUE;
		for (int activityIndex : dfg.getStartActivities()) {
			long cardinality = dfg.getStartActivities().getCardinalityOf(activityIndex);
			startMin = Math.min(startMin, cardinality);
			startMax = Math.max(startMax, cardinality);
		}

		long endMin = Long.MAX_VALUE;
		long endMax = Long.MIN_VALUE;
		for (int activityIndex : dfg.getEndActivities()) {
			long cardinality = dfg.getEndActivities().getCardinalityOf(activityIndex);
			endMin = Math.min(endMin, cardinality);
			endMax = Math.max(endMax, cardinality);
		}

		return Quadruple.of(startMin, startMax, endMin, endMax);
	}

}
