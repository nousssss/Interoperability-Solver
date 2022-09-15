package org.processmining.models.graphbased.directed.epc;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collection;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.graphbased.directed.epc.elements.Connector;
import org.processmining.models.graphbased.directed.epc.elements.Event;
import org.processmining.models.graphbased.directed.epc.elements.Function;
import org.processmining.models.graphbased.directed.utils.GraphIterator;
import org.processmining.models.graphbased.directed.utils.GraphIterator.EdgeAcceptor;
import org.processmining.models.graphbased.directed.utils.GraphIterator.NodeAcceptor;

public abstract class EPCNode extends AbstractDirectedGraphNode {

	private final AbstractDirectedGraph<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>> graph;

	public EPCNode(AbstractDirectedGraph<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>> epc, String label) {
		super();
		graph = epc;
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(50, 50));
	}

	public AbstractDirectedGraph<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>> getGraph() {
		return graph;
	}

	public Collection<Event> getPrecedingEvents() {
		return Arrays.asList(getPrecedingNodes(Event.class).toArray(new Event[0]));
	}

	public Collection<Connector> getPrecedingConnectors() {
		return Arrays.asList(getPrecedingNodes(Connector.class).toArray(new Connector[0]));
	}

	public Collection<Function> getPrecedingFunctions() {
		return Arrays.asList(getPrecedingNodes(Function.class).toArray(new Function[0]));
	}

	private Collection<EPCNode> getPrecedingNodes(final Class<? extends EPCNode> nodeType) {

		final NodeAcceptor<EPCNode> nodeAcceptor = new NodeAcceptor<EPCNode>() {
			public boolean acceptNode(EPCNode node, int depth) {
				return ((depth != 0) && (nodeType.isAssignableFrom(node.getClass())));
			}
		};

		return GraphIterator.getDepthFirstPredecessors(this, getGraph(),
				new EdgeAcceptor<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>>() {

					public boolean acceptEdge(EPCEdge<? extends EPCNode, ? extends EPCNode> edge, int depth) {
						// don't search beyond functions
						return !nodeAcceptor.acceptNode(edge.getTarget(), depth);
					}
				}, nodeAcceptor);

	}

	public Collection<Event> getSucceedingEvents() {
		return Arrays.asList(getSucceedingNodes(Event.class).toArray(new Event[0]));
	}

	public Collection<Connector> getSucceedingConnectors() {
		return Arrays.asList(getSucceedingNodes(Connector.class).toArray(new Connector[0]));
	}

	public Collection<Function> getSucceedingFunctions() {
		return Arrays.asList(getSucceedingNodes(Function.class).toArray(new Function[0]));
	}

	private Collection<EPCNode> getSucceedingNodes(final Class<? extends EPCNode> nodeType) {

		final NodeAcceptor<EPCNode> nodeAcceptor = new NodeAcceptor<EPCNode>() {
			public boolean acceptNode(EPCNode node, int depth) {
				return ((depth != 0) && (nodeType.isAssignableFrom(node.getClass())));
			}
		};

		return GraphIterator.getDepthFirstSuccessors(this, getGraph(),
				new EdgeAcceptor<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>>() {

					public boolean acceptEdge(EPCEdge<? extends EPCNode, ? extends EPCNode> edge, int depth) {
						// don't search beyond functions
						return !nodeAcceptor.acceptNode(edge.getSource(), depth);
					}
				}, nodeAcceptor);

	}

	public Collection<Function> getSurroundingFunctions() {
		Collection<Function> result = getPrecedingFunctions();
		result.addAll(getSucceedingFunctions());
		return result;
	}

	public Collection<Event> getSurroundingEvents() {
		Collection<Event> result = getPrecedingEvents();
		result.addAll(getSucceedingEvents());
		return result;
	}

	public Collection<Connector> getSurroundingConnectors() {
		Collection<Connector> result = getPrecedingConnectors();
		result.addAll(getSucceedingConnectors());
		return result;
	}

}
