/**
 * 
 */
package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.analysis.NonRelaxedSoundTransitionsSet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * @author aadrians
 * 
 */
public class NonRelaxedSoundTransitionsConnection extends AbstractSemanticConnection {
	public final static String TRANSITIONS = "ComponentSet";

	public NonRelaxedSoundTransitionsConnection(Petrinet net, Marking marking,
			Semantics<Marking, Transition> semantics, NonRelaxedSoundTransitionsSet causalNonRelaxedSoundTransitions) {
		super("Connection to unfireable transition causing unrelaxed sound of " + net.getLabel() + " (semantic "
				+ semantics.toString() + ")", net, marking, semantics);
		put(TRANSITIONS, causalNonRelaxedSoundTransitions);
	}
}