package org.processmining.models.graphbased.directed.epc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.graphbased.directed.epc.elements.Arc;
import org.processmining.models.graphbased.directed.epc.elements.ConfigurableConnector;
import org.processmining.models.graphbased.directed.epc.elements.ConfigurableFunction;
import org.processmining.models.graphbased.directed.epc.elements.Connector;
import org.processmining.models.graphbased.directed.epc.elements.Connector.ConnectorType;
import org.processmining.models.graphbased.directed.epc.elements.Event;
import org.processmining.models.graphbased.directed.epc.elements.Function;

abstract class AbstractConfigurableEPC extends
		AbstractDirectedGraph<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>> {

	public AbstractConfigurableEPC(boolean isConfigurable) {
		super();
		events = new LinkedHashSet<Event>();
		connectors = new LinkedHashSet<Connector>();
		functions = new LinkedHashSet<Function>();
		confConnectors = isConfigurable ? new LinkedHashSet<ConfigurableConnector>() : Collections
				.<ConfigurableConnector>emptySet();
		confFunctions = isConfigurable ? new LinkedHashSet<ConfigurableFunction>() : Collections
				.<ConfigurableFunction>emptySet();
		arcs = new LinkedHashSet<Arc>();
	}

	protected final Set<Connector> connectors;
	protected final Set<Event> events;
	protected final Set<Function> functions;
	protected final Set<ConfigurableConnector> confConnectors;
	protected final Set<ConfigurableFunction> confFunctions;
	protected final Set<Arc> arcs;

	public synchronized Connector addConnector(String label, ConnectorType type) {
		Connector c = new Connector(this, label, type);
		connectors.add(c);
		graphElementAdded(c);
		return c;
	}

	public synchronized Event addEvent(String label) {
		Event e = new Event(this, label);
		events.add(e);
		graphElementAdded(e);
		return e;
	}

	public synchronized Function addFunction(String label) {
		Function f = new Function(this, label);
		functions.add(f);
		graphElementAdded(f);
		return f;
	}

	public synchronized Arc addArc(EPCNode source, EPCNode target) {
		return addArc(source, target, source.toString() + " --> " + target.toString());
	}

	public synchronized Arc addArc(EPCNode source, EPCNode target, String label) {
		return addArcPrivate(source, target, label);
	}

	public synchronized Set<Arc> getArcs(EPCNode source, EPCNode target) {
		return arcs;
	}

	public synchronized Connector removeConnector(Connector connector) {
		removeSurroundingEdges(connector);
		return removeNodeFromCollection(connectors, connector);
	}

	public synchronized Event removeEvent(Event event) {
		removeSurroundingEdges(event);
		return removeNodeFromCollection(events, event);
	}

	public synchronized Function removeFunction(Function function) {
		removeSurroundingEdges(function);
		return removeNodeFromCollection(functions, function);
	}

	@SuppressWarnings("unchecked")
	public synchronized void removeEdge(DirectedGraphEdge edge) {
		if (edge instanceof Arc) {
			arcs.remove(edge);
		} else {
			assert (false);
		}
		graphElementRemoved(edge);
	}

	public synchronized void removeNode(DirectedGraphNode node) {
		if (node instanceof Function) {
			removeFunction((Function) node);
		} else if (node instanceof Event) {
			removeEvent((Event) node);
		} else if (node instanceof Connector) {
			removeConnector((Connector) node);
		} else {
			assert (false);
		}
	}

	public Set<EPCEdge<? extends EPCNode, ? extends EPCNode>> getEdges() {
		Set<EPCEdge<? extends EPCNode, ? extends EPCNode>> edges = new HashSet<EPCEdge<? extends EPCNode, ? extends EPCNode>>();
		edges.addAll(arcs);
		return Collections.unmodifiableSet(edges);
	}

	public Set<EPCNode> getNodes() {
		Set<EPCNode> nodes = new HashSet<EPCNode>();
		nodes.addAll(functions);
		nodes.addAll(events);
		nodes.addAll(connectors);
		return nodes;
	}

	public Collection<Connector> getConnectors() {
		return connectors;
	}

	public Collection<Event> getEvents() {
		return events;
	}

	public Collection<Function> getFunctions() {
		return functions;
	}

	public Collection<ConfigurableConnector> getConfigurableConnectors() {
		return confConnectors;
	}

	public Collection<ConfigurableFunction> getConfigurableFunctions() {
		return confFunctions;
	}

	public synchronized ConfigurableConnector addConnector(String label, ConnectorType type, boolean isConfigurable) {
		ConfigurableConnector c = new ConfigurableConnector(this, label, type, isConfigurable);
		connectors.add(c);
		confConnectors.add(c);
		graphElementAdded(c);
		return c;
	}

	public synchronized ConfigurableFunction addFunction(String label, boolean isConfigurable) {
		ConfigurableFunction f = new ConfigurableFunction(this, label, isConfigurable);
		functions.add(f);
		confFunctions.add(f);
		graphElementAdded(f);
		return f;
	}

	protected synchronized Map<DirectedGraphElement, DirectedGraphElement> cloneFrom(
			DirectedGraph<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>> graph) {
		AbstractConfigurableEPC epc = (AbstractConfigurableEPC) graph;
		HashMap<DirectedGraphElement, DirectedGraphElement> mapping = new HashMap<DirectedGraphElement, DirectedGraphElement>();

		for (Function f : epc.functions) {
			if (f instanceof ConfigurableEPCNode) {
				mapping.put(f, addFunction(f.getLabel(), ((ConfigurableEPCNode) f).isConfigurable()));
			} else {
				mapping.put(f, addFunction(f.getLabel()));
			}
		}
		for (Event e : epc.events) {
			mapping.put(e, addEvent(e.getLabel()));
		}
		for (Connector c : epc.connectors) {
			if (c instanceof ConfigurableEPCNode) {
				mapping.put(c, addConnector(c.getLabel(), c.getType(), ((ConfigurableEPCNode) c).isConfigurable()));
			} else {
				mapping.put(c, addConnector(c.getLabel(), c.getType()));
			}
		}
		for (Arc a : epc.arcs) {
			mapping.put(a, addArcPrivate((EPCNode) mapping.get(a.getSource()), (EPCNode) mapping.get(a.getTarget()), a
					.getLabel()));
		}

		getAttributeMap().clear();
		AttributeMap map = epc.getAttributeMap();
		for (String key : map.keySet()) {
			getAttributeMap().put(key, map.get(key));
		}

		return mapping;
	}

	protected synchronized Arc addArcPrivate(EPCNode source, EPCNode target, String label) {
		checkAddEdge(source, target);
		Arc arc = new Arc(source, target, label);
		arcs.add(arc);
		graphElementAdded(arc);
		return arc;
	}

}
