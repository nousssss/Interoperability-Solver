package org.processmining.plugins.petrinet.configurable;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.uitopia.ui.components.ImageLozengeButton;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMScrollablePanel;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureFactory;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureGroup;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.petrinet.configurable.ui.ConfigurableFeatureGroup_UI;
import org.processmining.plugins.petrinet.configurable.ui.ConfigurableFeature_UI;
import org.processmining.plugins.petrinet.configurable.ui.ConfigurableFeature_UI_Factory;
import org.processmining.plugins.petrinet.configurable.ui.widgets.UIUtils;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

/**
 * 
 * @author dfahland
 */
public class CreateConfigurableNet_UI  extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public static final String DIALOG_TITLE = "Create Configurable Petri net";
	
	private List<ConfigurableFeatureGroup_UI> groups_ui = new LinkedList<ConfigurableFeatureGroup_UI>();
	private JPanel transitionsPanel;
	
	public CreateConfigurableNet_UI(PetrinetGraph net, List<ConfigurableFeatureGroup> groups) {
		
		final SlickerFactory f = SlickerFactory.instance();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel scrollPanel = new ProMScrollablePanel();
		scrollPanel.setOpaque(false);
		scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
		
		final JScrollPane scrollPane = new JScrollPane(scrollPanel);
		scrollPane.setOpaque(false);
		scrollPane.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.getViewport().setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(false);
		vBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		vBar = scrollPane.getHorizontalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(false);
		vBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		add(scrollPane);


		for (ConfigurableFeatureGroup group : groups) {
			ConfigurableFeatureGroup_UI group_ui = new ConfigurableFeatureGroup_UI(this, net, group);
			
			List<ConfigurableFeature_UI<?, ?>> features_ui = new LinkedList<ConfigurableFeature_UI<?,?>>();
			for (ConfigurableFeature<? extends DirectedGraphElement, ? extends Object> feature : group.getFeatures()) {
				
//				String label = "";
//				if (feature.getConfiguredElement() instanceof PetrinetNode)
//				{
//					label = feature.getConfiguredElement().getLabel();
//				} else if (feature.getConfiguredElement() instanceof Arc) {
//					Arc a = (Arc)feature.getConfiguredElement();
//					label = a.getSource().getLabel()+" -> "+a.getTarget().getLabel();
//				}
				
				ConfigurableFeature_UI<?, ?> feature_ui = ConfigurableFeature_UI_Factory.getUIforFeature(this, feature);
				feature_ui.setIdEditable(true);
				feature_ui.setValues(feature);
				features_ui.add(feature_ui);
			}
			
			group_ui.setValues(group, features_ui);
			group_ui.installGroupRemoveHandler(new RemoveGroupHandler(group_ui));
			groups_ui.add(group_ui);
		}
		
		JPanel addGroupButtonPanel = new JPanel();
		addGroupButtonPanel.setOpaque(false);
		addGroupButtonPanel.setLayout(new BoxLayout(addGroupButtonPanel, BoxLayout.X_AXIS));
		
		ImageLozengeButton addGroupButton = new ImageLozengeButton(UIUtils.plusSign, "new feature group", Color.DARK_GRAY, new Color(0, 80, 0), 0);
		addGroupButton.setLabelColor(new Color(190, 190, 190));
		addGroupButton.addActionListener(new AddGroupHandler(net));
		addGroupButtonPanel.add(addGroupButton);
		addGroupButtonPanel.add(Box.createHorizontalStrut(20));

		addGroupButtonPanel.add(f.createLabel("<html><p>add groups of configurable features to this Petri net, and <br />" +
				  "add/set features for each group</html>"));
		
		ImageLozengeButton addNetGroupsButton = new ImageLozengeButton(UIUtils.plusSign, "all transitions", Color.DARK_GRAY, new Color(0, 80, 0), 0);
		addNetGroupsButton.setLabelColor(new Color(190, 190, 190));
		addNetGroupsButton.addActionListener(new AddTransitionsGroupHandler(net));
		addGroupButtonPanel.add(Box.createHorizontalGlue());
		addGroupButtonPanel.add(addNetGroupsButton);

		scrollPanel.add(addGroupButtonPanel);
		scrollPanel.add(Box.createVerticalStrut(10));
		
		transitionsPanel = new JPanel();
		transitionsPanel.setOpaque(false);
		transitionsPanel.setLayout(new BoxLayout(transitionsPanel, BoxLayout.Y_AXIS));
		
		for (ConfigurableFeatureGroup_UI group_ui : groups_ui){ 
			transitionsPanel.add(group_ui.getPanel());
		}
		transitionsPanel.validate();
		
		scrollPanel.add(transitionsPanel);
		
	}
	
	/**
	 * Remove panel to configure a feature from this feature group - will remove feature from the group as well. 
	 * @author dfahland
	 */
	public class RemoveGroupHandler implements ActionListener {
		private ConfigurableFeatureGroup_UI group_ui;
		public RemoveGroupHandler(ConfigurableFeatureGroup_UI group_ui) {
			this.group_ui = group_ui;
		}
		public void actionPerformed(ActionEvent e) {
			CreateConfigurableNet_UI.this.groups_ui.remove(group_ui);
			CreateConfigurableNet_UI.this.transitionsPanel.remove(group_ui.getPanel());
			CreateConfigurableNet_UI.this.transitionsPanel.revalidate();
			CreateConfigurableNet_UI.this.revalidate();
		}
	}
	
	public class AddGroupHandler implements ActionListener {
		public AddGroupHandler(PetrinetGraph net) {
			this.net = net;
		}
		private PetrinetGraph net;
		public void actionPerformed(ActionEvent e) {
			ConfigurableFeatureGroup group = new ConfigurableFeatureGroup("group id");
			ConfigurableFeatureGroup_UI group_ui = new ConfigurableFeatureGroup_UI(CreateConfigurableNet_UI.this, net, group);
			group_ui.setValues(group, new LinkedList<ConfigurableFeature_UI<?,?>>());
			
			group_ui.installGroupRemoveHandler(new RemoveGroupHandler(group_ui));
			groups_ui.add(0, group_ui);
			transitionsPanel.add(group_ui.getPanel(), 0);
			CreateConfigurableNet_UI.this.revalidate();
		}
	}
	
	public class AddTransitionsGroupHandler implements ActionListener {
		public AddTransitionsGroupHandler(PetrinetGraph net) {
			this.net = net;
		}
		private PetrinetGraph net;
		public void actionPerformed(ActionEvent e) {
			
			ConfigurableFeatureGroup group = new ConfigurableFeatureGroup("all transitions");
			List<ConfigurableFeature_UI<?,?>> feat_uis = new LinkedList<ConfigurableFeature_UI<?,?>>();
			for (Transition t : net.getTransitions()) {
				try {
					ConfigurableFeature<?,?> feature = ConfigurableFeatureFactory.createDefaultFeature(t, ConfigurableFeatureFactory.PARAM_FEATURE_PRESENCE, false);
					ConfigurableFeature_UI<?, ?> feat_ui = ConfigurableFeature_UI_Factory.getUIforFeature(CreateConfigurableNet_UI.this, feature);
					feat_ui.setValues(feature);
					feat_ui.setIdEditable(true);
					group.addFeature(feature);
					feat_uis.add(feat_ui);
				} catch (InvalidConfigurationException e1) {
				}
			}
			
			ConfigurableFeatureGroup_UI group_ui = new ConfigurableFeatureGroup_UI(CreateConfigurableNet_UI.this, net, group);
			group_ui.setValues(group, feat_uis);
			
			group_ui.installGroupRemoveHandler(new RemoveGroupHandler(group_ui));
			groups_ui.add(0, group_ui);
			transitionsPanel.add(group_ui.getPanel(), 0);
			CreateConfigurableNet_UI.this.revalidate();
		}
	}
	
	/**
	 * display a dialog to ask user what to do
	 * 
	 * @param context
	 * @return
	 */
	protected InteractionResult getUserChoice(UIPluginContext context) {
		return context.showConfiguration(DIALOG_TITLE, this);
	}
	
	/**
	 * Open UI dialogue to populate the given configuration object with
	 * settings chosen by the user.
	 * 
	 * @param context
	 * @param config
	 * @return result of the user interaction
	 * @throws InvalidConfigurationException 
	 */
	public InteractionResult setParameters(UIPluginContext context, List<ConfigurableFeatureGroup> groups) throws Exception {
		InteractionResult wish = getUserChoice(context);
		if (wish != InteractionResult.CANCEL) setParametersFromUI(groups);
		return wish;
	}
	
	protected void setParametersFromUI(List<ConfigurableFeatureGroup> groups) throws Exception {
		groups.clear();
		
		for (ConfigurableFeatureGroup_UI group_ui : groups_ui) {
			groups.add(group_ui.getFeatureGroup());
		}
		
	}
}
