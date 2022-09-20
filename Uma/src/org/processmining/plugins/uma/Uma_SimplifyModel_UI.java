package org.processmining.plugins.uma;

import hub.top.uma.view.MineSimplify.Configuration;

import java.awt.Dimension;

import javax.swing.JLabel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;
import org.processmining.framework.util.ui.widgets.WidgetColors;

import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;

public class Uma_SimplifyModel_UI extends ProMPropertiesPanel {

	private static final long serialVersionUID = 1L;
	
	private javax.swing.JCheckBox refoldBox;
	private NiceSlider filterSlider;
	private NiceSlider impliedSlider;
	private javax.swing.JCheckBox chainBox;
	private javax.swing.JCheckBox flowerBox;
	
	private static final String DIALOG_NAME = "Which simplification steps shall be applied?";
	
	public Uma_SimplifyModel_UI(Configuration config) {
		super(DIALOG_NAME);
		
		refoldBox = addCheckBox("unfold and refold net", config.unfold_refold);
		
		filterSlider = new NiceSlider(null, 0, 100, (int)(config.filter_threshold*100), Orientation.HORIZONTAL) {
			private static final long serialVersionUID = 1L;
			protected String formatValue(int arg0) {
				return arg0+"%";
			}
		};
		filterSlider.remove(0); // remove the slider's label we already have one
		// set size and text of value label
		filterSlider.getComponent(0).setForeground(WidgetColors.TEXT_COLOR);
		filterSlider.getComponent(0).setMinimumSize(new Dimension(200, 16));
		filterSlider.getComponent(0).setPreferredSize(new Dimension(200, 16));
		((JLabel)filterSlider.getComponent(0)).setHorizontalAlignment(JLabel.RIGHT);
		// and put slider into the panel
		addProperty("filter branches <", filterSlider);
		
		impliedSlider = new NiceSlider(null, 0, Configuration.REMOVE_IMPLIED_PRESERVE_CONNECTED2, config.remove_implied, Orientation.HORIZONTAL) {
			private static final long serialVersionUID = 1L;
			protected String formatValue(int arg0) {
				switch(arg0) {
					case Configuration.REMOVE_IMPLIED_OFF:
						return "off";
					case Configuration.REMOVE_ILP:
						return "few / preserve all behavior";
					case Configuration.REMOVE_IMPLIED_PRESERVE_ALL:
						return "some / preserve precision";
					case Configuration.REMOVE_IMPLIED_PRESERVE_VISIBLE:
						return "more / preserve log";
					case Configuration.REMOVE_IMPLIED_PRESERVE_CONNECTED:
						return "many / preserve connectivity";
					case Configuration.REMOVE_IMPLIED_PRESERVE_CONNECTED2:
						return "most / preserve connectivity (fast)";
					default:
						return "undefined";
				}
			}
		};
		impliedSlider.remove(0); // remove the slider's label we already have one
		// set size and text of value label
		impliedSlider.getComponent(0).setForeground(WidgetColors.TEXT_COLOR);
		impliedSlider.getComponent(0).setMinimumSize(new Dimension(200, 16));
		impliedSlider.getComponent(0).setPreferredSize(new Dimension(200, 16));
		((JLabel)impliedSlider.getComponent(0)).setHorizontalAlignment(JLabel.RIGHT);
		// and put slider into the panel
		addProperty("removed implied places", impliedSlider);
		
		chainBox = addCheckBox("collapse chains", config.abstract_chains);
		flowerBox = addCheckBox("remove flower places", config.remove_flower_places);
	}
	
	/**
	 * Open UI dialogue to poppulate the given configuration object with
	 * settings chosen by the user.
	 * 
	 * @param context
	 * @param config
	 * @return result of the user interaction
	 */
	public InteractionResult setParameters(UIPluginContext context, Configuration config) {
		InteractionResult wish = getUserChoice(context);
		if (wish != InteractionResult.CANCEL) getChosenParameters(config);
		return wish;
	}
	
	/**
	 * @return Configuration as picked in the user interface, call only after
	 *         {@link #getUserChoice(UIPluginContext)} was called
	 */
	private void getChosenParameters(Configuration config) {
		config.unfold_refold = refoldBox.isSelected();
		config.filter_threshold = filterSlider.getSlider().getValue()*0.01;
		config.remove_implied = impliedSlider.getSlider().getValue();
		config.abstract_chains = chainBox.isSelected();
		config.remove_flower_places = flowerBox.isSelected();
	}
	
	/**
	 * display a dialog to ask user what to do
	 * 
	 * @param context
	 * @return
	 */
	protected InteractionResult getUserChoice(UIPluginContext context) {
		return context.showConfiguration("Simplify mined model using Uma", this);
	}
	
	/**
	 * Generate proper cancelling information for Uma. 
	 * @param context
	 * @return
	 */
	protected Object[] userCancel(PluginContext context) {
		return Uma_SimplifyModel.cancel(context, "The user has cancelled Uma.");
	}

	
	
}
