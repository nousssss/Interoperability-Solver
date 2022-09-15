package org.processmining.petrinets.analysis.gedsim.algorithms.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.petrinets.analysis.gedsim.algorithms.abstr.AbstractGraphEditDistanceSimilarityAlgorithm;
import org.processmining.petrinets.analysis.gedsim.params.GraphEditDistanceSimilarityParameters;
import org.processmining.petrinets.analysis.gedsim.utils.StringEditDistance;

/**
 * Adopted from ProM5 class org.processmining.analysis.graphmatching.algos.
 * GraphEditDistanceAStarSim
 * 
 * @param <D>
 */
public class GraphEditDistanceSimilarityAStar<D extends DirectedGraph<?, ?>>
		extends AbstractGraphEditDistanceSimilarityAlgorithm<D> {

	public GraphEditDistanceSimilarityAStar(GraphEditDistanceSimilarityParameters params) {
		super(params);
	}

	private Set<DirectedGraphNode> partition1;
	private Set<DirectedGraphNode> partition2;

	private double labelSubstitutionCost(DirectedGraphNode v1, DirectedGraphNode v2) {
		if (partition1 != null)
			if ((partition1.contains(v1) && !partition2.contains(v2))
					|| (!partition1.contains(v1) && partition2.contains(v2)))
				return Double.POSITIVE_INFINITY;
		double led = 1.0 - StringEditDistance.similarity(v1.getLabel(), v2.getLabel());
		return led > params.getLedCutOff() ? Double.POSITIVE_INFINITY : led;
	}

	public double compute(D sg1, D sg2) {
		double accept_threshold = Double.POSITIVE_INFINITY;
		PriorityQueue<Mapping> open = new PriorityQueue<Mapping>();
		PriorityQueue<Mapping> fullMappings = new PriorityQueue<Mapping>();
		boolean matched = false;
		Mapping m;

		init(sg1, sg2);
		DirectedGraphNode v1 = sg1.getNodes().iterator().next();
		for (DirectedGraphNode v2 : sg2.getNodes()) {
			double labelSubs = labelSubstitutionCost(v1, v2);
			if (labelSubs != Double.POSITIVE_INFINITY) {
				m = new Mapping();
				m.step(v1, v2, labelSubs);
				m.updateCost(this);
				open.add(m);
				matched = true;
			}
		}

		if (!matched) {
			m = new Mapping();
			m.step(v1, EPSILON);
			m.updateCost(this);
			open.add(m);
		}

		while (!open.isEmpty()) {
			Mapping p = open.remove();

			if (p.getCost() > accept_threshold)
				break;
			if (p.remaining1.size() == 0 && p.remaining2.size() == 0) {
				fullMappings.add(p);
				accept_threshold = p.getCost();
				continue;
			}
			if (p.remaining1.size() > 0 && p.remaining2.size() > 0) {
				matched = false;
				DirectedGraphNode vk = p.remaining1.get(0);
				for (DirectedGraphNode w : p.remaining2) {
					double labelSubs = labelSubstitutionCost(vk, w);
					if (labelSubs != Double.POSITIVE_INFINITY) {
						m = p.clone();
						m.step(vk, w, labelSubs);
						m.updateCost(this);
						open.add(m);
						matched = true;
					}
				}

				if (!matched) {
					p.step(vk, EPSILON);
					p.updateCost(this);
					open.add(p);
				}
			} else if (p.remaining1.size() > 0) {
				DirectedGraphNode vk = p.remaining1.get(0);
				p.step(vk, EPSILON);
				p.updateCost(this);
				open.add(p);
			} else {
				DirectedGraphNode vk = p.remaining2.get(0);
				p.step(EPSILON, vk);
				p.updateCost(this);
				open.add(p);
			}
		}

		Mapping mapping = fullMappings.remove();

		bestMapping = new HashSet<Pair<DirectedGraphNode, DirectedGraphNode>>();
		for (Map.Entry<DirectedGraphNode, DirectedGraphNode> e : mapping.mappingsFromGraph1.entrySet()) {
			if (e.getValue() != EPSILON) {
				bestMapping.add(new Pair<DirectedGraphNode, DirectedGraphNode>(e.getKey(), e.getValue()));
			}
		}

		return mapping.cost;
	}

	Set<Pair<DirectedGraphNode, DirectedGraphNode>> bestMapping = null;

	public Set<Pair<DirectedGraphNode, DirectedGraphNode>> bestMapping() {
		return bestMapping;
	}

	public void setPartitions(Set<DirectedGraphNode> functions1, Set<DirectedGraphNode> functions2) {
		this.partition1 = functions1;
		this.partition2 = functions2;
	}

}
