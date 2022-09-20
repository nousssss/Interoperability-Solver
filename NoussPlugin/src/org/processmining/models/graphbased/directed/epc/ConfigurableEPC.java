package org.processmining.models.graphbased.directed.epc;

import java.util.Collection;

import org.processmining.models.graphbased.directed.epc.elements.ConfigurableConnector;
import org.processmining.models.graphbased.directed.epc.elements.ConfigurableFunction;
import org.processmining.models.graphbased.directed.epc.elements.Connector.ConnectorType;

public interface ConfigurableEPC extends EPCGraph {

	ConfigurableFunction addFunction(String label, boolean isConfigurable);

	ConfigurableConnector addConnector(String label, ConnectorType type, boolean isConfigurable);

	Collection<ConfigurableConnector> getConfigurableConnectors();

	Collection<ConfigurableFunction> getConfigurableFunctions();

}
