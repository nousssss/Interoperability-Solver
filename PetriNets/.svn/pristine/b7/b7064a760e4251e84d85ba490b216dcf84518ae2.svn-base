package org.processmining.petrinets.analysis.gedsim.algorithms;

import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.petrinets.analysis.gedsim.params.GraphEditDistanceSimilarityParameters;

/**
 * Graph Edit Distance Similarity based on Dijkman et al.: Graph Matching
 * Algorithms for Business Process Similarity Search
 * 
 * The measure is based on the graph-representation of business process models
 * where connector/gateway nodes are removed. Similarity is calculated based on
 * the amount of necessary transformation operations:
 * <ul>
 * <li>Node Substitution</li>
 * <li>Node Insertion / Deletion</li>
 * <li>Edge Insertion / Deletion</li>
 * </ul>
 * 
 * There is a cost function for every transformation. The cost of substituting
 * node n1 with node n2 is calculated by 1 - sim(n1, n2). The default similarity
 * metric for node similarity is {@link LevenshteinSimilarity}.
 * 
 * Similarity of models M1, M2 with activities A1, A2 and edges E1, E2 is then
 * calculated based on the set of substituted nodes subn, inserted/deleted nodes
 * skipn, and inserted/deleted edges skipe as follows: fskipn = |skipn|/(|A1| +
 * |A2|), fskipe = |skipe|/(|E1| + |E2|), fsubn = 2 * sum(1 - sim(a1,a2))
 * 
 * sim(M1, M2) = (wskipn*fskipn + wskipe*fskipe + wsubn*fsubn) / (wskipn +
 * wskipe + wsubn)
 * 
 * B. Hompes: This plug-in was copied from the ProM v5 plug-in, and modified to
 * support Petrinets that have non-unique place labels.
 * 
 * @author rdijkman
 * @author b.f.a.hompes
 * @author svzelst
 */
public interface GraphEditDistanceSimilarityAlgorithm<D extends DirectedGraph<?, ?>> {

	public GraphEditDistanceSimilarityParameters getParameters();

	public void setParameters(final GraphEditDistanceSimilarityParameters params);

	/**
	 * Given two graphs, returns a value by which graphs can be sorted for relevance,
	 * lowest value first. E.g. the value can be:
	 * - an edit distance (lower edit distance means better match between graphs)
	 * - 1.0 - similarity score (lower value means higher similarity score, means better match between graphs) 
	 * 
	 * @param sg1 A graph.
	 * @param sg2 A graph.
	 * @return A value, where a lower value represents a more relevant match between graphs.
	 */
	double compute(D sg1, D sg2);

}
