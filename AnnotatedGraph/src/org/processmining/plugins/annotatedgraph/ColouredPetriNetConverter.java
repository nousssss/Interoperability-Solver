package org.processmining.plugins.annotatedgraph;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.cpntools.accesscpn.model.Arc;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Transition;
import org.cpntools.accesscpn.model.importer.NetCheckException;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.annotatedgraph.AnnotatedEdgeIMP;
import org.processmining.models.annotatedgraph.AnnotatedGraph;
import org.processmining.models.annotatedgraph.AnnotatedVertex;
import org.processmining.plugins.cpnet.ColouredPetriNet;
import org.xml.sax.SAXException;

@Plugin(name = "Convert a Coloured Petri net to a directed graph", parameterLabels = { "ColouredPetriNet" }, returnLabels = { "AnnotatedGraph" }, returnTypes = { AnnotatedGraph.class })
public class ColouredPetriNetConverter {
	public static String CLASSIFIER = "concept:name";
	public static final String DURATIONTAG = "duration";
	public static final String FREQUENCYTAG = "frequency";
	public static final String LIFECYCLECOMPLETE = "complete";
	public static final String LIFECYCLETAG = "lifecycle:transition";
	public static final String TIMESTAMPTAG = "time:timestamp";

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "J.A.J. van Mourik", email = "j.a.j.v.mourik@student.tue.nl", pack = "JeroenVanMourik")
	@PluginVariant(variantLabel = "Convert a Coloured Petri net to a directed graph", requiredParameterLabels = { 0 })
	public AnnotatedGraph mine(PluginContext context, ColouredPetriNet cpNet) {
		return privateMine(context, cpNet);
	}

	private AnnotatedGraph privateMine(PluginContext context, ColouredPetriNet cpNet) {
		PetriNet localNet = null;
		try {
			localNet = cpNet.getPetriNet();
		} catch (NetCheckException e) {
			System.out.println("CPNC: NetCheckException");
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("CPNC: SAXException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("CPNC: IOException");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			System.out.println("CPNC: ParserConfigurationException");
			e.printStackTrace();
		}

		AnnotatedGraph localGraph = new AnnotatedGraph(localNet.getName().asString());

		for (Transition pageTransition : localNet.getPage().get(0).transition()) {
			AnnotatedVertex pageVertex = localGraph.addVertex(pageTransition.getName().asString());
			if (!(pageVertex == null)) {
				System.out.println("CPN Vertex: " + pageVertex.getValue());
			}
		}

		for (Place pagePlace : localNet.getPage().get(0).place()) {
			for (Arc pageIncomingArc : pagePlace.getTargetArc()) {

				String sourceLabel = pageIncomingArc.getSource().getName().asString();

				AnnotatedVertex sourceVertex = localGraph.getVertex(sourceLabel);
				if (sourceVertex == null) {
					sourceVertex = localGraph.addVertex(sourceLabel);
					if (!(sourceVertex == null)) {
						System.out.println("CPN Vertex: " + sourceVertex.getValue());
					}
				}

				if (!(sourceVertex == null)) {
					for (Arc pageOutgoingArc : pagePlace.getSourceArc()) {

						String targetLabel = pageOutgoingArc.getTarget().getName().asString();

						if (sourceLabel.equals(targetLabel)) {
							continue;
						}

						AnnotatedVertex targetVertex = localGraph.getVertex(targetLabel);
						if (targetVertex == null) {
							targetVertex = localGraph.addVertex(targetLabel);
							if (!((targetVertex == null))) {
								System.out.println("CPN Vertex: " + targetVertex.getValue());
							}
						}

						if (!((targetVertex == null))) {
							AnnotatedEdgeIMP edge = localGraph.addEdge(sourceVertex, targetVertex, localNet.getName()
									.asString());
							if (!(edge == null)) {
								System.out.println("CPN Edge: " + edge.getModel());
							}
						}
					}
				}
			}
		}

		return localGraph;
	}
}