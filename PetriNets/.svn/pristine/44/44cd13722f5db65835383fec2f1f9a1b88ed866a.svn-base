package org.processmining.models.semantics.petrinet.impl;

import org.processmining.framework.providedobjects.SubstitutionType;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.ExecutionInformation;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetExecutionInformation;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;

@SubstitutionType(substitutedType = PetrinetSemantics.class)
class PetrinetSemanticsImpl extends AbstractResetInhibitorNetSemantics implements PetrinetSemantics {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1863753685175892937L;

	public PetrinetSemanticsImpl() {
	}

	public ExecutionInformation executeTransition(Transition toExecute) {
		Marking required = getRequired(toExecute);
		Marking removed = state.minus(required);
		Marking produced = getProduced(toExecute);
		state.addAll(produced);

		return new PetrinetExecutionInformation(required, removed, produced, toExecute);
	}
}
