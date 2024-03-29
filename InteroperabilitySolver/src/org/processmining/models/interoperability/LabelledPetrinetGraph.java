package org.processmining.models.interoperability;


import java.util.Collection;

import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;

public interface LabelledPetrinetGraph extends 
    DirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> {
	

	String getLabel();

	// transitions
	LabelledTransition addTransition(String label);

	LabelledTransition addTransition(String label, ExpandableSubNet parent);

	LabelledTransition removeTransition(LabelledTransition transition);

	Collection<LabelledTransition> getTransitions();

	// transitions
	ExpandableSubNet addGroup(String label);

	ExpandableSubNet addGroup(String label, ExpandableSubNet parent);

	ExpandableSubNet removeGroup(ExpandableSubNet transition);

	Collection<ExpandableSubNet> getGroups();

	// places
	Place addPlace(String label);

	Place addPlace(String label, ExpandableSubNet parent);

	Place removePlace(Place place);

	Collection<Place> getPlaces();

	// arcs
	Arc addArc(Place p, LabelledTransition t, int weight);

	Arc addArc(Place p, LabelledTransition t);

	Arc addArc(LabelledTransition t, Place p, int weight);

	Arc addArc(LabelledTransition t, Place p);

	Arc addArc(Place p, LabelledTransition t, int weight, ExpandableSubNet parent);

	Arc addArc(Place p, LabelledTransition t, ExpandableSubNet parent);

	Arc addArc(LabelledTransition t, Place p, int weight, ExpandableSubNet parent);

	Arc addArc(LabelledTransition t, Place p, ExpandableSubNet parent);

	Arc removeArc(PetrinetNode source, PetrinetNode target);

	Arc getArc(PetrinetNode source, PetrinetNode target);

}