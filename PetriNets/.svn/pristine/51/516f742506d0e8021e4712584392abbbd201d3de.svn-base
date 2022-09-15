package org.processmining.plugins.petrinet;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * <p>
 * Title: PetriNetShortCircuiter
 * </p>
 * <p>
 * Description: This class adds a transition to the Petrinet, such that it
 * consumes tokens from all places that have no outgoing arcs and it produces
 * places in all places that have no incoming arcs. A pointer to the Transition
 * is provided. If no places with no incoming arcs exist, or no places with no
 * outgoing arcs exist, null is returned.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Technische Universiteit Eindhoven
 * </p>
 * 
 * @author Boudewijn van Dongen
 * @version 1.0
 */

public class PetriNetShortCircuiter {

	public static synchronized Transition shortCircuit(PetrinetGraph net) {
		// First, short circuit the net.
		Set<Place> in = new HashSet<Place>();
		Set<Place> out = new HashSet<Place>();

		// put edge in form of key-value in order to ease checking of input or output edge
		Iterator<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> arcsIterator = net.getEdges().iterator();

		Hashtable<PetrinetNode, PetrinetNode> arcsSet = new Hashtable<PetrinetNode, PetrinetNode>();

		while (arcsIterator.hasNext()) {
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge = arcsIterator.next();
			arcsSet.put(edge.getSource(), edge.getTarget());
		}

		// check every places		
		Iterator<Place> it = net.getPlaces().iterator();
		while (it.hasNext()) {
			Place p = it.next();
			if (!arcsSet.containsKey(p)) {
				in.add(p);
			}
			if (!arcsSet.containsValue(p)) {
				out.add(p);
			}
		}

		// return null if every place has at least one input or one output arc
		if ((in.size() == 0) || (out.size() == 0)) {
			return null;
		}

		// if there are places without connected arc, add transition
		Transition extra = net.addTransition("Added by shortCircuiter");

		it = in.iterator();
		while (it.hasNext()) {
			net.addArc(extra, it.next());
			//			net.addEdge(extra, (Place) it.next());
		}
		it = out.iterator();
		while (it.hasNext()) {
			net.addArc(extra, it.next());
			//			net.addEdge((Place) it.next(), extra);
		}
		return extra;
	}

}
