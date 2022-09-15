package org.processmining.models.graphbased.directed.epc.elements;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.epc.ConfigurableEPCNode;
import org.processmining.models.graphbased.directed.epc.EPCEdge;
import org.processmining.models.graphbased.directed.epc.EPCNode;

public class ConfigurableConnector extends Connector implements ConfigurableEPCNode {

	public ConfigurableConnector(AbstractDirectedGraph<EPCNode, EPCEdge<? extends EPCNode, ? extends EPCNode>> epc,
			String label, ConnectorType type, boolean isConfigurable) {
		super(epc, label, type);
		setConfigurable(isConfigurable);
		if (isConfigurable) {
			getAttributeMap().put(AttributeMap.BORDERWIDTH, 3);
		}
	}

	private boolean isConfigurable;

	public boolean isConfigurable() {
		return isConfigurable;
	}

	public void setConfigurable(boolean isConfigurable) {
		this.isConfigurable = isConfigurable;

	}

}
