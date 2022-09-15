package org.processmining.plugins.petrinet.configurable.ui;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.plugins.petrinet.configurable.ui.widgets.Configuration_UI;
import org.processmining.plugins.petrinet.configurable.ui.widgets.UIUtils;

public abstract class ConfigurableFeature_UI<T extends DirectedGraphElement, F extends Object> extends Configuration_UI<ConfigurableFeature<?, ?>, ConfigurableFeature<?, ?>>{

	public ConfigurableFeature_UI(JComponent root, ConfigurableFeature<T, F> feature) {
		this.element = feature.getConfiguredElement();
		setRoot(root);
	}
	
	private static final long serialVersionUID = 1L;
	
	private		T 			element;
	private		JButton 	removeButton;
	private		JPanel 		removeButtonReplacement;
	private		boolean		canBeRemoved;

	protected T getConfiguredElement() {
		return element;
	}
	
	protected void initializeOptionsPanel(JPanel optionsPanel) {
		
		JPanel featureOptions = new JPanel();
		featureOptions.setOpaque(false);
		featureOptions.setLayout(new BoxLayout(featureOptions, BoxLayout.X_AXIS));
		
		initializeFeatureOptionsPanel(featureOptions);
		
		removeButton = UIUtils.createCrossSignButton();
		removeButton.setVisible(false);
		removeButtonReplacement = new JPanel();
		removeButtonReplacement.setOpaque(false);
		removeButtonReplacement.setMinimumSize(new Dimension(30, 30));
		removeButtonReplacement.setPreferredSize(new Dimension(30, 30));
		removeButtonReplacement.setMaximumSize(new Dimension(30, 30));
		removeButtonReplacement.setVisible(true);
		
		optionsPanel.add(featureOptions);
		optionsPanel.add(Box.createHorizontalGlue());
		optionsPanel.add(Box.createHorizontalStrut(20));
		optionsPanel.add(removeButton);
		optionsPanel.add(removeButtonReplacement);
	}
	
	protected abstract void initializeFeatureOptionsPanel(JPanel featureOptionsPanel);
	
	protected void handlePanelIsActive() {
		if (canBeRemoved) {
			removeButton.setVisible(true);
			removeButtonReplacement.setVisible(false);
		}
	}
	
	protected void handlePanelIsInActive() {
		if (canBeRemoved) {
			removeButton.setVisible(false);
			removeButtonReplacement.setVisible(true);
		}
	}
	
	/**
	 * Add handler that handles the removal of this group from a surrounding component or environment
	 * @param handler
	 */
	public void installFeatureRemoveHandler(ActionListener handler) {
		removeButton.addActionListener(handler);
		canBeRemoved = true;
	}

	
}
