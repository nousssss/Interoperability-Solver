package org.processmining.petrinets.factories;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.petrinets.PetriNetList;
import org.processmining.petrinets.impl.PetriNetListImpl;

/**
 * 
 * @see org.processmining.petrinets.list.factory.PetriNetListFactory
 *
 */
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
