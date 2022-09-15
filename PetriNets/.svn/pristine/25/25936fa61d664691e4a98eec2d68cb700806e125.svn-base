package org.processmining.models.semantics.petrinet;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.ExecutionInformation;

public class PetrinetExecutionInformation implements ExecutionInformation {

	private final Marking tokensConsumed;
	private final Marking tokensProduced;
	private final Marking necessary;
	private final Transition transition;

	public PetrinetExecutionInformation(Marking necessary, Marking consumed, Marking produced, Transition t) {
		this.necessary = necessary;
		tokensConsumed = consumed;
		tokensProduced = produced;
		transition = t;

	}

	public Marking getTokensConsumed() {
		return tokensConsumed;
	}

	public Marking getTokensProduced() {
		return tokensProduced;
	}

	public String toString() {
		return transition + ": Necessary: " + necessary + "    Produced: " + tokensProduced + "    Consumed: "
				+ tokensConsumed;
	}

	public Marking getNecessary() {
		return necessary;
	}

}
