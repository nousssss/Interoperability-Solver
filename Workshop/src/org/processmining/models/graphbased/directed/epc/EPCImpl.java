package org.processmining.models.graphbased.directed.epc;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;

class EPCImpl extends AbstractConfigurableEPC implements EPC {

	public EPCImpl(String label) {
		super(false);
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.NORTH);
	}

	@Override
	protected AbstractConfigurableEPC getEmptyClone() {
		return new EPCImpl(getLabel());
	}

}
