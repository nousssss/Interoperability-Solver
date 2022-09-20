package org.processmining.models.graphbased.directed.epc.elements;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.epc.ConfigurableEPCNode;
import org.processmining.models.graphbased.directed.epc.EPCEdge;
import org.processmining.models.graphbased.directed.epc.EPCNode;

public class ConfigurableFunction extends Function implements ConfigurableEPCNode {

	public ConfigurableFunction(AbstractDirectedGraph<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>> epc,
			String label, boolean isConfigurable) {
		super(epc, label);
		setConfigurable(isConfigurable);
		if (isConfigurable) {
			getAttributeMap().put(AttributeMap.BORDERWIDTH, 3);
		}
	}

	public ConfigurableFunction(AbstractDirectedGraph<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>> epc,
			String label) {
		this(epc, label, false);
	}

	private boolean isConfigurable;

	public boolean isConfigurable() {
		return isConfigurable;
	}

	public void setConfigurable(boolean isConfigurable) {
		this.isConfigurable = isConfigurable;

	}

}
