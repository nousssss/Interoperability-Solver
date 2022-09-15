package org.processmining.plugins.petrinet.configurable.ui;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureGroup;
import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * Represents a {@link ConfigurableFeatureGroup} for configuring a Petri net.
 * 
 * @author dfahland
 *
 */
public class ConfigureFeatureGroup_UI {
	
	private static final long serialVersionUID = 1L;

	private JLabel config_feature_id;
	private JCheckBox doConfigure;
	private JPanel allFeaturePanel;

	public String groupID;
	public List<ConfigureFeature_UI<?>> features_ui = new LinkedList<ConfigureFeature_UI<?>>();

	private JPanel container;
	
	private ConfigurableFeatureGroup originalGroup;
	private boolean forcedDoConfigure;
	
	public ConfigureFeatureGroup_UI(ConfigurableFeatureGroup group, boolean forcedDoConfigure) {
		this.groupID = group.getId();
		this.forcedDoConfigure = forcedDoConfigure;
		initialize();
		originalGroup = group;
	}
	
	private void initialize() {
		SlickerFactory f = SlickerFactory.instance();

		container = new JPanel();
		container.setOpaque(false);
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		JPanel headerLine = new JPanel();
			headerLine.setOpaque(false);
			headerLine.setMinimumSize(new Dimension(300, 30));
			headerLine.setPreferredSize(new Dimension(500, 30));
			headerLine.setMaximumSize(new Dimension(1000, 30));
			headerLine.setLayout(new BoxLayout(headerLine, BoxLayout.X_AXIS));
			
			JLabel featureLabel = f.createLabel("feature");
			headerLine.add(featureLabel);
			headerLine.add(Box.createHorizontalStrut(5));
			
			config_feature_id = f.createLabel("");
			config_feature_id.setMinimumSize(new Dimension(400, 30));
			config_feature_id.setPreferredSize(new Dimension(400, 30));
			config_feature_id.setMaximumSize(new Dimension(400, 30));
			headerLine.add(config_feature_id);
			headerLine.add(Box.createHorizontalGlue());
			
			doConfigure = f.createCheckBox("configure", false);
			if (forcedDoConfigure) {
				doConfigure.setSelected(true);
				doConfigure.setEnabled(false);
			}
			headerLine.add(doConfigure);
			
		container.add(Box.createVerticalStrut(5));
		container.add(headerLine);
		container.add(Box.createVerticalStrut(5));
		
		allFeaturePanel = new JPanel();
		allFeaturePanel.setOpaque(false);
		allFeaturePanel.setLayout(new BoxLayout(allFeaturePanel, BoxLayout.Y_AXIS));
		
		container.add(allFeaturePanel);
		container.add(Box.createVerticalStrut(5));
	}
	
	public void setValues(ConfigurableFeatureGroup group, List<ConfigureFeature_UI<?>> features_ui) {
		config_feature_id.setText(group.getId());
		this.features_ui.clear();
		
		for (ConfigureFeature_UI<?> feat_ui : features_ui) {
			this.features_ui.add(feat_ui);
			allFeaturePanel.add(feat_ui.getPanel());
			allFeaturePanel.add(Box.createVerticalStrut(1));
		}
	}
	
	public void setDoConfigure(boolean configure) {
		doConfigure.setSelected(true);
	}
	
	public Configuration getConfiguration() throws Exception {
		
		if (doConfigure.isSelected()) {
			Configuration config = new Configuration(originalGroup.getId());
			for (ConfigureFeature_UI<?> feat_ui : features_ui) {
				config.put(feat_ui.getFeature().getId(), feat_ui.getConfigured());
			}
			return config;
		} else {
			return null;
		}
		
	}
	
	/**
	 * @return panel containing all controls
	 */
	public JPanel getPanel() {
		return container;
	}


}