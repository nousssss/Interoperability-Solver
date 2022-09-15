package org.processmining.plugins.petrinet.configurable.ui.impl;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableParameterGraphElement;
import org.processmining.plugins.petrinet.configurable.ui.ConfigureFeature_UI;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * UI to represent a {@link ConfigurableGraphElementFeature} for configuring
 * 
 * @author dfahland
 *
 */
public class ConfigureGraphElement_UI extends ConfigureFeature_UI<ConfigurableGraphElementOption> {

	public ConfigureGraphElement_UI(String label, ConfigurableParameter<ConfigurableGraphElementOption> parameter, int line_height) {
		super(parameter);
		initialize(label, line_height);
	}

	public JRadioButton		config_allow;
	public JRadioButton		config_block;
	public JRadioButton		config_skip;
	
	/*
	 * (non-Javadoc)
	 * @see org.processmining.plugins.petrinet.configurable.ui.ConfigurableFeature_UI#initializeOptionsPanel(javax.swing.JPanel)
	 */
	@Override
	protected void initializeOptionsPanel(JPanel optionsPanel) {
		SlickerFactory f = SlickerFactory.instance();
		
		ButtonGroup group = new ButtonGroup();
		config_allow = f.createRadioButton(ConfigurableGraphElementOption.ALLOW.toString());
		optionsPanel.add(config_allow);
		optionsPanel.add(Box.createHorizontalStrut(5));
		config_block = f.createRadioButton(ConfigurableGraphElementOption.BLOCK.toString());
		optionsPanel.add(config_block);
		optionsPanel.add(Box.createHorizontalStrut(5));
		config_skip = f.createRadioButton(ConfigurableGraphElementOption.SKIP.toString());
		optionsPanel.add(config_skip);
		optionsPanel.add(Box.createHorizontalStrut(400));
		
		group.add(config_allow);
		group.add(config_block);
		group.add(config_skip);

	}
	
	public void setValues(ConfigurableParameter<?> input) {
		if (input instanceof ConfigurableParameterGraphElement) {
			ConfigurableParameterGraphElement feature = (ConfigurableParameterGraphElement)input;
			
			switch (feature.getValue()) {
				case ALLOW: config_allow.setSelected(true); break;
				case BLOCK: config_block.setSelected(true); break;
				case SKIP: config_skip.setSelected(true); break;
			}
			
			boolean allow = (feature.isValidValue(ConfigurableGraphElementOption.ALLOW));
			config_allow.setEnabled(allow);
			config_allow.setVisible(allow);
			
			boolean block = (feature.isValidValue(ConfigurableGraphElementOption.BLOCK));
			config_block.setEnabled(block);
			config_block.setVisible(block);
			
			boolean skip = (feature.isValidValue(ConfigurableGraphElementOption.SKIP));
			config_skip.setEnabled(skip);
			config_skip.setVisible(skip);
		}
	}
	

	public ConfigurableGraphElementOption getConfigured() {
		if (config_allow.isSelected()) return ConfigurableGraphElementOption.ALLOW;
		if (config_block.isSelected()) return ConfigurableGraphElementOption.BLOCK;
		if (config_skip.isSelected()) return ConfigurableGraphElementOption.SKIP;
		return null;
	}
	
}
