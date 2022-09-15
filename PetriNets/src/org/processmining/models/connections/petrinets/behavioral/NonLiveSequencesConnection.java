package org.processmining.models.connections.petrinets.behavioral;

import java.lang.ref.WeakReference;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.analysis.NonLiveSequences;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

public class NonLiveSequencesConnection extends AbstractSemanticConnection {
	public final static String SEQUENCES = "Sequences";
	public final static String FINALMARKINGS = "Allowed Final Markings";

	public NonLiveSequencesConnection(PetrinetGraph net, NonLiveSequences sequences, Marking marking,
			Marking[] allowedFinalMarkings, Semantics<Marking, Transition> semantics) {
		super("Connection to non-live sequences of " + net.getLabel() + " (represents " + semantics.toString() + ")", net, marking,
				semantics);
		put(SEQUENCES, sequences);

		WeakReference<?>[] markingRefs = new WeakReference<?>[allowedFinalMarkings.length];
		for (int i = 0; i < markingRefs.length; i++) {
			markingRefs[i] = new WeakReference<Marking>(allowedFinalMarkings[i]);
		}

		putStrong(FINALMARKINGS, markingRefs);
	}

	@Override
	public boolean isRemoved() {
		if (super.isRemoved()) {
			return true;
		} else {
			for (WeakReference<?> ref : (WeakReference<?>[]) get(FINALMARKINGS)) {
				Object o = ref.get();
				if (o == null) {
					remove(FINALMARKINGS);
					put(FINALMARKINGS, new WeakReference<?>[0]);
					return true;
				}
			}
		}
		return false;
	}

}
