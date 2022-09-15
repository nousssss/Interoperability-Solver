package org.processmining.connections.logmodel;

import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.processmining.framework.connections.Connection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public interface LogPetrinetConnection extends Connection {

	public Set<Transition> getTransitionsFor(XEventClass clazz);

	public Set<XEventClass> getActivitiesFor(Transition transition);

	public Map<PetrinetNode, Set<XEventClass>> getActivityMap();

	public XEventClasses getEventClasses();

}