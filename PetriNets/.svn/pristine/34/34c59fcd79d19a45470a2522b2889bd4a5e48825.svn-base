package org.processmining.models.graphbased.directed.petrinet.configurable.impl;

import java.util.LinkedList;
import java.util.List;

import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;

/**
 * Abstract class describing a configurable feature of a node or arc expressed by a range of integer values.
 * 
 * @author dfahland
 *
 * @param <T>
 */
public abstract class ConfigurableIntegerFeature<T extends DirectedGraphElement> extends ConfigurableParameterInteger implements ConfigurableFeature<T, Integer> {

	public ConfigurableIntegerFeature(String id, T element, Integer min, Integer max, Integer defaultValue)
			throws InvalidConfigurationException {
		super(id, min, max, defaultValue);
		configuredElement = element;
	}

	private T configuredElement;
	
	public String getId() {
		return super.getId();
	}
	
	public T getConfiguredElement() {
		return configuredElement;
	}
	
	public ConfigurableParameter<Integer> getParameter() {
		return this;
	}
	
	public List<ConfigurableParameter<Integer>> getInputParameters() {
		LinkedList<ConfigurableParameter<Integer>> parameters = new LinkedList<ConfigurableParameter<Integer>>();
		parameters.add(this);
		return parameters;
	}
	
	public List<ConfigurableParameter<Integer>> getInputParametersByName() {
		return getInputParameters();
	}
	
	public void updateValue() {
		// do nothing, value is directly set on this object
	}
	
	public void setInputParameter(String key, Object value) throws InvalidConfigurationException {
		if (!getId().equals(key)) throw new InvalidConfigurationException("Unknown input parameter "+key);
		if (!(value instanceof Integer)) throw new InvalidConfigurationException("Invalid value "+value+" (expected: Integer)");
		
		setValue((Integer)value);
	}

}
