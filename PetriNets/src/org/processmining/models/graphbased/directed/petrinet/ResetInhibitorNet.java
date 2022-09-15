package org.processmining.models.graphbased.directed.petrinet;

import org.processmining.framework.annotations.AuthoredType;
import org.processmining.framework.annotations.Icon;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.InhibitorArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.ResetArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

@AuthoredType(typeName = "Reset/Inhibitor net", affiliation = AuthoredType.TUE, author = "B.F. van Dongen", email = "b.f.v.dongen@tue.nl")
@Icon(icon = "resourcetype_petrinet_30x35.png")
public interface ResetInhibitorNet extends PetrinetGraph {

	// reset arcs
	ResetArc addResetArc(Place p, Transition t, String label);

	ResetArc addResetArc(Place p, Transition t);

	ResetArc removeResetArc(Place p, Transition t);

	ResetArc getResetArc(Place p, Transition t);

	ResetArc addResetArc(Place p, Transition t, String label, ExpandableSubNet parent);

	ResetArc addResetArc(Place p, Transition t, ExpandableSubNet parent);

	// inhibitor arcs
	InhibitorArc addInhibitorArc(Place p, Transition t, String label);

	InhibitorArc addInhibitorArc(Place p, Transition t);

	InhibitorArc removeInhibitorArc(Place p, Transition t);

	InhibitorArc getInhibitorArc(Place p, Transition t);

	InhibitorArc addInhibitorArc(Place p, Transition t, String label, ExpandableSubNet parent);

	InhibitorArc addInhibitorArc(Place p, Transition t, ExpandableSubNet parent);

}
