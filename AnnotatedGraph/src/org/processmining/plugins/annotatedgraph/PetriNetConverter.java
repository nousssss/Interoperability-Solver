package org.processmining.plugins.annotatedgraph;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.annotatedgraph.AnnotatedEdgeIMP;
import org.processmining.models.annotatedgraph.AnnotatedGraph;
import org.processmining.models.annotatedgraph.AnnotatedVertex;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

@Plugin(name = "Convert a Petri net to a directed graph", parameterLabels = { "Petrinet", "String" }, returnLabels = { "AnnotatedGraph" }, returnTypes = { AnnotatedGraph.class })
public class PetriNetConverter {
	public static String CLASSIFIER = "concept:name";
	public static final String DURATIONTAG = "duration";
	public static final String FREQUENCYTAG = "frequency";
	public static final String LIFECYCLECOMPLETE = "complete";
	public static final String LIFECYCLETAG = "lifecycle:transition";
	public static final String TIMESTAMPTAG = "time:timestamp";

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "J.A.J. van Mourik", email = "j.a.j.v.mourik@student.tue.nl", pack = "JeroenVanMourik")
	@PluginVariant(variantLabel = "Convert a Petri net to a directed graph", requiredParameterLabels = { 0, 1 })
	public AnnotatedGraph mine(PluginContext context, Petrinet minedNet, String modelName) {
		return privateMine(context, minedNet, modelName);
	}

	private AnnotatedGraph privateMine(PluginContext context, Petrinet minedNet, String modelName) {
		AnnotatedGraph localGraph = new AnnotatedGraph(modelName);

		for (Transition pnTransition : minedNet.getTransitions()) {
			AnnotatedVertex pnVertex = localGraph.addVertex(pnTransition.getLabel());
			if (!(pnVertex == null)) {
				System.out.println("PN Vertex: " + pnVertex.getValue());
			}
		}

		for (Place pnPlace : minedNet.getPlaces()) {
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> inEdge : minedNet.getInEdges(pnPlace)) {

				String sourceLabel = inEdge.getSource().getLabel();
				sourceLabel = sourceLabel.replace("+start", "");
				sourceLabel = sourceLabel.replace("+complete", "");

				AnnotatedVertex sourceVertex = localGraph.getVertex(sourceLabel);
				if (sourceVertex == null) {
					sourceVertex = localGraph.addVertex(sourceLabel);
					if (!(sourceVertex == null)) {
						System.out.println("PN Vertex: " + sourceVertex.getValue());
					}
				}

				if (!(sourceVertex == null)) {
					for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> outEdge : minedNet
							.getOutEdges(pnPlace)) {

						String targetLabel = outEdge.getTarget().getLabel();
						targetLabel = targetLabel.replace("+start", "");
						targetLabel = targetLabel.replace("+complete", "");

						if (sourceLabel.equals(targetLabel)) {
							continue;
						}

						AnnotatedVertex targetVertex = localGraph.getVertex(targetLabel);
						if (targetVertex == null) {
							targetVertex = localGraph.addVertex(targetLabel);
							if (!((targetVertex == null))) {
								System.out.println("PN Vertex: " + targetVertex.getValue());
							}
						}

						if (!((targetVertex == null))) {
							AnnotatedEdgeIMP edge = localGraph.addEdge(sourceVertex, targetVertex, modelName);
							if (!(edge == null)) {
								System.out.println("PN Edge: " + edge.getModel());
							}
						}
					}
				}
			}
		}

		return localGraph;
	}
}