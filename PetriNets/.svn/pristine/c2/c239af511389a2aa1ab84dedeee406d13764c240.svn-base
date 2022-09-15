package org.processmining.models.graphbased.directed.petrinet.impl;

import javax.swing.SwingConstants;

import org.processmining.framework.providedobjects.SubstitutionType;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;

@SubstitutionType(substitutedType = InhibitorNet.class)
public class InhibitorNetImpl extends AbstractResetInhibitorNet implements InhibitorNet {

	public InhibitorNetImpl(String label) {
		super(false, true);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	@Override
	protected InhibitorNetImpl getEmptyClone() {
		return new InhibitorNetImpl(getLabel());
	}

}
