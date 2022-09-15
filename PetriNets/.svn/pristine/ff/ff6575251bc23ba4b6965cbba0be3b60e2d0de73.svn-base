package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.UnboundedSequences;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

public class UnboundedSequencesConnection extends AbstractSemanticConnection {
	public final static String SEQUENCES = "Sequences";

	public UnboundedSequencesConnection(PetrinetGraph net, UnboundedSequences sequences, Marking marking,
			Semantics<Marking, Transition> semantics) {
		super("Connection to unbounded sequences of " + net.getLabel() + " (represents " + semantics.toString() + ")", net, marking,
				semantics);
		put(SEQUENCES, sequences);
	}
}
