package org.processmining.models.graphbased.directed.petrinet.impl;

import javax.swing.SwingConstants;

import org.processmining.framework.providedobjects.SubstitutionType;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;

@SubstitutionType(substitutedType = ResetNet.class)
public class ResetNetImpl extends AbstractResetInhibitorNet implements ResetNet {

	public ResetNetImpl(String label) {
		super(true, false);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	@Override
	protected ResetNetImpl getEmptyClone() {
		return new ResetNetImpl(getLabel());
	}

}
