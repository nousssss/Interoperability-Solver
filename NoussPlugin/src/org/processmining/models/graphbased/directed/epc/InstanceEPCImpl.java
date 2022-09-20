package org.processmining.models.graphbased.directed.epc;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.epc.elements.Arc;
import org.processmining.models.graphbased.directed.epc.elements.Connector;
import org.processmining.models.graphbased.directed.epc.elements.Connector.ConnectorType;

class InstanceEPCImpl extends AbstractConfigurableEPC implements InstanceEPC {

	public InstanceEPCImpl(String label) {
		super(false);
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.NORTH);
	}

	@Override
	protected AbstractConfigurableEPC getEmptyClone() {
		return new InstanceEPCImpl(getLabel());
	}

	// need to check for connector type
	@Override
	public Connector addConnector(String label, ConnectorType type) {
		if (type != ConnectorType.AND) {
			throw new IllegalArgumentException("Cannot add connectors of type other than AND to an Instance EPC");
		}
		return super.addConnector(label, type);
	}

	// need to check for loops
	@Override
	protected Arc addArcPrivate(EPCNode source, EPCNode target, String label) {
		checkAddEdge(source, target);

		// TODO: loops.
		Arc arc = new Arc(source, target, label);
		arcs.add(arc);

		return arc;
	}

}
