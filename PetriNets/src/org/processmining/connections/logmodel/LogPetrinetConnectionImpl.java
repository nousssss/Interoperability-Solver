package org.processmining.connections.logmodel;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.Cast;
import org.processmining.framework.util.Pair;
import org.processmining.models.connections.AbstractLogModelConnection;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class LogPetrinetConnectionImpl extends
		AbstractLogModelConnection<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>>
		implements LogPetrinetConnection {

	public LogPetrinetConnectionImpl(XLog log, XEventClasses classes, PetrinetGraph graph,
			Collection<Pair<Transition, XEventClass>> relations) {
		super(log, classes, graph, graph.getTransitions(), relations);
	}

	public LogPetrinetConnectionImpl(XLog log, XEventClasses classes, PetrinetGraph graph,
			Map<Transition, XEventClass> relations) {
		super(log, classes, graph, graph.getTransitions(), relations);
	}

	public LogPetrinetConnectionImpl(XLog log, XEventClasses classes, Petrinet graph,
			Collection<Pair<Transition, XEventClass>> relations) {
		this(log, classes, (PetrinetGraph) graph, relations);
	}

	public LogPetrinetConnectionImpl(XLog log, XEventClasses classes, ResetNet graph,
			Collection<Pair<Transition, XEventClass>> relations) {
		this(log, classes, (PetrinetGraph) graph, relations);
	}

	public LogPetrinetConnectionImpl(XLog log, XEventClasses classes, InhibitorNet graph,
			Collection<Pair<Transition, XEventClass>> relations) {
		this(log, classes, (PetrinetGraph) graph, relations);
	}

	public LogPetrinetConnectionImpl(XLog log, XEventClasses classes, ResetInhibitorNet graph,
			Collection<Pair<Transition, XEventClass>> relations) {
		this(log, classes, (PetrinetGraph) graph, relations);
	}

	public LogPetrinetConnectionImpl(XLog log, XEventClasses classes, Petrinet graph,
			Map<Transition, XEventClass> relations) {
		this(log, classes, (PetrinetGraph) graph, relations);
	}

	public LogPetrinetConnectionImpl(XLog log, XEventClasses classes, ResetNet graph,
			Map<Transition, XEventClass> relations) {
		this(log, classes, (PetrinetGraph) graph, relations);
	}

	public LogPetrinetConnectionImpl(XLog log, XEventClasses classes, InhibitorNet graph,
			Map<Transition, XEventClass> relations) {
		this(log, classes, (PetrinetGraph) graph, relations);
	}

	public LogPetrinetConnectionImpl(XLog log, XEventClasses classes, ResetInhibitorNet graph,
			Map<Transition, XEventClass> relations) {
		this(log, classes, (PetrinetGraph) graph, relations);
	}

	public Set<Transition> getTransitionsFor(XEventClass clazz) {
		return Cast.<Set<Transition>>cast(super.getNodesFor(clazz));
	}

	public Set<XEventClass> getActivitiesFor(Transition transition) {
		return Cast.<Set<XEventClass>>cast(super.getActivitiesFor(transition));
	}

	public Map<PetrinetNode, Set<XEventClass>> getActivityMap() {
		return node2activity;
	}

}
