package org.processmining.models.factories;

import org.processmining.models.PetriNetList;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.impl.PetriNetListImpl;

// moved to package org.processmining.petrinets.factories
@Deprecated
public class PetriNetListFactory {

	public static PetriNetList createPetriNetList(Petrinet... nets) {
		PetriNetList list = new PetriNetListImpl();
		for (Petrinet p : nets) {
			list.add(p);
		}
		return list;
	}
}
