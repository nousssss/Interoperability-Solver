package org.processmining.petrinets.analysis.gedsim.algorithms.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.petrinets.analysis.gedsim.algorithms.abstr.AbstractGraphEditDistanceSimilarityAlgorithm;
import org.processmining.petrinets.analysis.gedsim.params.GraphEditDistanceSimilarityParameters;
import org.processmining.petrinets.analysis.gedsim.utils.StringEditDistance;

public class GraphEditDistanceSimilarityExhaustive<D extends DirectedGraph<? extends DirectedGraphNode, ? extends DirectedGraphEdge<?, ?>>>
		extends AbstractGraphEditDistanceSimilarityAlgorithm<D> {

	public GraphEditDistanceSimilarityExhaustive(GraphEditDistanceSimilarityParameters params) {
		super(params);
	}

	public class UnfinishedMapping {
		public Set<DirectedGraphNode> freeVertices1;
		public Set<DirectedGraphNode> freeVertices2;
		public Set<Pair<DirectedGraphNode, DirectedGraphNode>> mapping;

		public UnfinishedMapping() {
			this.freeVertices1 = new HashSet<DirectedGraphNode>();
			this.freeVertices2 = new HashSet<DirectedGraphNode>();
			this.mapping = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
		}

		public UnfinishedMapping(Set<DirectedGraphNode> freeVertices1, Set<DirectedGraphNode> freeVertices2,
				Set<Pair<DirectedGraphNode, DirectedGraphNode>> mapping) {
			this.freeVertices1 = new HashSet<DirectedGraphNode>(freeVertices1);
			this.freeVertices2 = new HashSet<DirectedGraphNode>(freeVertices2);
			this.mapping = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>(mapping);
		}

		public UnfinishedMapping(Set<? extends DirectedGraphNode> freeVertices1,
				Set<? extends DirectedGraphNode> freeVertices2) {
			this.freeVertices1 = new HashSet<DirectedGraphNode>(freeVertices1);
			this.freeVertices2 = new HashSet<DirectedGraphNode>(freeVertices2);
			this.mapping = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
		}

		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
			if (o instanceof GraphEditDistanceSimilarityExhaustive.UnfinishedMapping) {
				@SuppressWarnings("rawtypes")
				UnfinishedMapping op = (GraphEditDistanceSimilarityExhaustive.UnfinishedMapping) o;
				return op.freeVertices1.equals(this.freeVertices1) && op.freeVertices2.equals(this.freeVertices2)
						&& op.mapping.equals(this.mapping);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return this.freeVertices1.hashCode() + this.freeVertices2.hashCode() + this.mapping.hashCode();
		}
	}

	Set<Set<Pair<DirectedGraphNode, DirectedGraphNode>>> finalMappings;
	Set<Pair<DirectedGraphNode, DirectedGraphNode>> mappingWithMinimalDistance = null;

	public Set<Pair<DirectedGraphNode, DirectedGraphNode>> bestMapping() {
		return this.mappingWithMinimalDistance;
	}

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

	private void computeAllMappings() {
		Set<UnfinishedMapping> unfinishedMappings = new HashSet<UnfinishedMapping>();
		finalMappings = new HashSet<Set<Pair<DirectedGraphNode, DirectedGraphNode>>>();

		unfinishedMappings.add(new UnfinishedMapping(graph1.getNodes(), graph2.getNodes()));

		do {
			unfinishedMappings = prune(unfinishedMappings);
			unfinishedMappings = step(unfinishedMappings);
		} while (!unfinishedMappings.isEmpty());
	}

	private Set<UnfinishedMapping> prune(Set<UnfinishedMapping> ufs) {
		if ((params.getPruneWhen() == 0) || (ufs.size() < params.getPruneWhen())) {
			return ufs;
		}

		SortedMap<Double, UnfinishedMapping> mappingsByEd = new TreeMap<Double, UnfinishedMapping>();
		for (UnfinishedMapping uf : ufs) {
			mappingsByEd.put(editDistance(uf.mapping), uf);
		}

		//Return only the first 'pruneTo' members of that SortedMap.
		Set<UnfinishedMapping> newMappings = new HashSet<UnfinishedMapping>();
		Iterator<Map.Entry<Double, UnfinishedMapping>> ocms = mappingsByEd.entrySet().iterator();
		for (int i = 0; (i < params.getPruneTo()) && ocms.hasNext(); i++) {
			newMappings.add(ocms.next().getValue());
		}
		return newMappings;

	}

	private Set<UnfinishedMapping> step(Set<UnfinishedMapping> ufs) {
		Set<UnfinishedMapping> newUfs = new HashSet<UnfinishedMapping>();

		for (UnfinishedMapping uf : ufs) {
			if (uf.freeVertices1.isEmpty() || uf.freeVertices2.isEmpty()) {
				finalMappings.add(uf.mapping);
			} else {
				for (DirectedGraphNode f1 : uf.freeVertices1) {
					for (DirectedGraphNode f2 : uf.freeVertices2) {
						if (StringEditDistance.similarity(f1.getLabel(), f2.getLabel()) >= params.getLedCutOff()) {
							UnfinishedMapping newMapping = new UnfinishedMapping(uf.freeVertices1, uf.freeVertices2,
									uf.mapping);
							newMapping.freeVertices1.remove(f1);
							newMapping.freeVertices2.remove(f2);
							newMapping.mapping.add(new Pair<DirectedGraphNode, DirectedGraphNode>(f1, f2));

							newUfs.add(newMapping);
						}
					}
					//Must also map f1 to 'nothing'
					UnfinishedMapping newMapping = new UnfinishedMapping(uf.freeVertices1, uf.freeVertices2,
							uf.mapping);
					newMapping.freeVertices1.remove(f1);
					newUfs.add(newMapping);
				}
			}
		}

		return newUfs;
	}
}
