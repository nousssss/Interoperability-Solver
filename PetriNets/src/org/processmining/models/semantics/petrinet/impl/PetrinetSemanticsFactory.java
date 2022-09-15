package org.processmining.models.semantics.petrinet.impl;

import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.semantics.petrinet.EfficientPetrinetSemantics;
import org.processmining.models.semantics.petrinet.InhibitorNetSemantics;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.ResetInhibitorNetSemantics;
import org.processmining.models.semantics.petrinet.ResetNetSemantics;

public class PetrinetSemanticsFactory {

	private PetrinetSemanticsFactory() {

	}

	public static PetrinetSemantics regularPetrinetSemantics(Class<? extends Petrinet> net) {
		return new PetrinetSemanticsImpl();
	}

	/**
	 * Creates a new {@link EfficientPetrinetSemantics}. This implementation
	 * requires the Petrinet to be supplied.
	 * 
	 * @param net
	 * @return
	 */
	public static EfficientPetrinetSemantics regularEfficientPetrinetSemantics(PetrinetGraph net) {
		return new EfficientPetrinetSemanticsImpl(net);
	}

	public static PetrinetSemantics elementaryPetrinetSemantics(Class<? extends Petrinet> net) {
		return new ElementaryPetrinetSemanticsImpl();
	}

	public static ResetNetSemantics regularResetNetSemantics(Class<? extends ResetNet> net) {
		return new ResetNetSemanticsImpl();
	}

	public static ResetNetSemantics elementaryResetNetSemantics(Class<? extends ResetNet> net) {
		return new ElementaryResetNetSemanticsImpl();
	}

	public static InhibitorNetSemantics regularInhibitorNetSemantics(Class<? extends InhibitorNet> net) {
		return new InhibitorNetSemanticsImpl();
	}

	public static InhibitorNetSemantics elementaryInhibitorNetSemantics(Class<? extends InhibitorNet> net) {
		return new ElementaryInhibitorNetSemanticsImpl();
	}

	public static ResetInhibitorNetSemantics regularResetInhibitorNetSemantics(Class<? extends ResetInhibitorNet> net) {
		return new ResetInhibitorNetSemanticsImpl();
	}

	public static ResetInhibitorNetSemantics elementaryResetInhibitorNetSemantics(
			Class<? extends ResetInhibitorNet> net) {
		return new ElementaryResetInhibitorNetSemanticsImpl();
	}
}
