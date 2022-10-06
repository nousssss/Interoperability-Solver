package org.processmining.models.annotatedgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.jgraph.graph.GraphConstants;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;

/**
 * @author Jeroen
 * 
 * @param <S>
 * @param <T>
 */
public abstract class AnnotatedEdge<S extends AnnotatedVertex, T extends AnnotatedVertex> extends
		AbstractDirectedGraphEdge<S, T> {
	private HashMap<String, ArrayList<?>> attributes;
	private HashMap<String, HashMap<String, Double>> globalAggregates;
	private String model;
	private S source;
	private T target;

	AnnotatedEdge(S source, T target, String model) {
		super(source, target);
		this.source = source;
		this.target = target;
		this.model = model;
		this.attributes = new HashMap<String, ArrayList<?>>();
		this.globalAggregates = new HashMap<String, HashMap<String, Double>>();

		getAttributeMap().put(AttributeMap.STYLE, GraphConstants.STYLE_SPLINE);
		getAttributeMap().put(AttributeMap.SHOWLABEL, false);
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.LABEL, model);
		getAttributeMap().put(AttributeMap.LINEWIDTH, new Float(3.0));
	}

	public void addAttribute(String attributeName, ArrayList<?> value) {
		this.attributes.put(attributeName, value);
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

	@SuppressWarnings("unchecked")
	public String attributesToString(String attributeName) {
		String representation = "";
		ArrayList<?> value = null;
		if (this.attributes.containsKey(attributeName)) {
			value = this.attributes.get(attributeName);
		}
		if (value != null) {
			HashMap<Object, Integer> tempMap = new HashMap<Object, Integer>();
			for (Object obj : (ArrayList<Object>) value) {
				if (!(tempMap.containsKey(obj.toString()))) {
					tempMap.put(obj.toString(), 0);
				}
				tempMap.put(obj.toString(), tempMap.get(obj.toString()) + 1);
			}
			Object[] tempArray = tempMap.keySet().toArray(new Object[tempMap.keySet().size()]);
			Arrays.sort(tempArray);
			for (Object obj : tempArray) {
				representation += obj.toString() + " (" + tempMap.get(obj) + ") ";
			}
		}
		if (representation.equals("")) {
			representation = "";
		}
		return representation;
	}

	public boolean equals(Object e) {
		if (!(e instanceof AnnotatedEdge)) {
			return false;
		} else {
			AnnotatedEdgeIMP edge = (AnnotatedEdgeIMP) e;
			return (edge.getSource().getValue().equals(this.source.getValue())
					&& (edge.getTarget().getValue().equals(this.target.getValue())) && ((edge.getModel()
					.equals(this.model))));
		}
	}

	public HashMap<String, ArrayList<?>> getAttributes() {
		return this.attributes;
	}

	public Object getAttributes(String attributeName) {
		return this.attributes.get(attributeName);
	}

	AnnotatedEdgeIMP getClone(AnnotatedGraph graph) {
		HashMap<String, ArrayList<?>> newAttributes = new HashMap<String, ArrayList<?>>();
		HashMap<String, HashMap<String, Double>> newGlobalAggregates = new HashMap<String, HashMap<String, Double>>();

		for (String attributeName : this.attributes.keySet()) {
			newAttributes.put(attributeName, this.attributes.get(attributeName));
		}
		for (String attributeName : this.globalAggregates.keySet()) {
			if (!(newGlobalAggregates.containsKey(attributeName))) {
				newGlobalAggregates.put(attributeName, new HashMap<String, Double>());
			}
			for (String func : this.globalAggregates.get(attributeName).keySet()) {
				newGlobalAggregates.get(attributeName).put(func, this.globalAggregates.get(attributeName).get(func));
			}
		}

		AnnotatedVertex source = graph.getVertex(this.source.getValue());
		AnnotatedVertex target = graph.getVertex(this.target.getValue());
		if ((source == null) || (target == null)) {
			return null;
		}
		AnnotatedEdgeIMP newe = new AnnotatedEdgeIMP(source, target, this.model);
		newe.setAttributes(newAttributes);
		newe.setGlobalAggregates(newGlobalAggregates);

		return newe;
	}

	/* Global Aggregates */
	public HashMap<String, HashMap<String, Double>> getGlobalAggregates() {
		return this.globalAggregates;
	}

	public String getLabel() {
		return (String) getAttributeMap().get(AttributeMap.LABEL);
	}

	public String getModel() {
		return this.model;
	}

	/* Helpers */
	public S getSource() {
		return this.source;
	}

	public T getTarget() {
		return this.target;
	}

	public void setAttributes(HashMap<String, ArrayList<?>> attributes) {
		this.attributes = attributes;
	}

	public void setGlobalAggregates(HashMap<String, HashMap<String, Double>> aggregates) {
		this.globalAggregates = aggregates;
	}

	public void setLabel(String model) {
		getAttributeMap().put(AttributeMap.LABEL, model);
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setSource(S source) {
		this.source = source;
	}

	public void setTarget(T target) {
		this.target = target;
	}

	public String toString() {
		return "Edge: (" + this.source.getValue() + ", " + this.model + ", " + this.target.getValue() + ")";
	}
}