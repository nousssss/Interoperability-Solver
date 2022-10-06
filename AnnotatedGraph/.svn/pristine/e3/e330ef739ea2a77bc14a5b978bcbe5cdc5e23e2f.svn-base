package org.processmining.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.processmining.models.annotatedgraph.AnnotatedEdge;
import org.processmining.models.annotatedgraph.AnnotatedGraph;
import org.processmining.models.annotatedgraph.AnnotatedGraph.ElementType;
import org.processmining.models.annotatedgraph.AnnotatedVertex;

public class AttributeHelper {

	private static AttributeHelper instance = null;

	protected AttributeHelper() {
		// Exists only to defeat instantiation
	}

	public static AttributeHelper getInstance() {
		if (instance == null) {
			instance = new AttributeHelper();
		}
		return instance;
	}

	private boolean checkAllNumbers(ArrayList<Object> vAttributeValue) {
		for (Object object : vAttributeValue) {
			try {
				Double.parseDouble(object.toString());
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

	/*
	 * Store all maxima for all attributes. This will be used for filtering the
	 * graph in Visualizer
	 */
	@SuppressWarnings("unchecked")
	public AnnotatedGraph storeMaxAttributes(AnnotatedGraph graph) {
		HashMap<String, HashMap<ElementType, Double>> storedMaxAttributes = new HashMap<String, HashMap<ElementType, Double>>();

		for (AnnotatedVertex v : graph.getVertices()) {
			HashMap<String, HashMap<String, ArrayList<?>>> vAttributes = v.getAttributes();
			for (String vAttributeName : vAttributes.keySet()) {
				if (!(storedMaxAttributes.containsKey(vAttributeName))) {
					storedMaxAttributes.put(vAttributeName, new HashMap<ElementType, Double>());
				}
				for (String modelName : vAttributes.get(vAttributeName).keySet()) {
					Object vAttributeValue = vAttributes.get(vAttributeName).get(modelName);
					boolean allNumbers = checkAllNumbers((ArrayList<Object>) vAttributeValue);

					if (allNumbers) {
						for (Object vAttributeValueElement : ((ArrayList<Object>) vAttributeValue)) {
							Double currentMax = storedMaxAttributes.get(vAttributeName).get(
									AnnotatedGraph.ElementType.AnnotatedVertex);
							double candidateMax = Double.parseDouble(vAttributeValueElement.toString());
							if (currentMax == null) {
								storedMaxAttributes.get(vAttributeName).put(AnnotatedGraph.ElementType.AnnotatedVertex,
										candidateMax);
							} else {
								storedMaxAttributes.get(vAttributeName).put(AnnotatedGraph.ElementType.AnnotatedVertex,
										Math.max(currentMax, candidateMax));
							}
						}
					} else {
						HashSet<Object> vAttributeValueSet = new HashSet<Object>();
						vAttributeValueSet.addAll(((ArrayList<Object>) vAttributeValue));

						Double currentMax = storedMaxAttributes.get(vAttributeName).get(
								AnnotatedGraph.ElementType.AnnotatedVertex);
						if (currentMax == null) {
							storedMaxAttributes.get(vAttributeName).put(AnnotatedGraph.ElementType.AnnotatedVertex,
									(double) vAttributeValueSet.size());
						} else {
							storedMaxAttributes.get(vAttributeName).put(AnnotatedGraph.ElementType.AnnotatedVertex,
									Math.max(currentMax, vAttributeValueSet.size()));
						}
					}
				}
			}
		}

		for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : graph.getEdges()) {
			HashMap<String, ArrayList<?>> eAttributes = e.getAttributes();
			for (String eAttributeName : eAttributes.keySet()) {
				if (!(storedMaxAttributes.containsKey(eAttributeName))) {
					storedMaxAttributes.put(eAttributeName, new HashMap<ElementType, Double>());
				}
				Object eAttributeValue = eAttributes.get(eAttributeName);
				boolean allNumbers = checkAllNumbers((ArrayList<Object>) eAttributeValue);

				if (allNumbers) {
					for (Object vAttributeValueElement : ((ArrayList<Object>) eAttributeValue)) {
						Double currentMax = storedMaxAttributes.get(eAttributeName).get(
								AnnotatedGraph.ElementType.AnnotatedEdge);
						double candidateMax = Double.parseDouble(vAttributeValueElement.toString());
						if (currentMax == null) {
							storedMaxAttributes.get(eAttributeName).put(AnnotatedGraph.ElementType.AnnotatedEdge,
									candidateMax);
						} else {
							storedMaxAttributes.get(eAttributeName).put(AnnotatedGraph.ElementType.AnnotatedEdge,
									Math.max(currentMax, candidateMax));
						}
					}
				} else {
					HashSet<Object> eAttributeValueSet = new HashSet<Object>();
					eAttributeValueSet.addAll(((ArrayList<Object>) eAttributeValue));

					Double currentMax = storedMaxAttributes.get(eAttributeName).get(
							AnnotatedGraph.ElementType.AnnotatedEdge);
					if (currentMax == null) {
						storedMaxAttributes.get(eAttributeName).put(AnnotatedGraph.ElementType.AnnotatedEdge,
								(double) eAttributeValueSet.size());
					} else {
						storedMaxAttributes.get(eAttributeName).put(AnnotatedGraph.ElementType.AnnotatedEdge,
								Math.max(currentMax, eAttributeValueSet.size()));
					}
				}
			}
		}

		graph.setMaxAttributes(storedMaxAttributes);

		return graph;
	}
}