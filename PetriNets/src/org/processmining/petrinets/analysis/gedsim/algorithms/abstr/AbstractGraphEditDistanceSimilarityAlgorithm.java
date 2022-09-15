package org.processmining.petrinets.analysis.gedsim.algorithms.abstr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.petrinets.analysis.gedsim.algorithms.GraphEditDistanceSimilarityAlgorithm;
import org.processmining.petrinets.analysis.gedsim.algorithms.impl.GraphEditDistanceSimilarityAStar;
import org.processmining.petrinets.analysis.gedsim.params.GraphEditDistanceSimilarityParameters;
import org.processmining.petrinets.analysis.gedsim.utils.StringEditDistance;
import org.processmining.petrinets.utils.DirectedGraphUtils;

/**
 * This is an adaptation of the
 * org.processmining.analysis.graphmatching.algos.DistanceAlgoAbstr class
 * originating from ProM 5. The main difference is the adaptation of the class
 * to work with the ProM 6 {@link DirectedGraph} class. Note that I am not a fan
 * of the "tidiness" of this code, yet I do not have time to make it a little
 * more tidy.
 * 
 * @author svzelst
 *
 * @param <D>
 */
public abstract class AbstractGraphEditDistanceSimilarityAlgorithm<D extends DirectedGraph<? extends DirectedGraphNode, ? extends DirectedGraphEdge<?, ?>>>
		implements GraphEditDistanceSimilarityAlgorithm<D> {

	public final static DirectedGraphNode EPSILON = null; //means: 'no mapping'
	public final static double VERTEX_INSERTION_COST = 0.1; //Only for reproducing Luciano's results
	public final static double VERTEX_DELETION_COST = 0.9; //Only for reproducing Luciano's results

	protected D graph1;
	protected D graph2;
	protected int totalNrVertices;
	protected int totalNrEdges;

	protected GraphEditDistanceSimilarityParameters params = null;

	public AbstractGraphEditDistanceSimilarityAlgorithm(final GraphEditDistanceSimilarityParameters params) {
		setParameters(params);
	}

	public GraphEditDistanceSimilarityParameters getParameters() {
		return params;
	}

	public void setParameters(final GraphEditDistanceSimilarityParameters params) {
		this.params = params;
	}

	public class Mapping implements Comparable<Mapping> {

		public double cost;
		public double vertexMappingCost;
		public double vertexMappingCount;
		public Map<DirectedGraphNode, DirectedGraphNode> mappingsFromGraph1;
		public Map<DirectedGraphNode, DirectedGraphNode> mappingsFromGraph2;
		public Set<DirectedGraphNode> addedVertices;
		public Set<DirectedGraphNode> deletedVertices;
		public Set<Pair<DirectedGraphNode, DirectedGraphNode>> matchedEdges;
		public Set<Pair<DirectedGraphNode, DirectedGraphNode>> addedEdges;
		public Set<Pair<DirectedGraphNode, DirectedGraphNode>> deletedEdges;
		public List<DirectedGraphNode> remaining1;
		public List<DirectedGraphNode> remaining2;

		public Mapping() {
			cost = 0.0;
			vertexMappingCost = 0.0;
			vertexMappingCount = 0.0;
			mappingsFromGraph1 = new HashMap<DirectedGraphNode, DirectedGraphNode>();
			mappingsFromGraph2 = new HashMap<DirectedGraphNode, DirectedGraphNode>();
			addedVertices = new HashSet<DirectedGraphNode>();
			deletedVertices = new HashSet<DirectedGraphNode>();
			matchedEdges = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
			addedEdges = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
			deletedEdges = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
			remaining1 = new LinkedList<DirectedGraphNode>(graph1.getNodes());
			remaining2 = new LinkedList<DirectedGraphNode>(graph2.getNodes());
		}

		public Mapping clone() {
			Mapping m = new Mapping();
			m.remaining1.clear();
			m.remaining2.clear();
			m.cost = cost;
			m.vertexMappingCost = vertexMappingCost;
			m.vertexMappingCount = vertexMappingCount;

			m.mappingsFromGraph1.putAll(mappingsFromGraph1);
			m.mappingsFromGraph2.putAll(mappingsFromGraph2);
			m.addedVertices.addAll(addedVertices);
			m.deletedVertices.addAll(deletedVertices);
			m.matchedEdges.addAll(matchedEdges);
			m.addedEdges.addAll(addedEdges);
			m.deletedEdges.addAll(deletedEdges);
			m.remaining1.addAll(remaining1);
			m.remaining2.addAll(remaining2);
			return m;
		}

		public int compareTo(Mapping o) {
			if (cost < o.cost)
				return -1;
			else if (cost > o.cost)
				return 1;
			else
				return 0;
		}

		public double getCost() {
			return cost;
		}

		public void updateCost(GraphEditDistanceSimilarityAStar edCalculator) {
			cost = edCalculator.editDistance(this);
		}

		public void step(DirectedGraphNode v1, DirectedGraphNode v2) {
			step(v1, v2, 0.0);
		}

		public void step(DirectedGraphNode v1, DirectedGraphNode v2, double subsCost) {
			if (v1 == EPSILON) {
				remaining2.remove(v2);
				addedVertices.add(v2);
				mappingsFromGraph2.put(v2, EPSILON);
			} else if (v2 == EPSILON) {
				remaining1.remove(v1);
				deletedVertices.add(v1);
				mappingsFromGraph1.put(v1, EPSILON);
			} else {
				remaining1.remove(v1);
				remaining2.remove(v2);
				vertexMappingCost += subsCost;
				vertexMappingCount += 1.0;
				mappingsFromGraph1.put(v1, v2);
				mappingsFromGraph2.put(v2, v1);
			}

			if (v1 != EPSILON) {
				for (DirectedGraphNode v : DirectedGraphUtils.getPreSet(graph1, v1)) {
					if (mappingsFromGraph1.containsKey(v)) {
						if (v2 != EPSILON
								&& DirectedGraphUtils.getPreSet(graph2, v2).contains(mappingsFromGraph1.get(v))) {
							matchedEdges.add(new Pair<DirectedGraphNode, DirectedGraphNode>(v, v1));
						} else {
							deletedEdges.add(new Pair<DirectedGraphNode, DirectedGraphNode>(v, v1));
						}
					}
				}
				for (DirectedGraphNode v : DirectedGraphUtils.getPostSet(graph1, v1)) {
					if (mappingsFromGraph1.containsKey(v)) {
						if (v2 != EPSILON
								&& DirectedGraphUtils.getPostSet(graph2, v2).contains(mappingsFromGraph1.get(v))) {
							matchedEdges.add(new Pair<DirectedGraphNode, DirectedGraphNode>(v1, v));
						} else {
							deletedEdges.add(new Pair<DirectedGraphNode, DirectedGraphNode>(v1, v));
						}
					}
				}
			}

			if (v2 != EPSILON) {
				for (DirectedGraphNode v : DirectedGraphUtils.getPreSet(graph2, v2)) {
					if (mappingsFromGraph2.containsKey(v)) {
						if (v1 != EPSILON
								&& DirectedGraphUtils.getPostSet(graph1, v1).contains(mappingsFromGraph2.get(v))) {
							// Edge substitution set is handled over the SG1 edges
						} else {
							addedEdges.add(new Pair<DirectedGraphNode, DirectedGraphNode>(v, v2));
						}
					}
				}
				for (DirectedGraphNode v : DirectedGraphUtils.getPostSet(graph2, v2)) {
					if (mappingsFromGraph2.containsKey(v)) {
						if (v1 != EPSILON
								&& DirectedGraphUtils.getPostSet(graph1, v1).contains(mappingsFromGraph2.get(v))) {
							// Edge substitution set is handled over the SG1 edges
						} else {
							addedEdges.add(new Pair<DirectedGraphNode, DirectedGraphNode>(v, v2));
						}
					}
				}
			}
		}
	}

	public boolean useEvents() {
		return params.isUsEevents();
	}

	protected void init(D g1, D g2) {
		this.graph1 = g1;
		this.graph2 = g2;
		totalNrVertices = g1.getNodes().size() + g2.getNodes().size();
		totalNrEdges = g1.getEdges().size() + g2.getEdges().size();
	}

	protected double computeScore(double skippedEdges, double skippedVertices, double substitutedVertices,
			double nrSubstitutions, double insertedVertices, double deletedVertices) {
		if (params.isUsePureDistance()) {
			if (params.isUseEpsilon()) {
				return params.getWeightSkippedVertices()
						* (VERTEX_DELETION_COST * deletedVertices + VERTEX_INSERTION_COST * insertedVertices)
						+ params.getWeightSkippedEdges() * skippedEdges
						+ params.getWeightSubstitutedVertices() * 2.0 * substitutedVertices;
			} else {
				return params.getWeightSkippedVertices() * skippedVertices
						+ params.getWeightSkippedEdges() * skippedEdges
						+ params.getWeightSubstitutedVertices() * 2.0 * substitutedVertices;
			}
		} else {
			//Return the total edit distance. Multiply each element with its weight.
			double vskip = skippedVertices / (1.0 * totalNrVertices);
			double vsubs = (2.0 * substitutedVertices) / (1.0 * nrSubstitutions);
			double editDistance;
			if (totalNrEdges == 0) {
				editDistance = ((params.getWeightSkippedVertices() * vskip)
						+ (params.getWeightSubstitutedVertices() * vsubs))
						/ (params.getWeightSkippedVertices() + params.getWeightSubstitutedVertices());
			} else {
				double eskip = (skippedEdges / (1.0 * totalNrEdges));
				editDistance = ((params.getWeightSkippedVertices() * vskip)
						+ (params.getWeightSubstitutedVertices() * vsubs) + (params.getWeightSkippedEdges() * eskip))
						/ (params.getWeightSkippedVertices() + params.getWeightSubstitutedVertices()
								+ params.getWeightSkippedEdges());
			}
			return ((editDistance >= 0.0) && (editDistance <= 1.0)) ? editDistance : 1.0;
		}
	}

	protected double editDistance(Mapping m) {
		double skippedEdges;
		double skippedVertices;
		if (params.isUseEpsilon()) {
			skippedEdges = (double) m.addedEdges.size() + (double) m.deletedEdges.size();
			skippedVertices = (double) m.addedVertices.size() + (double) m.deletedVertices.size();
		} else {
			skippedEdges = totalNrEdges - 2.0 * m.matchedEdges.size();
			skippedVertices = totalNrVertices - 2.0 * m.vertexMappingCount;
		}
		double substitutedVertices = m.vertexMappingCost;
		double deletedVertices = m.deletedVertices.size();
		double insertedVertices = m.addedVertices.size();
		double nrSubstitutions = (totalNrVertices) - skippedVertices;

		return computeScore(skippedEdges, skippedVertices, substitutedVertices, nrSubstitutions, insertedVertices,
				deletedVertices);
	}

	protected double editDistance(Set<Pair<DirectedGraphNode, DirectedGraphNode>> m) {
		Set<DirectedGraphNode> verticesFrom1Used = new HashSet<DirectedGraphNode>();
		Set<DirectedGraphNode> verticesFrom2Used = new HashSet<DirectedGraphNode>();

		double epsilonSkippedVertices = 0.0;
		double epsilonInsertedVertices = 0.0;
		double epsilonDeletedVertices = 0.0;
		double epsilonSkippedEdges = 0.0;

		//vid1tovid2 = m, but it is a mapping, so we can more efficiently find the 
		//counterpart of a node in SimpleGraph1.
		Map<DirectedGraphNode, DirectedGraphNode> vid1tovid2 = new HashMap<DirectedGraphNode, DirectedGraphNode>();
		Map<DirectedGraphNode, DirectedGraphNode> vid2tovid1 = new HashMap<DirectedGraphNode, DirectedGraphNode>();

		//Substituted vertices are vertices that >are< mapped.
		//Their distance is 1.0 - string-edit similarity of their labels.
		double substitutedVertices = 0.0;
		double nrSubstitutions = 0.0;
		for (Pair<DirectedGraphNode, DirectedGraphNode> pair : m) {
			if ((pair.getFirst() != EPSILON) && (pair.getSecond() != EPSILON)) {
				double substitutionDistance;
				String label1 = pair.getFirst().getLabel();
				String label2 = pair.getSecond().getLabel();
				verticesFrom1Used.add(pair.getFirst());
				verticesFrom2Used.add(pair.getSecond());
				if (((label1.length() == 0) && (label2.length() != 0))
						|| ((label1.length() != 0) && (label2.length() == 0))) {
					//Do not tolerate mapping of tasks to control nodes
					substitutionDistance = Double.MAX_VALUE;
				} else {
					//Score the substitution
					substitutionDistance = 1.0
							- StringEditDistance.similarity(pair.getFirst().getLabel(), pair.getSecond().getLabel());
				}
				nrSubstitutions += 1.0;
				substitutedVertices += substitutionDistance;
			} else {
				if (pair.getFirst() == EPSILON) {
					epsilonInsertedVertices += 1.0;
				} else {
					epsilonDeletedVertices += 1.0;
				}
				epsilonSkippedVertices += 1.0;
			}

			//make each pair \in m also a pair \in vid1tovid2, 
			//such that in the end vid1tovid2 = m.
			vid1tovid2.put(pair.getFirst(), pair.getSecond());
			vid2tovid1.put(pair.getSecond(), pair.getFirst());
		}

		//Substituted edges are edges that are not mapped.
		//First, create the set of all edges in SimpleGraph 2.
		Set<Pair<DirectedGraphNode, DirectedGraphNode>> edgesIn1 = DirectedGraphUtils
				.translateToNodePairs(graph1.getEdges());
		Set<Pair<DirectedGraphNode, DirectedGraphNode>> edgesIn2 = DirectedGraphUtils
				.translateToNodePairs(graph2.getEdges());

		//Second, create the set of all edges in SimpleGraph 1,
		//but translate it into an edge on vertices from SimpleGraph 2.
		//I.e.: if (v1,v2) \in <Edges from SimpleGraph 1> and
		//v1 is mapped onto v1' and v2 is mapped onto v2', then
		//(v1',v2') \in <Translated edges from SimpleGraph 1>.
		Set<Pair<DirectedGraphNode, DirectedGraphNode>> translatedEdgesIn1 = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
		for (DirectedGraphNode i : graph1.getNodes()) {
			for (DirectedGraphNode j : DirectedGraphUtils.getPostSet(graph1, i)) {
				DirectedGraphNode srcMap = vid1tovid2.get(i);
				DirectedGraphNode tgtMap = vid1tovid2.get(j);
				if ((srcMap != null) && (tgtMap != null)) {
					if ((srcMap != EPSILON) && (tgtMap != EPSILON)) {
						translatedEdgesIn1.add(new Pair<DirectedGraphNode, DirectedGraphNode>(srcMap, tgtMap));
					} else {
						epsilonSkippedEdges += 1.0;
					}
				}
			}
		}
		edgesIn2.removeAll(translatedEdgesIn1); //Edges that are skipped remain
		Set<Pair<DirectedGraphNode, DirectedGraphNode>> translatedEdgesIn2 = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
		for (DirectedGraphNode i : graph2.getNodes()) {
			for (DirectedGraphNode j : DirectedGraphUtils.getPostSet(graph2, i)) {
				DirectedGraphNode srcMap = vid2tovid1.get(i);
				DirectedGraphNode tgtMap = vid2tovid1.get(j);
				if ((srcMap != null) && (tgtMap != null)) {
					if ((srcMap != EPSILON) && (tgtMap != EPSILON)) {
						translatedEdgesIn2.add(new Pair<DirectedGraphNode, DirectedGraphNode>(srcMap, tgtMap));
					} else {
						epsilonSkippedEdges += 1.0;
					}
				}
			}
		}
		edgesIn1.removeAll(translatedEdgesIn2); //Edges that are skipped remain
		double skippedEdges = 1.0 * edgesIn1.size() + 1.0 * edgesIn2.size();
		Set<DirectedGraphNode> skippedVerticesIn1 = new HashSet<DirectedGraphNode>(graph1.getNodes());
		skippedVerticesIn1.removeAll(verticesFrom1Used);
		Set<DirectedGraphNode> skippedVerticesIn2 = new HashSet<DirectedGraphNode>(graph2.getNodes());
		skippedVerticesIn2.removeAll(verticesFrom2Used);
		double skippedVertices = skippedVerticesIn1.size() + skippedVerticesIn2.size();

		if (params.isUseEpsilon()) {
			skippedEdges = epsilonSkippedEdges;
			skippedVertices = epsilonSkippedVertices;
		}

		return computeScore(skippedEdges, skippedVertices, substitutedVertices, nrSubstitutions, 0.0, 0.0);
	}

}
