package org.processmining.models.graphbased.directed.petrinet.impl;

import javax.swing.SwingConstants;

import org.processmining.framework.providedobjects.SubstitutionType;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;

@SubstitutionType(substitutedType = ResetInhibitorNet.class)
public class ResetInhibitorNetImpl extends AbstractResetInhibitorNet implements ResetInhibitorNet {

	public ResetInhibitorNetImpl(String label) {
		super(true, true);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	@Override
	protected ResetInhibitorNetImpl getEmptyClone() {
		return new ResetInhibitorNetImpl(getLabel());
	}

}
