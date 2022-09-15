package org.processmining.models.graphbased.directed.petrinet.configurable;

/**
 * A configurable parameter has a name ({@link #getId()}), a domain that is either given
 * as discrete enumeration or as a range of values in an interval, and a default value. 
 * 
 * @author dfahland
 *
 * @param <F>
 */
public abstract class ConfigurableParameter<F> {
	
	/**
	 * Create new configurable parameter ranging over a discrete domain of values, wit given default value.
	 * 
	 * @param element
	 * @param values
	 * @param defaultValue
	 * @throws InvalidConfigurationException
	 */
	public ConfigurableParameter(String id, F[] values, F defaultValue) throws InvalidConfigurationException {
		setDiscreteDomain(values);
		if (!isValidValue(defaultValue)) throw new InvalidConfigurationException(defaultValue+" is not a valid value for "+values);
		setValue(defaultValue);
		setId(id);
	}

	/**
	 * Create a new configurable parameter ranging over values from the given interval with a given default value.
	 * 
	 * @param element
	 * @param min
	 * @param max
	 * @param defaultValue
	 * @throws InvalidConfigurationException
	 */
	public ConfigurableParameter(String id, F min, F max, F defaultValue) throws InvalidConfigurationException {
		setIntervalDomain(min, max);
		if (!isValidValue(defaultValue)) throw new InvalidConfigurationException(defaultValue+" is not a valid value for interval ["+min+","+max+"]");
		setValue(defaultValue);
		setId(id);
	}
	
	private F min, max;
	private F[] domain;
	
	private F value;
	
	private String id;
	
	/**
	 * Set a range of values the feature can take.
	 *  
	 * @param min
	 * @param max
	 */
	public void setIntervalDomain(F min, F max) {
		this.min = min;
		this.max = max;
		this.domain = null;
	}
	
	/**
	 * @return minimum value if domain is an interval ({@link #isIntervalDomain()})
	 */
	public F getIntervalMin() {
		if (this.domain == null) return min;
		return null;
	}
	
	/**
	 * @return maximum value if domain is an interval ({@link #isIntervalDomain()})
	 */
	public F getIntervalMax() {
		if (this.domain == null) return max;
		return null;
	}

	
	/**
	 * Set a list of values the feature can take.
	 * @param values
	 */
	public void setDiscreteDomain(F[] values) {
		this.domain = values;
	}
	
	/**
	 * @return list of values, if domain is discrete ({@link #isIntervalDomain()})
	 */
	public F[] getDiscreteDomain() {
		return this.domain;
	}
	
	/**
	 * @return true iff domain of the feature is an interval domain (this is the
	 *         case when {@link #setIntervalDomain(Object, Object)} is used to
	 *         set the domain), and false iff domain of the feature is a
	 *         discrete domain (this is the case when
	 *         {@link #setDiscreteDomain(Object[])} is used to set the domain)
	 */
	public boolean isIntervalDomain() {
		return (domain == null);
	}
	
	
	/**
	 * Set chosen value of the feature. 
	 * @param value
	 * @throws InvalidConfigurationException if value is invalid for this feature
	 */
	@SuppressWarnings("unchecked")
	public void setValue(Object value) throws InvalidConfigurationException {
		if (!isValidValue((F)value)) throw new InvalidConfigurationException("Invalid value "+value);
		this.value = (F)value;
	}
	
	/**
	 * @return currently chosen value of the feature
	 */
	public F getValue() {
		return value;
	}
	
	/**
	 * @param value
	 * @return true iff the given value is in the domain of the feature
	 */
	public boolean isValidValue(F value) {
		if (isIntervalDomain()) return isValidIntervalValue(value);
		else {
			for (F v : domain) {
				if (v.equals(value)) return true;
			}
		}
		return false;
	}
	
	protected abstract boolean isValidIntervalValue(F value);
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Update domain and value of this parameter by copying values from the given parameter.
	 * @param update
	 * @throws InvalidConfigurationException
	 */
	public void updateParameter(ConfigurableParameter<F> update) throws InvalidConfigurationException {
		if (update.isIntervalDomain()) setIntervalDomain(update.getIntervalMin(), update.getIntervalMax());
		else setDiscreteDomain(update.getDiscreteDomain());
		
		setValue(update.getValue());
	}
}
