package org.processmining.plugins.interoperability;

import javax.swing.SwingConstants;

import org.processmining.framework.providedobjects.SubstitutionType;
import org.processmining.models.graphbased.AttributeMap;

@SubstitutionType(substitutedType = LabelledPetrinet.class)
public class LabelledPetrinetImpl extends LabelledAbstractResetInhibitorNet implements LabelledPetrinet {
	
	public LabelledPetrinetImpl(String label) {
		super(false, false);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	@Override
	public LabelledPetrinetImpl getEmptyClone() {
		return new LabelledPetrinetImpl(getLabel());
	}

}
