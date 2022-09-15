package org.processmining.plugins.petrinet.configurable.ui.impl;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableArc;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurablePlace;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ConfigurableTransition;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.petrinet.configurable.ui.ConfigurableFeature_UI;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * Abstract class to represent a {@link ConfigurableGraphElementFeature}
 * 
 * @author dfahland
 *
 * @param <T> node type of this {@link ConfigurableGraphElementFeature}
 */
public abstract class ConfigurableGraphElement_UI<T extends DirectedGraphElement> extends ConfigurableFeature_UI<T, ConfigurableGraphElementOption> {

	public ConfigurableGraphElement_UI(JComponent root, ConfigurableGraphElementFeature<T> feature, int line_height) {
		super(root, feature);
		initialize(feature.getId(), line_height);
	}

	public JCheckBox		config_allow;
	public JCheckBox		config_block;
	public JCheckBox		config_skip;
	public JComboBox 		config_default;
	//public ProMTextField	config_feature_id;

	public boolean definesValuesForGroup = true;
	
	
	
	/*
	 * (non-Javadoc)
	 * @see org.processmining.plugins.petrinet.configurable.ui.ConfigurableFeature_UI#initializeOptionsPanel(javax.swing.JPanel)
	 */
	@Override
	protected void initializeFeatureOptionsPanel(JPanel optionsPanel) {
		SlickerFactory f = SlickerFactory.instance();
		
		config_allow = f.createCheckBox(ConfigurableGraphElementOption.ALLOW.toString(), true);
		optionsPanel.add(config_allow);
		optionsPanel.add(Box.createHorizontalStrut(5));
		config_block = f.createCheckBox(ConfigurableGraphElementOption.BLOCK.toString(), true);
		optionsPanel.add(config_block);
		optionsPanel.add(Box.createHorizontalStrut(5));
		config_skip = f.createCheckBox(ConfigurableGraphElementOption.SKIP.toString(), true);
		optionsPanel.add(config_skip);
		optionsPanel.add(Box.createHorizontalStrut(20));
		
		JLabel defaultLabel = f.createLabel("default");
		optionsPanel.add(defaultLabel);
		optionsPanel.add(Box.createHorizontalStrut(5));

		config_default = f.createComboBox(ConfigurableGraphElementFeature.ALL);
		config_default.setMinimumSize(new Dimension(100, 30));
		config_default.setPreferredSize(new Dimension(200, 30));
		config_default.setMaximumSize(new Dimension(200, 30));
		optionsPanel.add(config_default);
	}
	
	public void setValues(ConfigurableFeature<?, ?> input) {
		if (input instanceof ConfigurableGraphElementFeature<?>) {
			ConfigurableGraphElementFeature<?> feature = (ConfigurableGraphElementFeature<?>)input;
		
			config_allow.setSelected(feature.isValidValue(ConfigurableGraphElementOption.ALLOW));
			config_block.setSelected(feature.isValidValue(ConfigurableGraphElementOption.BLOCK));
			config_skip.setSelected(feature.isValidValue(ConfigurableGraphElementOption.SKIP));
			switch (feature.getValue()) {
				case ALLOW: config_default.setSelectedItem(ConfigurableGraphElementOption.ALLOW); break;
				case BLOCK: config_default.setSelectedItem(ConfigurableGraphElementOption.BLOCK); break;
				case SKIP: config_default.setSelectedItem(ConfigurableGraphElementOption.SKIP); break;
			}
		}
	}
	
	protected ConfigurableGraphElementOption[] getPossibleValues() {
		List<ConfigurableGraphElementOption> possibleValues = new LinkedList<ConfigurableGraphElementOption>();
		if (config_allow.isSelected()) possibleValues.add(ConfigurableGraphElementOption.ALLOW);
		if (config_block.isSelected()) possibleValues.add(ConfigurableGraphElementOption.BLOCK);
		if (config_skip.isSelected()) possibleValues.add(ConfigurableGraphElementOption.SKIP);
		return possibleValues.toArray(new ConfigurableGraphElementOption[possibleValues.size()]);
	}
	
	protected ConfigurableGraphElementOption getDefaultValue() {

		ConfigurableGraphElementOption possibleValues[] = getPossibleValues();
		ConfigurableGraphElementOption defaultValue = possibleValues[0];
		
		if (   config_default.getSelectedItem() == ConfigurableGraphElementOption.ALLOW
			&& config_allow.isSelected()) 
			defaultValue = ConfigurableGraphElementOption.ALLOW;
		
		if (   config_default.getSelectedItem() == ConfigurableGraphElementOption.BLOCK
			&& config_block.isSelected()) 
				defaultValue = ConfigurableGraphElementOption.BLOCK;

		if (config_default.getSelectedItem() == ConfigurableGraphElementOption.SKIP
			&& config_skip.isSelected()) 
				defaultValue = ConfigurableGraphElementOption.SKIP;
		
		return defaultValue;
	}
	
	public static class ConfigurableTransition_UI extends ConfigurableGraphElement_UI<Transition> {

		public ConfigurableTransition_UI(JComponent root, ConfigurableGraphElementFeature<Transition> feature, int line_height) {
			super(root, feature, line_height);
		}
		public ConfigurableGraphElementFeature<Transition> getConfigured() throws InvalidConfigurationException {
			return new ConfigurableTransition(getId(), getConfiguredElement(), getPossibleValues(), getDefaultValue());
		}

	}
	

	public static class ConfigurablePlace_UI extends ConfigurableGraphElement_UI<Place> {

		public ConfigurablePlace_UI(JComponent root, ConfigurableGraphElementFeature<Place> feature, int line_height) {
			super(root, feature, line_height);
		}
		public ConfigurableGraphElementFeature<Place> getConfigured() throws InvalidConfigurationException {
			return new ConfigurablePlace(getId(), getConfiguredElement(), getPossibleValues(), getDefaultValue());
		}
	}
	
	public static class ConfigurableArc_UI extends ConfigurableGraphElement_UI<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> {

		public ConfigurableArc_UI(JComponent root, ConfigurableGraphElementFeature<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> feature, int line_height) {
			super(root, feature, line_height);
		}
		public ConfigurableGraphElementFeature<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> getConfigured() throws InvalidConfigurationException {
			return new ConfigurableArc(getId(), getConfiguredElement(), getPossibleValues(), getDefaultValue());
		}
	}

}
