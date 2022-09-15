package org.processmining.models.graphbased.directed.petrinet.configurable.impl;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;

/**
 * A configurable parameter ranging over {@link Integer}.
 * 
 * @author dfahland
 *
 */
public class ConfigurableParameterInteger extends ConfigurableParameter<Integer> {

	public ConfigurableParameterInteger(String id, Integer min, Integer max, Integer defaultValue)
			throws InvalidConfigurationException {
		super(id, min, max, defaultValue);
	}
	
	protected final boolean isValidIntervalValue(Integer value) {
		return getIntervalMin() <= value && value <= getIntervalMax();
	}

}
