package org.processmining.models.graphbased.directed.epc;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;

class ConfigurableEPCImpl extends AbstractConfigurableEPC implements ConfigurableEPC {

	public ConfigurableEPCImpl(String label) {
		super(true);
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.NORTH);
	}

	@Override
	protected AbstractConfigurableEPC getEmptyClone() {
		return new ConfigurableEPCImpl(getLabel());
	}

}
