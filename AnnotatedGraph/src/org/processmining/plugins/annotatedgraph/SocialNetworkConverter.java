package org.processmining.plugins.annotatedgraph;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.annotatedgraph.AnnotatedEdgeIMP;
import org.processmining.models.annotatedgraph.AnnotatedGraph;
import org.processmining.models.annotatedgraph.AnnotatedVertex;
import org.processmining.models.graphbased.directed.socialnetwork.SNEdge;
import org.processmining.models.graphbased.directed.socialnetwork.SNNode;
import org.processmining.models.graphbased.directed.socialnetwork.SocialNetwork;

@Plugin(name = "Convert a Social network to a directed graph", parameterLabels = { "SocialNetwork" }, returnLabels = { "AnnotatedGraph" }, returnTypes = { AnnotatedGraph.class })
public class SocialNetworkConverter {
	public static String CLASSIFIER = "concept:name";
	public static final String DURATIONTAG = "duration";
	public static final String FREQUENCYTAG = "frequency";
	public static final String LIFECYCLECOMPLETE = "complete";
	public static final String LIFECYCLETAG = "lifecycle:transition";
	public static final String TIMESTAMPTAG = "time:timestamp";

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "J.A.J. van Mourik", email = "j.a.j.v.mourik@student.tue.nl", pack = "JeroenVanMourik")
	@PluginVariant(variantLabel = "Convert a Social network to a directed graph", requiredParameterLabels = { 0 })
	public AnnotatedGraph mine(PluginContext context, SocialNetwork minedNetwork) {
		return privateMine(context, minedNetwork);
	}

	private AnnotatedGraph privateMine(PluginContext context, SocialNetwork minedNetwork) {
		AnnotatedGraph localGraph = new AnnotatedGraph(minedNetwork.getLabel());

		CLASSIFIER = "org:resource";

		for (SNNode snNode : minedNetwork.getSNNodes()) {
			AnnotatedVertex snVertex = localGraph.addVertex(snNode.getLabel());
			if (!(snVertex == null)) {
				System.out.println("SN Vertex: " + snVertex.getValue());
			}
		}

		for (SNEdge snEdge : minedNetwork.getSNEdges()) {
			String sourceLabel = snEdge.getSource().getLabel();
			sourceLabel = sourceLabel.replace("+start", "");
			sourceLabel = sourceLabel.replace("+complete", "");

			AnnotatedVertex sourceVertex = localGraph.getVertex(sourceLabel);
			if (sourceVertex == null) {
				sourceVertex = localGraph.addVertex(sourceLabel);
				if (!(sourceVertex == null)) {
					System.out.println("SN Vertex: " + sourceVertex.getValue());
				} else {
					continue;
				}
			}

			String targetLabel = snEdge.getTarget().getLabel();
			targetLabel = targetLabel.replace("+start", "");
			targetLabel = targetLabel.replace("+complete", "");

			if (sourceLabel.equals(targetLabel)) {
				continue;
			}

			AnnotatedVertex targetVertex = localGraph.getVertex(targetLabel);
			if (targetVertex == null) {
				targetVertex = localGraph.addVertex(targetLabel);
				if (!((targetVertex == null))) {
					System.out.println("SN Vertex: " + targetVertex.getValue());
				}
			}

			if (!((targetVertex == null))) {
				AnnotatedEdgeIMP edge = localGraph.addEdge(sourceVertex, targetVertex, minedNetwork.getLabel());
				if (!(edge == null)) {
					System.out.println("SN Edge: " + edge.getModel());
				}
			}
		}

		return localGraph;
	}
}