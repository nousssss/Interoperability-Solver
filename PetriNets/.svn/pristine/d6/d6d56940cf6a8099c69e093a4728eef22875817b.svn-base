package org.processmining.models.connections.petrinets.behavioral;

import org.processmining.framework.connections.impl.AbstractStrongReferencingConnection;
import org.processmining.framework.util.Cast;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;

public abstract class AbstractSemanticConnection extends AbstractStrongReferencingConnection {

	public final static String NET = "Net";
	public final static String MARKING = "Marking";
	public final static String SEMANTICS = "Semantics";

	AbstractSemanticConnection(String label, PetrinetGraph net, Marking marking,
			Semantics<Marking, Transition> semantics) {
		super(label);
		putStrong(SEMANTICS, semantics);
		put(NET, net);
		put(MARKING, marking);
	}

	public Semantics<Marking, Transition> getSemantics() {
		return Cast.<Semantics<Marking, Transition>>cast(get(SEMANTICS));
	}

}
