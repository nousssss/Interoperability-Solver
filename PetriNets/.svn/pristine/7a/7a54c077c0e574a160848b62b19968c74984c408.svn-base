package org.processmining.petrinets.analysis.gedsim.algorithms.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.petrinets.analysis.gedsim.algorithms.abstr.AbstractGraphEditDistanceSimilarityAlgorithm;
import org.processmining.petrinets.analysis.gedsim.params.GraphEditDistanceSimilarityParameters;
import org.processmining.petrinets.analysis.gedsim.utils.StringEditDistance;
import org.processmining.petrinets.utils.DirectedGraphUtils;

/**
 * Adopted from ProM5 class org.processmining.analysis.graphmatching.algos.
 * GraphEditDistanceProcessHeuristic
 * 
 * @param <D>
 */
public class GraphEditDistanceSimilarityProcessHeuristic<D extends DirectedGraph<?, ?>>
		extends AbstractGraphEditDistanceSimilarityAlgorithm<D> {

	public GraphEditDistanceSimilarityProcessHeuristic(GraphEditDistanceSimilarityParameters params) {
		super(params);
	}

	/**
	 * A tuple that contains a mapping that is being evaluated by the algorithm
	 * in computeAllMappings(). See cmoputeAllMappings for details.
	 */
	class CurrentMapping {
		public Set<DirectedGraphNode> currVerticesSG1;
		public Set<DirectedGraphNode> currVerticesSG2;
		public Set<Pair<DirectedGraphNode, DirectedGraphNode>> mapping;
		public Set<DirectedGraphNode> verticesSG1Used;
		public Set<DirectedGraphNode> verticesSG2Used;

		public CurrentMapping(Set<DirectedGraphNode> currVerticesSG1, Set<DirectedGraphNode> currVerticesSG2) {
			this.currVerticesSG1 = currVerticesSG1;
			this.currVerticesSG2 = currVerticesSG2;
			this.mapping = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
			this.verticesSG1Used = new HashSet<DirectedGraphNode>();
			this.verticesSG2Used = new HashSet<DirectedGraphNode>();
		}

		public CurrentMapping(Set<DirectedGraphNode> currVerticesSG1, Set<DirectedGraphNode> currVerticesSG2,
				Set<Pair<DirectedGraphNode, DirectedGraphNode>> mapping, Set<DirectedGraphNode> verticesSG1Used,
				Set<DirectedGraphNode> verticesSG2Used) {
			this.currVerticesSG1 = currVerticesSG1;
			this.currVerticesSG2 = currVerticesSG2;
			this.mapping = mapping;
			this.verticesSG1Used = verticesSG1Used;
			this.verticesSG2Used = verticesSG2Used;
		}

		@SuppressWarnings("rawtypes")
		public boolean equals(Object o) {
			if (o instanceof GraphEditDistanceSimilarityProcessHeuristic.CurrentMapping) {
				@SuppressWarnings("unchecked")
				CurrentMapping cm = (GraphEditDistanceSimilarityProcessHeuristic.CurrentMapping) o;
				return (currVerticesSG1.equals(cm.currVerticesSG1)) && (currVerticesSG2.equals(cm.currVerticesSG2))
						&& (mapping.equals(cm.mapping)) && (verticesSG1Used.equals(cm.verticesSG1Used))
						&& (verticesSG2Used.equals(cm.verticesSG2Used));
			} else {
				return false;
			}
		}

		public int hashCode() {
			return currVerticesSG1.hashCode() + currVerticesSG2.hashCode() + mapping.hashCode()
					+ verticesSG1Used.hashCode() + verticesSG2Used.hashCode();
		}
	}

	Set<Set<Pair<DirectedGraphNode, DirectedGraphNode>>> finalMappings;

	/**
	 * Computes all possible mappings between the two graphs. Stores them in
	 * 'finalMappings'. (Actually it does not compute >all< mappings; it only
	 * computes mappings that are expected to be >optimal<. So the naming of the
	 * method is not entirely accurate.)
	 * 
	 * The algorithm works as follows: - initial step: set <current vertices> to
	 * the source vertices of the two graphs - Recursive step: 1. prune the set
	 * of current mappings if necessary 2. take all current mappings up to
	 * <current vertices> and extend them
	 */
	private void computeAllMappings() {
		CurrentMapping currentMapping = new CurrentMapping(DirectedGraphUtils.getSources(graph1),
				DirectedGraphUtils.getSources(graph2));

		Set<CurrentMapping> currentMappings = new HashSet<CurrentMapping>();
		currentMappings.add(currentMapping);

		do {
			currentMappings = prune(currentMappings);
			currentMappings = step(currentMappings);
		} while (!currentMappings.isEmpty());
	}

	/**
	 * Extend each current mapping
	 */
	private Set<CurrentMapping> step(Set<CurrentMapping> currentMappings) {
		Set<CurrentMapping> newMappings = new HashSet<CurrentMapping>();

		for (CurrentMapping cm : currentMappings) {
			//For each currentMapping
			if (cm.currVerticesSG1.isEmpty() || cm.currVerticesSG2.isEmpty()) {
				//If mapping is final, then store it
				finalMappings.add(cm.mapping);
			} else {
				//If the mapping is not final, 
				for (DirectedGraphNode i : cm.currVerticesSG1) {
					for (DirectedGraphNode j : cm.currVerticesSG2) {
						//take a possible pair (i,j) from the <current vertices>
						//for that pair:
						Set<DirectedGraphNode> newVerticesSG1Used;
						Set<DirectedGraphNode> newVerticesSG2Used;
						Set<DirectedGraphNode> newCurrVerticesSG1;
						Set<DirectedGraphNode> newCurrVerticesSG2;
						Set<Pair<DirectedGraphNode, DirectedGraphNode>> newMapping;
						CurrentMapping nm;
						//1. Create a mapping in which i is substituted for j
						if (StringEditDistance.similarity(i.getLabel(), j.getLabel()) >= params.getLedCutOff()) {
							newVerticesSG1Used = new HashSet<DirectedGraphNode>(cm.verticesSG1Used);
							newVerticesSG1Used.add(i);
							newVerticesSG2Used = new HashSet<DirectedGraphNode>(cm.verticesSG2Used);
							newVerticesSG2Used.add(j);
							newCurrVerticesSG1 = new HashSet<DirectedGraphNode>(cm.currVerticesSG1);
							newCurrVerticesSG1.addAll(DirectedGraphUtils.getPostSet(graph1, i));
							newCurrVerticesSG1.removeAll(newVerticesSG1Used);
							newCurrVerticesSG2 = new HashSet<DirectedGraphNode>(cm.currVerticesSG2);
							newCurrVerticesSG2.addAll(DirectedGraphUtils.getPostSet(graph2, j));
							newCurrVerticesSG2.removeAll(newVerticesSG2Used);
							newMapping = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>(cm.mapping);
							newMapping.add(new Pair<DirectedGraphNode, DirectedGraphNode>(i, j));
							nm = new CurrentMapping(newCurrVerticesSG1, newCurrVerticesSG2, newMapping,
									newVerticesSG1Used, newVerticesSG2Used);
							newMappings.add(nm);
						}

						//2. Create a mapping in which i is skipped
						newVerticesSG1Used = new HashSet<DirectedGraphNode>(cm.verticesSG1Used);
						newVerticesSG1Used.add(i);
						newVerticesSG2Used = new HashSet<DirectedGraphNode>(cm.verticesSG2Used);
						newCurrVerticesSG1 = new HashSet<DirectedGraphNode>(cm.currVerticesSG1);
						newCurrVerticesSG1.addAll(DirectedGraphUtils.getPostSet(graph1, i));
						newCurrVerticesSG1.removeAll(newVerticesSG1Used);
						newCurrVerticesSG2 = new HashSet<DirectedGraphNode>(cm.currVerticesSG2);
						newMapping = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>(cm.mapping);
						nm = new CurrentMapping(newCurrVerticesSG1, newCurrVerticesSG2, newMapping, newVerticesSG1Used,
								newVerticesSG2Used);
						newMappings.add(nm);

						//2. Create a mapping in which j is skipped
						newVerticesSG1Used = new HashSet<DirectedGraphNode>(cm.verticesSG1Used);
						newVerticesSG2Used = new HashSet<DirectedGraphNode>(cm.verticesSG2Used);
						newVerticesSG2Used.add(j);
						newCurrVerticesSG1 = new HashSet<DirectedGraphNode>(cm.currVerticesSG1);
						newCurrVerticesSG2 = new HashSet<DirectedGraphNode>(cm.currVerticesSG2);
						newCurrVerticesSG2.addAll(DirectedGraphUtils.getPostSet(graph2, j));
						newCurrVerticesSG2.removeAll(newVerticesSG2Used);
						newMapping = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>(cm.mapping);
						nm = new CurrentMapping(newCurrVerticesSG1, newCurrVerticesSG2, newMapping, newVerticesSG1Used,
								newVerticesSG2Used);
						newMappings.add(nm);
					}
				}
			}
		}

		return newMappings;
	}

	/**
	 * Prune the set of current mappings into a smaller set of current mappings,
	 * when the size of the set exceeds 'pruneWhen'. Prune by keeping only the
	 * 'pruneTo' most viable mappings. (I.e. the 'pruneTo' mappings with the
	 * smallest edit-distance.
	 */
	private Set<CurrentMapping> prune(Set<CurrentMapping> currentMappings) {
		if ((params.getPruneWhen() == 0) || (currentMappings.size() < params.getPruneWhen())) {
			return currentMappings;
		}

		//Prune when size of currentMappings >= pruneWhen 

		//Create a SortedMap that couples each currentMapping to its edit-distance and
		//that is sorted by edit-distance.
		SortedMap<Double, CurrentMapping> mappingsByEd = new TreeMap<Double, CurrentMapping>();
		for (CurrentMapping cm : currentMappings) {
			mappingsByEd.put(editDistance(cm.mapping), cm);
		}

		//Return only the first 'pruneTo' members of that SortedMap.
		Set<CurrentMapping> newMappings = new HashSet<CurrentMapping>();
		Iterator<Map.Entry<Double, CurrentMapping>> ocms = mappingsByEd.entrySet().iterator();
		for (int i = 0; (i < params.getPruneTo()) && ocms.hasNext(); i++) {
			newMappings.add(ocms.next().getValue());
		}
		return newMappings;
	}

	Set<Pair<DirectedGraphNode, DirectedGraphNode>> mappingWithMinimalDistance = null;

	/**
	 * Computes the edit distance between the two SimpleGraph instances with
	 * which the object was instantiated.
	 * 
	 * Precondition: the algorithm only returns a useful edit distance if the
	 * graphs both have at least one 'source node' (a node with no incoming
	 * arcs.) Otherwise, Double.MAX_VALUE is returned.
	 * 
	 * @return edit distance
	 */
	public double compute(D sg1, D sg2) {
		init(sg1, sg2);
		finalMappings = new HashSet<Set<Pair<DirectedGraphNode, DirectedGraphNode>>>();

		//Compute all mappings
		computeAllMappings();

		mappingWithMinimalDistance = null;
		double minimalDistance = Double.MAX_VALUE;

		//Find the mapping with the smallest edit distance
		for (Set<Pair<DirectedGraphNode, DirectedGraphNode>> mapping : finalMappings) {
			double editDistance = editDistance(mapping);
			if (editDistance < minimalDistance) {
				minimalDistance = editDistance;
				mappingWithMinimalDistance = mapping;
			}
		}
		/*
		 * for (TwoVertices pair: mappingWithMinimalDistance){
		 * System.out.println("\"" + sg1.getLabel(pair.v1) + "\" - \"" +
		 * sg2.getLabel(pair.v2) + "\""); }
		 */
		//Return the smallest edit distance
		return minimalDistance;
	}

	public Set<Pair<DirectedGraphNode, DirectedGraphNode>> bestMapping() {
		return this.mappingWithMinimalDistance;
	}
}
