package org.processmining.models.graphbased.directed.epc;

import java.util.Collection;
import java.util.Set;

import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.epc.elements.Arc;
import org.processmining.models.graphbased.directed.epc.elements.Connector;
import org.processmining.models.graphbased.directed.epc.elements.Connector.ConnectorType;
import org.processmining.models.graphbased.directed.epc.elements.Event;
import org.processmining.models.graphbased.directed.epc.elements.Function;

public interface EPCGraph extends DirectedGraph<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>> {

	String getLabel();

	// functions
	Function addFunction(String label);

	Function removeFunction(Function function);

	Collection<Function> getFunctions();

	// events
	Event addEvent(String label);

	Event removeEvent(Event event);

	Collection<Event> getEvents();

	// connectors
	Connector addConnector(String label, ConnectorType type);

	Connector removeConnector(Connector connector);

	Collection<Connector> getConnectors();

	// arcs
	Arc addArc(EPCNode source, EPCNode target);

	Arc addArc(EPCNode source, EPCNode target, String label);

	Set<Arc> getArcs(EPCNode source, EPCNode target);

}
