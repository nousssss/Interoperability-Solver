package org.processmining.petrinets.analysis.gedsim.algorithms.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.petrinets.analysis.gedsim.algorithms.abstr.AbstractGraphEditDistanceSimilarityAlgorithm;
import org.processmining.petrinets.analysis.gedsim.params.GraphEditDistanceSimilarityParameters;
import org.processmining.petrinets.analysis.gedsim.utils.StringEditDistance;
import org.processmining.petrinets.utils.DirectedGraphUtils;

public class GraphEditDistanceSimilarityGreedy<D extends DirectedGraph<? extends DirectedGraphNode, ? extends DirectedGraphEdge<?, ?>>>
		extends AbstractGraphEditDistanceSimilarityAlgorithm<D> {
	public GraphEditDistanceSimilarityGreedy(GraphEditDistanceSimilarityParameters params) {
		super(params);
	}

	/**
	 * Class that implements the algorithm to compute the edit distance between
	 * two SimpleGraph instances. Use the algorithm by calling the constructor
	 * with the two SimpleGraph instances between which you want to compute the
	 * edit distance. Then call compute(), which will return the edit distance.
	 */

	private Set<Pair<DirectedGraphNode, DirectedGraphNode>> times(Set<? extends DirectedGraphNode> a,
			Set<? extends DirectedGraphNode> b) {
		Set<Pair<DirectedGraphNode, DirectedGraphNode>> result = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
		for (DirectedGraphNode ea : a) {
			for (DirectedGraphNode eb : b) {
				if (StringEditDistance.similarity(ea.getLabel(), eb.getLabel()) >= params.getLedCutOff()) {
					result.add(new Pair<DirectedGraphNode, DirectedGraphNode>(ea, eb));
				}
			}
		}
		return result;
	}

	Set<Pair<DirectedGraphNode, DirectedGraphNode>> mapping = null;

	public double compute(D sg1, D sg2) {
		init(sg1, sg2);

		//INIT
		mapping = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
		Set<Pair<DirectedGraphNode, DirectedGraphNode>> openCouples = times(sg1.getNodes(), sg2.getNodes());
		double shortestEditDistance = Double.MAX_VALUE;
		Random randomized = new Random();

		//STEP
		boolean doStep = true;
		while (doStep) {
			doStep = false;
			Vector<Pair<DirectedGraphNode, DirectedGraphNode>> bestCandidates = new Vector<Pair<DirectedGraphNode, DirectedGraphNode>>();
			double newShortestEditDistance = shortestEditDistance;

			for (Pair<DirectedGraphNode, DirectedGraphNode> couple : openCouples) {
				Set<Pair<DirectedGraphNode, DirectedGraphNode>> newMapping = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>(mapping);
				newMapping.add(couple);
				double newEditDistance = params.isDoGrouping() ? this.groupedEditDistance(newMapping)
						: this.editDistance(newMapping);

				if (newEditDistance < newShortestEditDistance) {
					bestCandidates = new Vector<Pair<DirectedGraphNode, DirectedGraphNode>>();
					bestCandidates.add(couple);
					newShortestEditDistance = newEditDistance;
				} else if (newEditDistance == newShortestEditDistance) {
					bestCandidates.add(couple);
				}
			}

			if (bestCandidates.size() > 0) {
				//Choose a random candidate
				Pair<DirectedGraphNode, DirectedGraphNode> couple = bestCandidates
						.get(randomized.nextInt(bestCandidates.size()));

				if (params.isDoGrouping()) {
					openCouples.remove(couple);
				} else {
					Set<Pair<DirectedGraphNode, DirectedGraphNode>> newOpenCouples = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
					for (Pair<DirectedGraphNode, DirectedGraphNode> p : openCouples) {
						if (!p.getFirst().equals(couple.getFirst()) && !p.getSecond().equals(couple.getSecond())) {
							newOpenCouples.add(p);
						}
					}
					openCouples = newOpenCouples;
				}

				mapping.add(couple);
				shortestEditDistance = newShortestEditDistance;
				doStep = true;
			}
		}

		//Return the smallest edit distance
		return shortestEditDistance;
	}

	public Set<Pair<DirectedGraphNode, DirectedGraphNode>> bestMapping() {
		return mapping;
	}

	protected double groupedEditDistance(Set<Pair<DirectedGraphNode, DirectedGraphNode>> m) {
		Set<DirectedGraphNode> verticesFrom1Used = new HashSet<DirectedGraphNode>();
		Set<DirectedGraphNode> verticesFrom2Used = new HashSet<DirectedGraphNode>();

		//Relate each vertex to a group
		Map<DirectedGraphNode, Integer> vid1togid = new HashMap<DirectedGraphNode, Integer>();
		Map<DirectedGraphNode, Integer> vid2togid = new HashMap<DirectedGraphNode, Integer>();
		Map<Integer, Set<DirectedGraphNode>> gidtovid1 = new HashMap<Integer, Set<DirectedGraphNode>>();
		Map<Integer, Set<DirectedGraphNode>> gidtovid2 = new HashMap<Integer, Set<DirectedGraphNode>>();
		int gid = 1;

		double substitutedVertices = 0.0;
		for (Pair<DirectedGraphNode, DirectedGraphNode> pair : m) {
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
			substitutedVertices += substitutionDistance;

			Integer groupOf1 = vid1togid.get(pair.getFirst());
			Integer groupOf2 = vid2togid.get(pair.getSecond());
			if ((groupOf1 == null) && (groupOf2 == null)) {
				vid1togid.put(pair.getFirst(), gid);
				vid2togid.put(pair.getSecond(), gid);
				Set<DirectedGraphNode> elts1ingroup = new HashSet<DirectedGraphNode>();
				elts1ingroup.add(pair.getFirst());
				gidtovid1.put(gid, elts1ingroup);
				Set<DirectedGraphNode> elts2ingroup = new HashSet<DirectedGraphNode>();
				elts2ingroup.add(pair.getSecond());
				gidtovid2.put(gid, elts2ingroup);
				gid++;
			} else if ((groupOf1 != null) && (groupOf2 == null)) {
				vid2togid.put(pair.getSecond(), groupOf1);
				Set<DirectedGraphNode> elts2ingroup = new HashSet<DirectedGraphNode>();
				elts2ingroup.add(pair.getSecond());
				gidtovid2.put(groupOf1, elts2ingroup);
			} else if ((groupOf1 == null) && (groupOf2 != null)) {
				vid1togid.put(pair.getFirst(), groupOf2);
				Set<DirectedGraphNode> elts1ingroup = new HashSet<DirectedGraphNode>();
				elts1ingroup.add(pair.getFirst());
				gidtovid1.put(groupOf2, elts1ingroup);
			} else if (!groupOf1.equals(groupOf2)) { //Both elements are in a different group
				Integer usegroup = Math.min(groupOf1, groupOf2);
				Set<DirectedGraphNode> elts1ingroup = new HashSet<DirectedGraphNode>();
				Set<DirectedGraphNode> elts2ingroup = new HashSet<DirectedGraphNode>();
				for (DirectedGraphNode elt1 : gidtovid1.get(groupOf1)) {
					vid1togid.put(elt1, usegroup);
					elts1ingroup.add(elt1);
				}
				for (DirectedGraphNode elt1 : gidtovid1.get(groupOf2)) {
					vid1togid.put(elt1, usegroup);
					elts1ingroup.add(elt1);
				}
				for (DirectedGraphNode elt2 : gidtovid2.get(groupOf1)) {
					vid2togid.put(elt2, usegroup);
					elts2ingroup.add(elt2);
				}
				for (DirectedGraphNode elt2 : gidtovid2.get(groupOf2)) {
					vid2togid.put(elt2, usegroup);
					elts2ingroup.add(elt2);
				}
				gidtovid1.put(usegroup, elts1ingroup);
				gidtovid2.put(usegroup, elts2ingroup);
			}
		}
		double skippedVertices = (double) totalNrVertices - (double) verticesFrom1Used.size()
				- verticesFrom2Used.size();

		//Substituted edges are edges that are not mapped.
		//First, create the set of all edges in SimpleGraph 2.
		Set<Pair<DirectedGraphNode, DirectedGraphNode>> edgesIn1 = DirectedGraphUtils
				.translateToNodePairs(graph1.getEdges());
		Set<Pair<DirectedGraphNode, DirectedGraphNode>> edgesIn2 = DirectedGraphUtils
				.translateToNodePairs(graph2.getEdges());

		Set<Pair<Integer, Integer>> translatedEdgesIn1 = new HashSet<Pair<Integer, Integer>>();
		Set<Pair<Integer, Integer>> translatedEdgesIn2 = new HashSet<Pair<Integer, Integer>>();

		double groupedEdges = 0.0;
		for (Pair<DirectedGraphNode, DirectedGraphNode> e1 : edgesIn1) {
			Integer gsrc = vid1togid.get(e1.getFirst());
			Integer gtgt = vid1togid.get(e1.getSecond());
			if ((gsrc != null) && (gtgt != null)) {
				if (gsrc.equals(gtgt)) {
					groupedEdges += 1.0;
				} else {
					translatedEdgesIn1.add(new Pair<Integer, Integer>(gsrc, gtgt));
				}
			}
		}
		for (Pair<DirectedGraphNode, DirectedGraphNode> e2 : edgesIn2) {
			Integer gsrc = vid2togid.get(e2.getFirst());
			Integer gtgt = vid2togid.get(e2.getSecond());
			if ((gsrc != null) && (gtgt != null)) {
				if (gsrc.equals(gtgt)) {
					groupedEdges += 1.0;
				} else {
					translatedEdgesIn2.add(new Pair<Integer, Integer>(gsrc, gtgt));
				}
			}
		}

		//Grouped vertices
		Set<Integer> groups = new HashSet<Integer>();
		double groupedVertices = 0.0;
		for (Integer groupid : vid1togid.values())
			groups.add(groupid);
		for (Integer groupid : vid2togid.values())
			groups.add(groupid);
		for (Integer groupid : groups) {
			double groupsize1 = gidtovid1.get(groupid).size();
			if (groupsize1 > 1.0) {
				groupedVertices += groupsize1;
			}
			double groupsize2 = gidtovid2.get(groupid).size();
			if (groupsize2 > 1.0) {
				groupedVertices += groupsize2;
			}
		}

		translatedEdgesIn1.retainAll(translatedEdgesIn2); //These are mapped edges
		double mappedEdges = translatedEdgesIn1.size();
		double skippedEdges = (double) edgesIn1.size() + (double) edgesIn2.size() - groupedEdges - mappedEdges;

		double vskip = skippedVertices / (1.0 * totalNrVertices);
		double vgroup = groupedVertices / (1.0 * totalNrVertices);
		double vsubs = (2.0 * substitutedVertices) / (1.0 * totalNrVertices - skippedVertices);
		double editDistance;
		if (totalNrEdges == 0) {
			editDistance = ((params.getWeightSkippedVertices() * vskip) + (params.getWeightGroupedVertex() * vgroup)
					+ (params.getWeightSubstitutedVertices() * vsubs))
					/ (params.getWeightSkippedVertices() + params.getWeightSubstitutedVertices()
							+ params.getWeightGroupedVertex());
		} else {
			double eskip = (skippedEdges / (1.0 * totalNrEdges));
			editDistance = ((params.getWeightSkippedVertices() * vskip) + (params.getWeightGroupedVertex() * vgroup)
					+ (params.getWeightSubstitutedVertices() * vsubs) + (params.getWeightSkippedEdges() * eskip))
					/ (params.getWeightSkippedVertices() + params.getWeightSubstitutedVertices()
							+ params.getWeightSkippedEdges() + params.getWeightGroupedVertex());
		}
		return ((editDistance >= 0.0) && (editDistance <= 1.0)) ? editDistance : 1.0;
	}

}
