package org.processmining.plugins.petrinet.configurable.ui;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.plugins.petrinet.configurable.ui.widgets.Configuration_UI;

/**
 * Abstract class for a configurable feature.
 * @author dfahland
 *
 * @param <F> value of the parameter configured by this feature.
 */
public abstract class ConfigureFeature_UI<F> extends Configuration_UI<ConfigurableParameter<?>, F>{

	public ConfigureFeature_UI(ConfigurableParameter<F> parameter) {
		originalParameter = parameter;
	}
	
    private ConfigurableParameter<F> originalParameter;
    
    /**
     * @return original feature
     */
	public final ConfigurableParameter<F> getFeature() {
		return originalParameter;
	}
	
}
