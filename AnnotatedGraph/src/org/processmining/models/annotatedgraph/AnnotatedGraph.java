package org.processmining.models.annotatedgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;

public class AnnotatedGraph extends
		AbstractDirectedGraph<AnnotatedVertex, AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>> {

	private HashMap<AnnotatedVertex, HashMap<AnnotatedVertex, Set<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>>>> elements;
	private String label;
	private HashMap<String, HashMap<ElementType, Double>> maxattributemap;

	public enum ElementType {
		AnnotatedEdge, AnnotatedVertex
	};

	public AnnotatedGraph(String label) {
		this(label, new HashSet<AnnotatedVertex>(),
				new HashSet<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>>());
	}

	private AnnotatedGraph(String label, Set<AnnotatedVertex> vertices,
			Set<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>> edges) {
		this.elements = new HashMap<AnnotatedVertex, HashMap<AnnotatedVertex, Set<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>>>>();
		for (AnnotatedVertex v : vertices) {
			addVertex(v);
		}
		for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : edges) {
			addEdge((AnnotatedEdgeIMP) e);
		}
		this.label = label;
		this.maxattributemap = new HashMap<String, HashMap<ElementType, Double>>();
	}

	/* Elements */

	private AnnotatedEdgeIMP addEdge(AnnotatedEdgeIMP edge) {
		if ((this.elements.containsKey(edge.getSource())) && (this.elements.containsKey(edge.getTarget()))) {
			if (!(this.elements.get(edge.getSource()).containsKey(edge.getTarget()))) {
				this.elements.get(edge.getSource()).put(edge.getTarget(),
						new HashSet<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>>());
			}
			if (this.elements.get(edge.getSource()).get(edge.getTarget()).contains(edge)) {
				return null;
			}
			this.elements.get(edge.getSource()).get(edge.getTarget()).add(edge);
			return edge;
		}

		return null;
	}

	/* Vertices */

	public AnnotatedEdgeIMP addEdge(AnnotatedVertex source, AnnotatedVertex target, String model) {
		return addEdge(new AnnotatedEdgeIMP(source, target, model));
	}

	public AnnotatedVertex addVertex(AnnotatedVertex vertex) {
		if (this.elements.keySet().contains(vertex)) {
			return null;
		}
		this.elements
				.put(vertex,
						new HashMap<AnnotatedVertex, Set<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>>>());

		return vertex;
	}

	public AnnotatedVertex addVertex(String value) {
		return addVertex(new AnnotatedVertex(value));
	}

	protected Map<? extends DirectedGraphElement, ? extends DirectedGraphElement> cloneFrom(
			DirectedGraph<AnnotatedVertex, AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>> graph) {
		return null;
	}

	/* Edges */

	public AnnotatedGraph getClone() {
		AnnotatedGraph g = new AnnotatedGraph(this.label);
		for (AnnotatedVertex v : this.elements.keySet()) {
			g.addVertex(v.getClone());
		}
		for (AnnotatedVertex source : this.elements.keySet()) {
			for (AnnotatedVertex target : this.elements.get(source).keySet()) {
				for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : this.elements.get(source)
						.get(target)) {
					g.addEdge(e.getClone(g));
				}
			}
		}
		HashMap<String, HashMap<ElementType, Double>> newattmap = new HashMap<String, HashMap<ElementType, Double>>();
		for (String attributeName : this.maxattributemap.keySet()) {
			HashMap<ElementType, Double> newattnamemap = new HashMap<ElementType, Double>();
			for (ElementType type : this.maxattributemap.get(attributeName).keySet()) {
				newattnamemap.put(type, this.maxattributemap.get(attributeName).get(type));
			}
			newattmap.put(attributeName, newattnamemap);
		}
		g.setMaxAttributes(newattmap);

		return g;
	}

	public AnnotatedEdgeIMP getEdge(String source, String target, String model) {
		for (AnnotatedVertex v1 : this.elements.keySet()) {
			if (v1.getValue().equals(source)) {
				for (AnnotatedVertex v2 : this.elements.get(v1).keySet()) {
					if (v2.getValue().equals(target)) {
						Set<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>> edges = this.elements
								.get(v1).get(v2);
						for (AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex> e : edges) {
							if (e.getModel().equals(model)) {
								return (AnnotatedEdgeIMP) e;
							}
						}
					}
				}
			}
		}

		return null;
	}

	public Set<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>> getEdges() {
		Set<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>> edges = new HashSet<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>>();
		for (AnnotatedVertex source : this.elements.keySet()) {
			for (AnnotatedVertex target : this.elements.get(source).keySet()) {
				edges.addAll(this.elements.get(source).get(target));
			}
		}

		return edges;
	}

	public HashMap<AnnotatedVertex, HashMap<AnnotatedVertex, Set<AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>>>> getElements() {
		return this.elements;
	}

	protected AbstractDirectedGraph<AnnotatedVertex, AnnotatedEdge<? extends AnnotatedVertex, ? extends AnnotatedVertex>> getEmptyClone() {
		return null;
	}

	/* DirectedGraph */

	public String getLabel() {
		return this.label;
	}

	public Double getMaxAttribute(String attribute, ElementType type) {
		if (this.maxattributemap.containsKey(attribute)) {
			if (this.maxattributemap.get(attribute).containsKey(type)) {
				return this.maxattributemap.get(attribute).get(type);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public HashMap<String, HashMap<ElementType, Double>> getMaxAttributes() {
		return this.maxattributemap;
	}

	public Set<AnnotatedVertex> getNodes() {
		return this.elements.keySet();
	}

	public AnnotatedVertex getVertex(String value) {
		for (AnnotatedVertex vertex : this.elements.keySet()) {
			if (vertex.getValue().equals(value)) {
				return vertex;
			}
		}

		return null;
	}

	public Set<AnnotatedVertex> getVertices() {
		return this.elements.keySet();
	}

	private void removeEdge(AnnotatedEdgeIMP e) {
		if (this.elements.containsKey(e.getSource())) {
			if (this.elements.get(e.getSource()).containsKey(e.getTarget())) {
				this.elements.get(e.getSource()).get(e.getTarget()).remove(e);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void removeEdge(DirectedGraphEdge edge) {
		removeEdge((AnnotatedEdgeIMP) edge);
	}

	public void removeNode(DirectedGraphNode cell) {
		removeVertex((AnnotatedVertex) cell);
	}

	public void removeVertex(AnnotatedVertex vertex) {
		this.elements.remove(vertex);
		for (AnnotatedVertex v : this.elements.keySet()) {
			this.elements.get(v).remove(vertex);
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setMaxAttributes(HashMap<String, HashMap<ElementType, Double>> newmaxatts) {
		this.maxattributemap = new HashMap<String, HashMap<ElementType, Double>>(newmaxatts);
	}
}