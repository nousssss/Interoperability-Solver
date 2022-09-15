package org.processmining.plugins.petrinet.configurable.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureGroup;
import org.processmining.plugins.petrinet.configurable.ui.AddFeatureWizardPage.NetElement;
import org.processmining.plugins.petrinet.configurable.ui.widgets.UIUtils;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * UI for a {@link ConfigurableFeatureGroup}
 * 
 * @author dfahland
 *
 */
public class ConfigurableFeatureGroup_UI {
	
	private static final long serialVersionUID = 1L;

	public ProMTextField	config_feature_id;
	private JPanel allFeaturePanel;

	public String groupID;
	public List<ConfigurableFeature_UI<?, ?>> features_ui = new LinkedList<ConfigurableFeature_UI<?, ?>>();
	private NetElement[] netElements;

	public boolean definesValuesForGroup = true;
	
	private JPanel container;
	private JButton groupRemoveButton;
	
	private JComponent root;
	
	private JPanel extendPanel;
	
	public ConfigurableFeatureGroup_UI(JComponent root, PetrinetGraph net, ConfigurableFeatureGroup group) {
		this.groupID = group.getId();
		this.root = root;
		this.netElements = NetElement.getNetElements(net);
		initialize();
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
			
			config_feature_id = new ProMTextField();
			config_feature_id.setMinimumSize(new Dimension(400, 30));
			config_feature_id.setPreferredSize(new Dimension(400, 30));
			config_feature_id.setMaximumSize(new Dimension(400, 30));
			headerLine.add(config_feature_id);
			
			groupRemoveButton = UIUtils.createCrossSignButton();
			groupRemoveButton.setVisible(false);

			headerLine.add(Box.createHorizontalGlue());
			headerLine.add(groupRemoveButton);
			headerLine.add(Box.createHorizontalStrut(5));
			
		container.add(Box.createVerticalStrut(5));
		container.add(headerLine);
		container.add(Box.createVerticalStrut(5));
		
		allFeaturePanel = new JPanel();
		allFeaturePanel.setOpaque(false);
		allFeaturePanel.setLayout(new BoxLayout(allFeaturePanel, BoxLayout.Y_AXIS));
		
		container.add(allFeaturePanel);
		container.add(Box.createVerticalStrut(5));
		
		extendPanel = createFeatureExtendPanel();
		container.add(extendPanel);
		container.add(Box.createVerticalStrut(5));
	}
	
	protected void addFeature(ConfigurableFeature_UI<?, ?> feat_ui) {
		features_ui.add(feat_ui);
		allFeaturePanel.add(feat_ui.getPanel());
		feat_ui.installFeatureRemoveHandler(new RemoveFeatureHandler(feat_ui));
	}
	
	public void setValues(ConfigurableFeatureGroup group, List<ConfigurableFeature_UI<?, ?>> features_ui) {
		config_feature_id.setText(group.getId());
		this.features_ui.clear();
		
		for (ConfigurableFeature_UI<?, ?> feat_ui : features_ui) {
			addFeature(feat_ui);
		}
	}
	
	protected void removeFeature(ConfigurableFeature_UI<?, ?> feat_ui) {
		allFeaturePanel.remove(feat_ui.getPanel());
		features_ui.remove(feat_ui);
		getRoot().revalidate();
	}
	
	public ConfigurableFeatureGroup getFeatureGroup() throws Exception {
		
		ConfigurableFeatureGroup group = new ConfigurableFeatureGroup(config_feature_id.getText());
		for (ConfigurableFeature_UI<?, ?> feat_ui : features_ui) {
			group.addFeature(feat_ui.getConfigured());
		}
		return group;
		
	}
	
	/**
	 * @return panel containing all controls
	 */
	public JPanel getPanel() {
		return container;
	}
	
	/**
	 * @return root component to update in case of changes in the visualization
	 */
	protected JComponent getRoot() {
		return root;
	}
	
	/**
	 * Add handler that handles the removal of this group from a surrounding component or environment
	 * @param handler
	 */
	public void installGroupRemoveHandler(ActionListener handler) {
		groupRemoveButton.addActionListener(handler);
		groupRemoveButton.setVisible(true);
	}

	protected NetElement[] getNetElements() {
		return netElements;
	}
	
	private JPanel createFeatureExtendPanel() {
		extendPanel = new AddFeatureWizard(this);
		return extendPanel;
	}
	
	/**
	 * Remove panel to configure a feature from this feature group - will remove feature from the group as well. 
	 * @author dfahland
	 */
	public class RemoveFeatureHandler implements ActionListener {
		public RemoveFeatureHandler(ConfigurableFeature_UI<?, ?> feat_ui) {
			this.feat_ui = feat_ui;
		}
		private ConfigurableFeature_UI<?, ?> feat_ui;
		
		public void actionPerformed(ActionEvent e) {
			ConfigurableFeatureGroup_UI.this.removeFeature(feat_ui);
		}
	}
	

}
