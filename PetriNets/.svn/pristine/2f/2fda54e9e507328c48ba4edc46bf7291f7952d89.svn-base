package org.processmining.plugins.petrinet.configurable.ui.impl;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableArcWeight;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurablePlaceMarking;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableIntegerFeature;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.plugins.petrinet.configurable.ui.ConfigurableFeature_UI;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * UI element to represent a {@link ConfigurableIntegerFeature}
 * 
 * @author dfahland
 *
 * @param <T> node type of this {@link ConfigurableIntegerFeature}
 */
public abstract class ConfigurableIntegerFeature_UI<T extends DirectedGraphElement> extends ConfigurableFeature_UI<T, Integer> implements FocusListener {

	public ConfigurableIntegerFeature_UI(JComponent root, ConfigurableIntegerFeature<T> feature, int line_height) {
		super(root, feature);
		initialize(feature.getId(), line_height);
	}
	
	protected NiceIntegerSlider slider;
	protected ProMTextField minValue;
	protected ProMTextField maxValue;
	
	protected void initializeFeatureOptionsPanel(JPanel optionsPanel) {
		SlickerFactory f = SlickerFactory.instance();
			
		minValue = new ProMTextField();
		minValue.addFocusListener(this);
		minValue.setMinimumSize(new Dimension(60, 30));
		minValue.setPreferredSize(new Dimension(60, 30));
		minValue.setMaximumSize(new Dimension(60, 30));
		optionsPanel.add(minValue);
		optionsPanel.add(Box.createHorizontalStrut(20));

		
		slider = f.createNiceIntegerSlider("value", 0, 100, 0, Orientation.HORIZONTAL);
		optionsPanel.add(slider);
		optionsPanel.add(Box.createHorizontalStrut(20));
		
		maxValue = new ProMTextField();
		maxValue.addFocusListener(this);
		maxValue.setMinimumSize(new Dimension(60, 30));
		maxValue.setPreferredSize(new Dimension(60, 30));
		maxValue.setMaximumSize(new Dimension(60, 30));
		optionsPanel.add(maxValue);
	}
	
	public void setValues(ConfigurableFeature<?, ?> input) {
		if (input instanceof ConfigurableIntegerFeature<?>) {
			ConfigurableIntegerFeature<?> feature = (ConfigurableIntegerFeature<?>)input;
			minValue.setText(feature.getIntervalMin().toString());
			maxValue.setText(feature.getIntervalMax().toString());
			slider.getSlider().setMinimum(feature.getIntervalMin());
			slider.getSlider().setMaximum(feature.getIntervalMax());
			slider.setValue(feature.getValue());
		}
	}
	
	
	public void focusGained(FocusEvent e) {
		updateSliderFromMinMax();
	}
	
	public void focusLost(FocusEvent e) {
		updateSliderFromMinMax();
	}
	
	protected void updateSliderFromMinMax() {
		try {
			Integer minVal = Integer.parseInt(minValue.getText());
			Integer maxVal = Integer.parseInt(maxValue.getText());
			slider.getSlider().setMinimum(minVal);
			slider.getSlider().setMaximum(maxVal);
			
			if (slider.getValue() < minVal) slider.setValue(minVal);
			if (slider.getValue() > maxVal) slider.setValue(maxVal);
		} catch (NullPointerException e) {
		}
	}

	public static class ConfigurablePlaceMarkingFeature_UI extends ConfigurableIntegerFeature_UI<Place> {
		public ConfigurablePlaceMarkingFeature_UI(JComponent root, ConfigurableIntegerFeature<Place> feature, int line_height) {
			super(root, feature, line_height);
		}
		public ConfigurableIntegerFeature<Place> getConfigured() throws InvalidConfigurationException {
			return new ConfigurablePlaceMarking(getId(), getConfiguredElement(), slider.getSlider().getMinimum(), slider.getSlider().getMaximum(), slider.getValue());
		}
	}
	
	public static class ConfigurableArcWeightFeature_UI extends ConfigurableIntegerFeature_UI<Arc> {
		public ConfigurableArcWeightFeature_UI(JComponent root, ConfigurableIntegerFeature<Arc> feature, int line_height) {
			super(root, feature, line_height);
		}
		public ConfigurableIntegerFeature<Arc> getConfigured() throws InvalidConfigurationException {
			return new ConfigurableArcWeight(getId(), getConfiguredElement(), slider.getSlider().getMinimum(), slider.getSlider().getMaximum(), slider.getValue());
		}
	}

}
