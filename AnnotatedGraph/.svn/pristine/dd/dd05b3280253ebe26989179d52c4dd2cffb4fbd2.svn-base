package org.processmining.models.annotatedgraph;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.jgraph.renderers.AnnotatedShapeRenderer;
import org.processmining.models.shapes.Rectangle;

public class AnnotatedVertex extends AbstractDirectedGraphNode {
	private HashMap<String, HashMap<String, ArrayList<?>>> attributes;
	private HashMap<String, HashMap<String, Double>> globalAggregates;
	private HashMap<String, HashMap<String, HashMap<String, Double>>> localAggregates;
	private String value;

	public static final Color FADEFILLCOLOR = new Color(200, 200, 200);
	public static final Color FADESTROKECOLOR = new Color(140, 140, 140);
	public static final int STDHEIGHT = 84;
	public static final int STDWIDTH = 100;

	AnnotatedVertex(String value) {
		this.value = value;
		this.attributes = new HashMap<String, HashMap<String, ArrayList<?>>>();
		this.globalAggregates = new HashMap<String, HashMap<String, Double>>();
		this.localAggregates = new HashMap<String, HashMap<String, HashMap<String, Double>>>();

		getAttributeMap().put(AttributeMap.SHAPE, new Rectangle(false));
		getAttributeMap().put(AttributeMap.SQUAREBB, false);
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		getAttributeMap().put(AttributeMap.LABEL, value);
		getAttributeMap().put(AttributeMap.LABELCOLOR, Color.DARK_GRAY);
		getAttributeMap().put(AttributeMap.LABELHORIZONTALALIGNMENT, SwingConstants.CENTER);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(STDWIDTH, STDHEIGHT));
		getAttributeMap().put(AttributeMap.FILLCOLOR, FADEFILLCOLOR);
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 1);
		getAttributeMap().put(AttributeMap.STROKECOLOR, FADESTROKECOLOR);
		
		/*
		 * HV: Register a specific renderer for this vertex.
		 */
		getAttributeMap().put(AttributeMap.RENDERER, new AnnotatedShapeRenderer());
	}

	public void addAttribute(String attributeName, String modelName, ArrayList<?> value) {
		if (this.attributes.containsKey(attributeName)) {
			this.attributes.get(attributeName).put(modelName, value);
		} else {
			HashMap<String, ArrayList<?>> newmap = new HashMap<String, ArrayList<?>>();
			newmap.put(modelName, value);
			this.attributes.put(attributeName, newmap);
		}
	}

	public void addGlobalAggregate(String attributeName, String functionName, Double value) {
		if (this.globalAggregates.containsKey(attributeName)) {
			this.globalAggregates.get(attributeName).put(functionName, value);
		} else {
			HashMap<String, Double> newmap = new HashMap<String, Double>();
			newmap.put(functionName, value);
			this.globalAggregates.put(attributeName, newmap);
		}
	}

	public void addLocalAggregate(String attributeName, String modelName, String functionName, Double value) {
		if (!(this.localAggregates.containsKey(attributeName))) {
			this.localAggregates.put(attributeName, new HashMap<String, HashMap<String, Double>>());
		}
		if (!(this.localAggregates.get(attributeName).containsKey(modelName))) {
			this.localAggregates.get(attributeName).put(modelName, new HashMap<String, Double>());
		}
		this.localAggregates.get(attributeName).get(modelName).put(functionName, value);
	}

	@SuppressWarnings("unchecked")
	public String attributesToString(String attributeName, String modelName) {
		String representation = "";
		ArrayList<?> value = null;
		if (this.attributes.containsKey(attributeName) && this.attributes.get(attributeName).containsKey(modelName)) {
			value = this.attributes.get(attributeName).get(modelName);
		}
		if (value != null) {
			HashMap<Object, Integer> tempMap = new HashMap<Object, Integer>();
			for (Object obj : (ArrayList<Object>) value) {
				if (!(tempMap.containsKey(obj.toString()))) {
					tempMap.put(obj.toString(), 0);
				}
				tempMap.put(obj.toString(), tempMap.get(obj.toString()) + 1);
			}
			Object[] tempObjectArray = tempMap.keySet().toArray(new Object[tempMap.keySet().size()]);

			Arrays.sort(tempObjectArray);
			for (Object obj : tempObjectArray) {
				representation += obj.toString() + " (" + tempMap.get(obj) + ") ";
			}
		}
		if (representation.equals("")) {
			representation = "";
		}
		return representation;
	}

	public boolean equals(Object v) {
		if (!(v instanceof AnnotatedVertex)) {
			return false;
		} else {
			AnnotatedVertex vertex = (AnnotatedVertex) v;

			return (vertex.getValue().equals(this.value));
		}
	}

	/* Attributes */
	public HashMap<String, HashMap<String, ArrayList<?>>> getAttributes() {
		return this.attributes;
	}

	public HashMap<String, ArrayList<?>> getAttributes(String attributeName) {
		return this.attributes.get(attributeName);
	}

	public AnnotatedVertex getClone() {
		HashMap<String, HashMap<String, ArrayList<?>>> newAttributes = new HashMap<String, HashMap<String, ArrayList<?>>>();
		HashMap<String, HashMap<String, Double>> newGlobalAggregates = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, HashMap<String, HashMap<String, Double>>> newLocalAggregates = new HashMap<String, HashMap<String, HashMap<String, Double>>>();

		for (String attributeName : this.attributes.keySet()) {
			if (!(newAttributes.containsKey(attributeName))) {
				newAttributes.put(attributeName, new HashMap<String, ArrayList<?>>());
			}
			for (String model : this.attributes.get(attributeName).keySet()) {
				newAttributes.get(attributeName).put(model, this.attributes.get(attributeName).get(model));
			}
		}
		for (String attributeName : this.globalAggregates.keySet()) {
			if (!(newGlobalAggregates.containsKey(attributeName))) {
				newGlobalAggregates.put(attributeName, new HashMap<String, Double>());
			}
			for (String func : this.globalAggregates.get(attributeName).keySet()) {
				newGlobalAggregates.get(attributeName).put(func, this.globalAggregates.get(attributeName).get(func));
			}
		}
		for (String attributeName : this.localAggregates.keySet()) {
			if (!(newLocalAggregates.containsKey(attributeName))) {
				newLocalAggregates.put(attributeName, new HashMap<String, HashMap<String, Double>>());
			}
			for (String model : this.localAggregates.get(attributeName).keySet()) {
				if (!(newLocalAggregates.get(attributeName).containsKey(model))) {
					newLocalAggregates.get(attributeName).put(model, new HashMap<String, Double>());
				}
				for (String func : this.localAggregates.get(attributeName).get(model).keySet()) {
					newLocalAggregates.get(attributeName).get(model)
							.put(func, this.localAggregates.get(attributeName).get(model).get(func));
				}
			}
		}

		AnnotatedVertex newv = new AnnotatedVertex(this.value);
		newv.setAttributes(newAttributes);
		newv.setGlobalAggregates(newGlobalAggregates);
		newv.setLocalAggregates(newLocalAggregates);

		return newv;
	}

	/* Global Aggregates */
	public HashMap<String, HashMap<String, Double>> getGlobalAggregates() {
		return this.globalAggregates;
	}

	/* Helpers */
	public AbstractDirectedGraph<?, ?> getGraph() {
		return null;
	}

	public String getLabel() {
		return (String) getAttributeMap().get(AttributeMap.LABEL);
	}

	/* Local Aggregates */
	public HashMap<String, HashMap<String, HashMap<String, Double>>> getLocalAggregates() {
		return this.localAggregates;
	}

	public String getValue() {
		return this.value;
	}

	public void setAttributes(HashMap<String, HashMap<String, ArrayList<?>>> attributes) {
		this.attributes = attributes;
	}

	public void setGlobalAggregates(HashMap<String, HashMap<String, Double>> aggregates) {
		this.globalAggregates = aggregates;
	}

	public void setLabel(String label) {
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public void setLocalAggregates(HashMap<String, HashMap<String, HashMap<String, Double>>> aggregates) {
		this.localAggregates = aggregates;
	}

	public void setValue(String value) {
		this.value = value;
		getAttributeMap().put(AttributeMap.LABEL, value);
	}

	public String toString() {
		return "Vertex: " + this.value;
	}
}