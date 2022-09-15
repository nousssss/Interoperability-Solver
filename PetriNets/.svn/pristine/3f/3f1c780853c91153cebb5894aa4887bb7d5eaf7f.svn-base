package org.processmining.petrinets.analysis.gedsim.algorithms.impl;

import java.util.HashSet;
import java.util.Set;

import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.petrinets.analysis.gedsim.algorithms.abstr.AbstractGraphEditDistanceSimilarityAlgorithm;
import org.processmining.petrinets.analysis.gedsim.params.GraphEditDistanceSimilarityParameters;
import org.processmining.petrinets.analysis.gedsim.utils.StringEditDistance;

public class GraphEditDistanceSimilarityLexical<D extends DirectedGraph<?, ?>>
		extends AbstractGraphEditDistanceSimilarityAlgorithm<D> {

	public GraphEditDistanceSimilarityLexical(GraphEditDistanceSimilarityParameters params) {
		super(params);
	}

	Set<Pair<DirectedGraphNode, DirectedGraphNode>> solutionMappings = null;

	public double compute(D sg1, D sg2) {
		init(sg1, sg2);
		solutionMappings = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();

		// function mapping score
		for (DirectedGraphNode g1Func : sg1.getNodes()) {
			for (DirectedGraphNode g2Func : sg2.getNodes()) {

				// find the score using edit distance 
				double edScore = StringEditDistance.similarity(g1Func.getLabel(), g2Func.getLabel());

				// add all the result that has the ed >= threshold
				if (edScore >= params.getLedCutOff()) {
					solutionMappings.add(new Pair<DirectedGraphNode, DirectedGraphNode>(g1Func, g2Func));
				}
			}
		}

		return editDistance(solutionMappings);
	}

	public Set<Pair<DirectedGraphNode, DirectedGraphNode>> bestMapping() {
		return solutionMappings;
	}
}
