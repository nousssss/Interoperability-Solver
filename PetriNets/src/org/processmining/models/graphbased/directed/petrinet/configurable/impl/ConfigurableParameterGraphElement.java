package org.processmining.models.graphbased.directed.petrinet.configurable.impl;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;

/**
 * A configurable parameter ranging over {@link ConfigurableGraphElementOption}.
 * @author dfahland
 *
 */
public class ConfigurableParameterGraphElement extends ConfigurableParameter<ConfigurableGraphElementOption> {

	public ConfigurableParameterGraphElement(String id, ConfigurableGraphElementOption[] values,
			ConfigurableGraphElementOption defaultValue) throws InvalidConfigurationException {
		super(id, values, defaultValue);
	}

	protected boolean isValidIntervalValue(ConfigurableGraphElementOption value) {
		return true;
	}

}
