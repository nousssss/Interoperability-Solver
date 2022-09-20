package org.processmining.plugins.uma;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.widgets.ProMWizardPanel;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.uma.Uma_UnfoldPN.UnfoldingConfiguration;

import com.fluxicon.slickerbox.components.RoundedPanel;

public class Uma_UnfoldPN_UI extends ProMWizardPanel {
	
	private static final long serialVersionUID = 1L;
	
	private JRadioButton buildPrefixButton;
	private JRadioButton checkSoundnessFCButton;
	private JTextField	boundField;
	
	public final static String DIALOG_TITLE = "Please choose the analysis task";  
	
	public Uma_UnfoldPN_UI (UnfoldingConfiguration config) {
		super(DIALOG_TITLE);
		
		JPanel buildPrefixPanel = new RoundedPanel();
		buildPrefixPanel.setLayout(new BoxLayout(buildPrefixPanel, BoxLayout.X_AXIS));
		buildPrefixPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		buildPrefixPanel.setMinimumSize(new Dimension(150, 20));
		buildPrefixPanel.setMaximumSize(new Dimension(150, 1000));
		buildPrefixPanel.setPreferredSize(new Dimension(150, 30));
		buildPrefixPanel.setBackground(new Color(0, 0, 0, 0));
		
			JLabel boundLabel = new JLabel("bound k = ");
			boundLabel.setForeground(WidgetColors.TEXT_COLOR);
			boundLabel.setOpaque(false);
			boundField = new JTextField("1");
			boundField.setOpaque(true);
			boundField.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
			boundField.setMinimumSize(new Dimension(40,20));
			boundField.setMaximumSize(new Dimension(40,20));
			boundField.setPreferredSize(new Dimension(40,20));
			buildPrefixPanel.add(boundLabel);
			buildPrefixPanel.add(boundField);

		addOption("build prefix", buildPrefixPanel);
		buildPrefixButton = optionButtons.getLast();
		
		addOption("check soundness (free-choice net)", null);
		checkSoundnessFCButton = optionButtons.getLast();
		
		switch (config.mode) {
			case UnfoldingConfiguration.BUILD_PREFIX:
				buildPrefixButton.setSelected(true);
				break;
			case UnfoldingConfiguration.CHECK_SOUNDNESS_FC:
				checkSoundnessFCButton.setSelected(true);
				break;
			default:
				buildPrefixButton.setSelected(true);
				break;
		}
		boundField.setText(Integer.toString(config.bound));
		
	}
	
	/**
	 * Open UI dialogue to poppulate the given configuration object with
	 * settings chosen by the user.
	 * 
	 * @param context
	 * @param config
	 * @return result of the user interaction
	 */
	protected InteractionResult setParameters(UIPluginContext context, UnfoldingConfiguration config) {
		InteractionResult wish = getUserChoice(context);
		if (wish != InteractionResult.CANCEL) getChosenParameters(config);
		return wish;
	}
	
	/**
	 * @return UnfoldingParameters as picked in the user interface, call only after
	 *         {@link #getUserChoice(UIPluginContext)} was called
	 */
	private void getChosenParameters(UnfoldingConfiguration param) {
		if (boundField.getText() != null && boundField.getText().length() > 0) {
			try {
				param.bound = Integer.parseInt(boundField.getText());
			} catch (NumberFormatException e) {
				param.bound = 1;
			}
		}
		
		if (buildPrefixButton.isSelected()) {
			param.mode = UnfoldingConfiguration.BUILD_PREFIX;
		} else if (checkSoundnessFCButton.isSelected()) {
			param.mode = UnfoldingConfiguration.CHECK_SOUNDNESS_FC;
		}
	}

	/**
	 * display a dialog to ask user what to do
	 * 
	 * @param context
	 * @return
	 */
	private InteractionResult getUserChoice(UIPluginContext context) {
		/*
		JPanel configPanel = new JPanel();
		configPanel.removeAll();
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
		configPanel.setBorder(BorderFactory.createEmptyBorder());
		
		configPanel.setMaximumSize(new Dimension(800, 500));
		configPanel.setMinimumSize(new Dimension(230, 180));
		configPanel.setPreferredSize(new Dimension(250, 180));
		
		JPanel headPanel = new RoundedPanel();
		headPanel.setLayout(new BoxLayout(headPanel, BoxLayout.X_AXIS));
		headPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		headPanel.setMinimumSize(new Dimension(350, 50));
		headPanel.setMaximumSize(new Dimension(1000, 50));
		headPanel.setPreferredSize(new Dimension(400, 50));
		headPanel.setBackground(SlickerColors.COLOR_BG_1);
		
			JLabel headLabel = new JLabel("Please choose the verification task");
			headLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
			
		headPanel.add(Box.createHorizontalGlue());
		headPanel.add(headLabel);
		headPanel.add(Box.createHorizontalGlue());
		
		JPanel buildPrefixPanel = new RoundedPanel();
		buildPrefixPanel.setLayout(new BoxLayout(buildPrefixPanel, BoxLayout.X_AXIS));
		buildPrefixPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		buildPrefixPanel.setMinimumSize(new Dimension(350, 50));
		buildPrefixPanel.setMaximumSize(new Dimension(1000, 50));
		buildPrefixPanel.setPreferredSize(new Dimension(400, 50));
		buildPrefixPanel.setBackground(SlickerColors.COLOR_BG_2);
		
		
			buildPrefixButton = new JRadioButton("construct prefix", true);
			buildPrefixButton.setUI(new SlickerRadioButtonUI());
			buildPrefixButton.setOpaque(false);
			buildPrefixButton.setForeground(SlickerColors.COLOR_FG);
			//buildPrefixButton.setMaximumSize(new Dimension(200,20));
			//buildPrefixButton.setPreferredSize(new Dimension(200,20));
			
			JLabel boundLabel = new JLabel("bound k = ");
			//filterLabel.setForeground(new Color(160, 160, 160));
			boundLabel.setForeground(SlickerColors.COLOR_FG);
			boundLabel.setOpaque(false);
			boundField = new JTextField("1");
			boundField.setOpaque(true);
			boundField.setBackground(COLOR_WHITE);
			boundField.setMinimumSize(new Dimension(40,20));
			boundField.setMaximumSize(new Dimension(40,20));
			boundField.setPreferredSize(new Dimension(40,20));
			
		buildPrefixPanel.add(buildPrefixButton);
		buildPrefixPanel.add(Box.createHorizontalGlue());
		buildPrefixPanel.add(boundLabel);
		buildPrefixPanel.add(boundField);
		
		JPanel checkSoundnessPanel = new RoundedPanel();
		checkSoundnessPanel.setLayout(new BoxLayout(checkSoundnessPanel, BoxLayout.X_AXIS));
		checkSoundnessPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		checkSoundnessPanel.setMinimumSize(new Dimension(350, 50));
		checkSoundnessPanel.setMaximumSize(new Dimension(1000, 50));
		checkSoundnessPanel.setPreferredSize(new Dimension(400, 50));
		checkSoundnessPanel.setBackground(SlickerColors.COLOR_BG_2);

			checkSoundnessFCButton = new JRadioButton("check soundness (free-choice net)", false);
			checkSoundnessFCButton.setUI(new SlickerRadioButtonUI());
			checkSoundnessFCButton.setOpaque(false);
			checkSoundnessFCButton.setForeground(SlickerColors.COLOR_FG);

		checkSoundnessPanel.add(checkSoundnessFCButton);
		checkSoundnessPanel.add(Box.createHorizontalGlue());


		ButtonGroup bgroup = new ButtonGroup();
		bgroup.add(buildPrefixButton);
		bgroup.add(checkSoundnessFCButton);

		configPanel.add(headPanel, BorderLayout.CENTER);
		configPanel.add(buildPrefixPanel, BorderLayout.CENTER);
		configPanel.add(checkSoundnessPanel, BorderLayout.CENTER);
		*/
		return context.showConfiguration("Uma: an Unfolding-based Model Analyzer", this);
	}
	
	/**
	 * Generate proper cancelling information for Uma. 
	 * @param context
	 * @return
	 */
	protected Petrinet userCancel(PluginContext context) {
		context.log("The user has cancelled Uma.");
		context.getFutureResult(0).cancel(true);
		return null;
	}
}
