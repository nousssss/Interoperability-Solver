package org.processmining.plugins.petrinet.configurable.ui;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableParameterGraphElement;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableParameterInteger;
import org.processmining.plugins.petrinet.configurable.ui.impl.ConfigureGraphElement_UI;
import org.processmining.plugins.petrinet.configurable.ui.impl.ConfigureInteger_UI;

/**
 * Factory for creating a specific {@link ConfigureFeature_UI} object for a
 * given {@link ConfigurableParameter}
 * 
 * @author dfahland
 * 
 */
public class ConfigureFeature_UI_Factory {
	
	/**
	 * @param label
	 * @param feature
	 * @param line_height
	 * @return Panel containing configuration options for the given configurable feature
	 */
	public static ConfigureFeature_UI<?> 
		getUIforFeature(String label, ConfigurableParameter<?> feature)
	{
		int line_height = 40;
		if (feature instanceof ConfigurableParameterGraphElement) {
			return new ConfigureGraphElement_UI(label, (ConfigurableParameterGraphElement)feature, line_height);
		} else if (feature instanceof ConfigurableParameterInteger) {
			return new ConfigureInteger_UI(label, (ConfigurableParameterInteger)feature, line_height);
		}
		return null;
	}

}
