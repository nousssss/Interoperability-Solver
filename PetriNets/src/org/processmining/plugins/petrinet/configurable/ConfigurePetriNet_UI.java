package org.processmining.plugins.petrinet.configurable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMScrollablePanel;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureGroup;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;
import org.processmining.plugins.petrinet.configurable.ui.ConfigureFeatureGroup_UI;
import org.processmining.plugins.petrinet.configurable.ui.ConfigureFeature_UI;
import org.processmining.plugins.petrinet.configurable.ui.ConfigureFeature_UI_Factory;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

/**
 * 
 * @author dfahland
 */
public class ConfigurePetriNet_UI  extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public static final String DIALOG_TITLE = "Configure Petri net";
	
	private ArrayList<ConfigureFeatureGroup_UI> groups_ui = new ArrayList<ConfigureFeatureGroup_UI>();
	private JPanel transitionsPanel;
	
	public ConfigurePetriNet_UI(ConfigurablePetrinet<? extends PetrinetGraph> net, boolean configureAll) {
		
		final SlickerFactory f = SlickerFactory.instance();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// label
		add(f.createLabel("<html><h1>"+DIALOG_TITLE+"</h1>" +
				"<p>Select which transition has which configuration option.</p></html>"));
		
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
		
		for (ConfigurableFeatureGroup group : net.getConfigurableFeatureGroups()) {
			ConfigureFeatureGroup_UI group_ui = new ConfigureFeatureGroup_UI(group, configureAll);
			
			List<ConfigureFeature_UI<?>> features_ui = new LinkedList<ConfigureFeature_UI<?>>();
			for (ConfigurableParameter<?> feature : group.getInputParametersByName()) {
				
				String label = feature.getId();
				
				ConfigureFeature_UI<?> feature_ui = ConfigureFeature_UI_Factory.getUIforFeature(label, feature);
				feature_ui.setValues(feature);
				features_ui.add(feature_ui);
			}
			
			group_ui.setValues(group, features_ui);
			groups_ui.add(group_ui);
		}
		
		transitionsPanel = new JPanel();
		transitionsPanel.setOpaque(false);
		transitionsPanel.setLayout(new BoxLayout(transitionsPanel, BoxLayout.Y_AXIS));
		for (ConfigureFeatureGroup_UI t_ui : groups_ui){ 
			transitionsPanel.add(t_ui.getPanel());
		}
		transitionsPanel.validate();
		
		scrollPanel.add(transitionsPanel);
		
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
	 * @param net
	 * @return result of the user interaction
	 */
	public InteractionResult setParameters(UIPluginContext context, List<Configuration> configuration) throws Exception {
		InteractionResult wish = getUserChoice(context);
		if (wish != InteractionResult.CANCEL) setParametersFromUI(configuration);
		return wish;
	}
	
	protected void setParametersFromUI(List<Configuration> configurations) throws Exception {
		configurations.clear();
		
		for (ConfigureFeatureGroup_UI f_ui : groups_ui) {
			Configuration config = f_ui.getConfiguration();
			if (config != null) configurations.add(config);
		}
	}
}
