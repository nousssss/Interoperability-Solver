package org.processmining.models.graphbased.directed.petrinet.configurable;

import java.util.List;

/**
 * Groups several {@link ConfigurableFeature}s into one parameterized feature. The
 * {@link ConfigurableFeature}s are input parameters to this parameterized feature.
 * The value of each input parameter can be set separately.
 * 
 * @author dfahland
 *
 * @param <T>
 */
public interface ParameterizedFeature<T extends ConfigurableParameter<?>> {
	
	/**
	 * @return all parameters of this feature together with their chosen value
	 */
	public List<T> getInputParameters();
	
	/**
	 * @return input parameters that differ by their
	 *         {@link ConfigurableParameter#getId()}, i.e., from two input
	 *         parameters with same id, only one will be included
	 */
	public List<T> getInputParametersByName();
	
	/**
	 * Set value of an input parameter.
	 *  
	 * @param key
	 * @param value
	 */
	public void setInputParameter(String key, Object value) throws InvalidConfigurationException;
}
