package org.processmining.plugins.inductiveminer2.framework.cutfinders;

import java.util.Arrays;
import java.util.List;

import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntComponents;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TIntSet;

public class CutFinderIMSequenceStrict {

	public static List<TIntSet> merge(IntDfg dfg, List<TIntSet> partition) {

		if (partition.size() == 2) {
			return partition;
		}

		/*
		 * Assumption: there are no empty traces. (and if there are, they will
		 * be ignored).
		 */

		IntComponents components = new IntComponents(partition);

		//make a mapping node -> subCut
		//initialise counting of taus
		TObjectIntMap<TIntSet> component2index = new TObjectIntHashMap<>();
		TIntObjectMap<TIntSet> index2component = new TIntObjectHashMap<>();
		{
			for (int index = 0; index < partition.size(); index++) {
				component2index.put(partition.get(index), index);
				index2component.put(index, partition.get(index));
			}
		}

		//establish the minimum/maximum component from which there is an edge to/from this component.
		int[] edgeMinFrom = new int[partition.size()];
		int[] edgeMaxTo = new int[partition.size()];
		boolean[] hasSkippingEdges = new boolean[partition.size()];
		{
			Arrays.fill(edgeMinFrom, Integer.MAX_VALUE);
			Arrays.fill(edgeMaxTo, Integer.MIN_VALUE);

			for (int activity : dfg.getStartActivities()) {
				edgeMinFrom[components.getComponentOf(activity)] = Integer.MIN_VALUE;
				for (int i = 0; i < components.getComponentOf(activity); i++) {
					hasSkippingEdges[i] = true;
				}
			}

			for (int activity : dfg.getEndActivities()) {
				edgeMaxTo[components.getComponentOf(activity)] = Integer.MAX_VALUE;
				for (int i = components.getComponentOf(activity) + 1; i < partition.size(); i++) {
					hasSkippingEdges[i] = true;
				}
			}

			for (long edge : dfg.getDirectlyFollowsGraph().getEdges()) {
				int source = components.getComponentOf(dfg.getDirectlyFollowsGraph().getEdgeSource(edge));
				int target = components.getComponentOf(dfg.getDirectlyFollowsGraph().getEdgeTarget(edge));

				edgeMinFrom[target] = Math.min(edgeMinFrom[target], source);
				edgeMaxTo[source] = Math.max(edgeMaxTo[source], target);

				for (int i = source + 1; i < target; i++) {
					hasSkippingEdges[i] = true;
				}
			}
		}

		//find the inversion point in the min-array
		int inversionStart = -1;
		for (int i = 1; i < edgeMaxTo.length; i++) {
			if (edgeMaxTo[i - 1] > edgeMaxTo[i]) {
				inversionStart = i;
				break;
			}
		}

		//find the inversion point in the max-array
		int inversionEnd = edgeMinFrom.length;
		for (int i = edgeMinFrom.length - 1; i > 0; i--) {
			if (edgeMinFrom[i - 1] > edgeMinFrom[i]) {
				inversionEnd = i;
				break;
			}
		}

		if (inversionStart == -1 && inversionEnd == edgeMinFrom.length) {
			return components.getComponents();
		}

		//InductiveMiner.debug("  " + Arrays.toString(edgeMinFrom), minerState);
		//InductiveMiner.debug("  " + Arrays.toString(edgeMaxTo), minerState);

		//look for pivots
		for (int i = 0; i < partition.size(); i++) {

			//backward pivot
			if (i >= 1 && hasSkippingEdges[i] && edgeMaxTo[i - 1] == i) {
				//				System.out.println("backward pivot found " + i);
				//walk backward to find dependent nodes
				int j = i - 1;
				while (j >= 0 && edgeMaxTo[j] <= i) {
					//					System.out.println(" depending node " + j);
					j--;
				}
				for (int k = j + 1; k < i; k++) {
					components.mergeComponentsOf(index2component.get(k).iterator().next(),
							index2component.get(k + 1).iterator().next());
				}
			}

			//forward pivot
			if (i < partition.size() - 1 && hasSkippingEdges[i] && edgeMinFrom[i + 1] == i) {
				//				System.out.println("forward pivot found " + i);
				//walk forward to find dependent nodes
				int j = i + 1;
				while (j < partition.size() && edgeMinFrom[j] >= i) {
					//					System.out.println(" depending node " + j);
					j++;
				}
				for (int k = i; k < j - 1; k++) {
					components.mergeComponentsOf(index2component.get(k).iterator().next(),
							index2component.get(k + 1).iterator().next());
				}
			}
		}

		//		System.out.println(components.getComponents());
		//		System.out.println("");

		return components.getComponents();
	}
}
