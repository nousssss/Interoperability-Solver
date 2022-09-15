package org.processmining.petrinets.list.factory;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.petrinets.list.PetriNetList;
import org.processmining.petrinets.list.impl.PetriNetListImpl;

public class PetriNetListFactory {

	public static PetriNetList createPetriNetList(Petrinet... nets) {
		PetriNetList list = new PetriNetListImpl();
		for (Petrinet p : nets) {
			list.add(p);
		}
		return list;
	}
}
