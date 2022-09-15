package org.processmining.models.graphbased.directed.petrinet.configurable.impl;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurationUtils;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;

/**
 * Abstract class to configure elements of a Petri net to be kept, removed, or
 * made invisible. Uses {@link ConfigurableGraphElementOption} to configure
 * graph elements.
 * 
 * @author dfahland
 * 
 * @param <T> the type of Petri net elements to be configured
 */
public abstract class ConfigurableGraphElementFeature<T extends DirectedGraphElement> extends ConfigurableParameterGraphElement implements ConfigurableFeature<T, ConfigurableGraphElementOption> {
	
	public ConfigurableGraphElementFeature(String id, T element, ConfigurableGraphElementOption[] values,
			ConfigurableGraphElementOption defaultValue) throws InvalidConfigurationException
	{
		super(id, values, defaultValue);
		configuredElement = element;
	}

	private T configuredElement;
	
	public String getId() {
		return super.getId();
	}
	
	public T getConfiguredElement() {
		return configuredElement;
	}
	
	public List<ConfigurableParameter<ConfigurableGraphElementOption>> getInputParameters() {
		LinkedList<ConfigurableParameter<ConfigurableGraphElementOption>> parameters = new LinkedList<ConfigurableParameter<ConfigurableGraphElementOption>>();
		parameters.add(this);
		return parameters;
	}
	
	public List<ConfigurableParameter<ConfigurableGraphElementOption>> getInputParametersByName() {
		return getInputParameters();
	}
	
	public void updateValue() {
		// do nothing, value is directly set on this object
	}
	
	public void setInputParameter(String key, Object value) throws InvalidConfigurationException {
		if (!getId().equals(key)) throw new InvalidConfigurationException("Unknown input parameter "+key);
		if (!(value instanceof ConfigurableGraphElementOption)) throw new InvalidConfigurationException("Invalid value "+value+" (expected: ConfigurableGraphElementOption)");
		
		setValue((ConfigurableGraphElementOption)value);
	}
	
	/**
	 * @param o1
	 * @param o2
	 * @return true iff o1 and o2 contain the same elements (in possibly different ordering)
	 */
	public static boolean sameOptions(ConfigurableGraphElementOption[] o1, ConfigurableGraphElementOption[] o2) {
		if (o1.length != o2.length) return false;
		for (int i=0; i<o1.length; i++) {
			boolean found = false;
			for (int j=0; j<o2.length; j++) {
				if (o1[i] == o2[i]) {
					found = true; break;
				}
			}
			if (!found) return false;
		}
		return true;
	}
	
	public static final ConfigurableGraphElementOption[] ALL = new ConfigurableGraphElementOption[] { ConfigurableGraphElementOption.ALLOW, ConfigurableGraphElementOption.BLOCK, ConfigurableGraphElementOption.SKIP };
	
	/**
	 * @param o
	 * @param allowed
	 * @return true iff o is in allowed
	 */
	public static boolean isValidValue(ConfigurableGraphElementOption o, ConfigurableGraphElementOption allowed[]) {
		for (int i=0; i<allowed.length; i++) {
			if (allowed[i] == o) return true;
		}
		return false;
	}
	
	public void updateElementVisualization() {
		if (getDiscreteDomain().length == 0) {
			clearElementVisualization();
		} else {
			switch (getValue()) {
				case ALLOW:
					getConfiguredElement().getAttributeMap().put(AttributeMap.STROKECOLOR, Color.GREEN);
					getConfiguredElement().getAttributeMap().put(AttributeMap.BORDERWIDTH, 5);
					break;
				case BLOCK:
					getConfiguredElement().getAttributeMap().put(AttributeMap.STROKECOLOR, Color.RED);
					getConfiguredElement().getAttributeMap().put(AttributeMap.BORDERWIDTH, 5);
					break;
				case SKIP:
					getConfiguredElement().getAttributeMap().put(AttributeMap.STROKECOLOR, Color.ORANGE);
					getConfiguredElement().getAttributeMap().put(AttributeMap.BORDERWIDTH, 5);
					break;
			}
		}
	}
	
	public void clearElementVisualization() {
		getConfiguredElement().getAttributeMap().remove(AttributeMap.STROKECOLOR);
		getConfiguredElement().getAttributeMap().remove(AttributeMap.BORDERWIDTH);
	}
	
	public static String generateID(DirectedGraphElement el) {
		return ConfigurationUtils.generateElementIDforFeature(el)+"_presence";
	}
	
}
