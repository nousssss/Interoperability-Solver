package org.processmining.models.graphbased.directed.petrinet;

import org.processmining.framework.annotations.AuthoredType;
import org.processmining.framework.annotations.Icon;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.ResetArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

@AuthoredType(typeName = "Reset net", affiliation = AuthoredType.TUE, author = "B.F. van Dongen", email = "b.f.v.dongen@tue.nl")
@Icon(icon = "resourcetype_petrinet_30x35.png")
public interface ResetNet extends PetrinetGraph {

	// reset arcs
	ResetArc addResetArc(Place p, Transition t, String label);

	ResetArc addResetArc(Place p, Transition t);

	ResetArc removeResetArc(Place p, Transition t);

	ResetArc getResetArc(Place p, Transition t);

	ResetArc addResetArc(Place p, Transition t, String label, ExpandableSubNet parent);

	ResetArc addResetArc(Place p, Transition t, ExpandableSubNet parent);


}
