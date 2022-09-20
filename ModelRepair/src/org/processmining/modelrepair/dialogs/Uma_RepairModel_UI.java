package org.processmining.modelrepair.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JLabel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.widgets.LeftAlignedHeader;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.modelrepair.parameters.RepairConfiguration;
import org.processmining.modelrepair.plugins.Uma_RepairModel_Plugin;
import org.processmining.modelrepair.util.ui.widgets.ProMPropertiesPanel;

public class Uma_RepairModel_UI extends ProMPropertiesPanel {

	
	private static final long serialVersionUID = 1L;
	
	private javax.swing.JCheckBox detectLoopsBox;
	private javax.swing.JCheckBox detectSubprocessBox;
	private javax.swing.JCheckBox removeInfrequentBox;
	private javax.swing.JCheckBox globalCostBox;
	private javax.swing.JCheckBox alignSublogsBox;
	private ProMTextField loopModelMoveCosts;
	private ProMTextField remove_keepIfMoreThan;
	private ProMTextField globalCost_maxIterations;
	
	private static final String DIALOG_NAME = "Which model repair steps shall be applied?";
	
	public Uma_RepairModel_UI(RepairConfiguration config) {
		
		super(null);
		
		addToProperties(new LeftAlignedHeader(DIALOG_NAME));
		addToProperties(Box.createVerticalStrut(15));
		JLabel main_description = new JLabel();
		main_description.setAlignmentX(Component.RIGHT_ALIGNMENT);
		main_description.setText("Model repair runs three repair steps in the order given below. Each step is optional.");
		main_description.setOpaque(false);
		main_description.setFont(main_description.getFont().deriveFont(12f));
		main_description.setFont(main_description.getFont().deriveFont(Font.PLAIN));
		main_description.setMinimumSize(new Dimension(1000, 20));
		main_description.setMaximumSize(new Dimension(1000, 1000));
		main_description.setPreferredSize(new Dimension(1000, 30));
		addToProperties(main_description);
		addToProperties(Box.createVerticalStrut(15));

		
		detectLoopsBox = addCheckBox("detect loops", config.detectLoops, 0, 250);
		loopModelMoveCosts = addTextField("model move costs in loop detection", Integer.toString(config.loopModelMoveCosts), 1, 400);
		

		detectSubprocessBox = addCheckBox("detect subprocesses", config.detectSubProcesses, 0, 250);
		
		removeInfrequentBox = addCheckBox("remove infrequent nodes", config.removeInfrequentNodes, 0, 250);
		remove_keepIfMoreThan = addTextField("keep if more frequent than", Integer.toString(config.remove_keepIfAtLeast), 1, 400);

		
		addToProperties(Box.createVerticalStrut(15));
		addToProperties(new LeftAlignedHeader("Repair options"));
		addToProperties(Box.createVerticalStrut(15));

		alignSublogsBox = addCheckBox("align sublogs", config.alignAlignments, 0, 250);
		globalCostBox = addCheckBox("compute global cost function", config.globalCostAlignment, 0, 250);
		globalCost_maxIterations = addTextField("max iterations", Integer.toString(config.globalCost_maxIterations), 1, 400);
	}
	
	/**
	 * Open UI dialogue to poppulate the given configuration object with
	 * settings chosen by the user.
	 * 
	 * @param context
	 * @param config
	 * @return result of the user interaction
	 */
	public InteractionResult setParameters(UIPluginContext context, RepairConfiguration config) {
		InteractionResult wish = getUserChoice(context);
		if (wish != InteractionResult.CANCEL) getChosenParameters(config);
		return wish;
	}
	
	/**
	 * @return Configuration as picked in the user interface, call only after
	 *         {@link #getUserChoice(UIPluginContext)} was called
	 */
	private void getChosenParameters(RepairConfiguration config) {
		config.detectLoops = detectLoopsBox.isSelected();
		try {
			config.loopModelMoveCosts = Integer.parseInt(loopModelMoveCosts.getText());
		} finally {
		}
		config.detectSubProcesses = detectSubprocessBox.isSelected();
		config.removeInfrequentNodes = removeInfrequentBox.isSelected();
		try {
			config.remove_keepIfAtLeast = Integer.parseInt(remove_keepIfMoreThan.getText());
		} finally {
		}
		config.globalCostAlignment = globalCostBox.isSelected();
		try {
			config.globalCost_maxIterations = Integer.parseInt(globalCost_maxIterations.getText());
		} finally {
		}
		config.alignAlignments = alignSublogsBox.isSelected();
	}
	
	/**
	 * display a dialog to ask user what to do
	 * 
	 * @param context
	 * @return
	 */
	protected InteractionResult getUserChoice(UIPluginContext context) {
		return context.showConfiguration("Repair Model", this);
	}
	
	/**
	 * Generate proper cancelling information for Uma. 
	 * @param context
	 * @return
	 */
	protected Object[] userCancel(PluginContext context) {
		return Uma_RepairModel_Plugin.cancel(context, "The user has cancelled Uma.");
	}

}
