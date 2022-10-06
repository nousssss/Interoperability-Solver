package org.processmining.plugins.annotatedgraph;

import java.util.ArrayList;
import java.util.HashSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.annotatedgraph.AnnotatedGraph;
import org.processmining.models.annotatedgraph.AnnotatedVertex;

@Plugin(name = "Mine a directed graph using causal relations", parameterLabels = { "Log" }, returnLabels = { "AnnotatedGraph" }, returnTypes = { AnnotatedGraph.class })
public class DirectedGraphMiner {
	public static String CLASSIFIER = "concept:name";
	public static final String LIFECYCLECOMPLETE = "complete";
	public static final String LIFECYCLETAG = "lifecycle:transition";
	public static final String TIMESTAMPTAG = "time:timestamp";

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "J.A.J. van Mourik", email = "j.a.j.v.mourik@student.tue.nl", pack = "JeroenVanMourik")
	@PluginVariant(variantLabel = "Mine a directed graph using causal relations", requiredParameterLabels = { 0 })
	public AnnotatedGraph mine(PluginContext context, XLog xLog) {
		return privateMine(context, xLog);
	}

	private AnnotatedGraph privateMine(PluginContext context, XLog xLog) {
		HashSet<String> minedVertices = new HashSet<String>();
		HashSet<ArrayList<String>> minedEdges = new HashSet<ArrayList<String>>();
		boolean newEvent = true;
		String sourceVertexName = null;
		String lastVertexName = null;

		String modelName = XConceptExtension.instance().extractName(xLog);
		context.getFutureResult(0).setLabel("Directed graph from " + modelName);
		AnnotatedGraph minedGraph = new AnnotatedGraph(modelName);

		// Retrieving classifier
		CLASSIFIER = xLog.getClassifiers().get(0).name();
		//System.out("PDGM: " + xLog.getClassifiers().get(0).name());

		for (XTrace xTrace : xLog) {
			sourceVertexName = null;
			for (XEvent xEvent : xTrace) {
				XAttributeMap xEventAttributes = xEvent.getAttributes();

				// Classifier and time stamps are required
				if ((!(xEventAttributes.containsKey(CLASSIFIER))) || (!(xEventAttributes.containsKey(TIMESTAMPTAG)))) {
					continue;
				}

				lastVertexName = xEventAttributes.get(CLASSIFIER).toString();

				// STUDENT model fix
				//if ((lastVertexName.equals("Start")) || (lastVertexName.equals("End"))) {
				//	continue;
				//}

				// If the current event is a new event (see example in comment below), the source is defined and (optional) source and target are different >>> create edge
				if (newEvent && (sourceVertexName != null) && (!sourceVertexName.equals(lastVertexName))) {
					ArrayList<String> newEdge = new ArrayList<String>();

					newEdge.add(sourceVertexName);
					newEdge.add(lastVertexName);

					minedEdges.add(newEdge);
				}

				// Since the model may contain multiple event for the very same event, check whether we are processing a new event or an event in progress
				// Example:		A (life cycle start)	A (life cycle complete)		B (life cycle start)	B (life cycle complete)		...
				//			 ^ new event = true		 ^ new event = false			 ^ new event = true		 ^ new event = false			 ...
				//
				// Or:			A (life cycle complete)		B (life cycle complete)		...
				//			 ^ new event = true			 ^ new event = true			 ...
				if (newEvent) {
					newEvent = false;
				}

				// If there is no life cycle tag (start, complete, ...) or life cycle tag indicates a completed event >>> store vertex
				if ((!xEventAttributes.containsKey(LIFECYCLETAG))
						|| (xEventAttributes.containsKey(LIFECYCLETAG) && xEventAttributes.get(LIFECYCLETAG).toString()
								.equals(LIFECYCLECOMPLETE))) {
					sourceVertexName = lastVertexName;
					newEvent = true;

					minedVertices.add(lastVertexName);
				}
			}
		}

		for (String v : minedVertices) {
			minedGraph.addVertex(v);
		}
		for (ArrayList<String> e : minedEdges) {
			AnnotatedVertex potentialSource = minedGraph.getVertex(e.get(0));
			AnnotatedVertex potentialTarget = minedGraph.getVertex(e.get(1));

			if ((potentialSource == null) || (potentialTarget == null)) {
				continue;
			}

			minedGraph.addEdge(potentialSource, potentialTarget, modelName);
		}

		return minedGraph;
	}
}