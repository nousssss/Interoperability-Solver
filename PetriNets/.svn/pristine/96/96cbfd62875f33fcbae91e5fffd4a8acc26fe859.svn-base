package org.processmining.models.graphbased.directed.petrinet.impl;

import javax.swing.SwingConstants;

import org.processmining.framework.providedobjects.SubstitutionType;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

@SubstitutionType(substitutedType = Petrinet.class)
public class PetrinetImpl extends AbstractResetInhibitorNet implements Petrinet {

	public PetrinetImpl(String label) {
		super(false, false);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	@Override
	protected PetrinetImpl getEmptyClone() {
		return new PetrinetImpl(getLabel());
	}

}
