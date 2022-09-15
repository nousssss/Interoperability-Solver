package org.processmining.plugins.petrinet.configurable.ui.impl;

import javax.swing.Box;
import javax.swing.JPanel;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableIntegerFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableParameterInteger;
import org.processmining.plugins.petrinet.configurable.ui.ConfigureFeature_UI;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * UI to represent a {@link ConfigurableIntegerFeature} for configuring.
 * 
 * @author dfahland
 *
 */
public class ConfigureInteger_UI extends ConfigureFeature_UI<Integer> {

	public ConfigureInteger_UI(String label, ConfigurableParameter<Integer> feature, int line_height) {
		super(feature);
		initialize(label, line_height);
	}
	
	protected NiceIntegerSlider slider;
	
	protected void initializeOptionsPanel(JPanel optionsPanel) {
		SlickerFactory f = SlickerFactory.instance();
		
		slider = f.createNiceIntegerSlider("value", 0, 100, 0, Orientation.HORIZONTAL);
		optionsPanel.add(slider);
		optionsPanel.add(Box.createHorizontalStrut(20));
		
	}
	
	public void setValues(ConfigurableParameter<?> input) {
		if (input instanceof ConfigurableParameterInteger) {
			ConfigurableParameterInteger feature = (ConfigurableParameterInteger)input;
			slider.getSlider().setMinimum(feature.getIntervalMin());
			slider.getSlider().setMaximum(feature.getIntervalMax());
			slider.setValue(feature.getValue());
		}
	}
	
	public Integer getConfigured() {
		return slider.getValue();
	}
	
}
