package org.processmining.plugins.directlyfollowsgraph.mining.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.directlyfollowsgraph.DirectlyFollowsGraph;
import org.processmining.plugins.directlyfollowsgraph.mining.DFMMiner;
import org.processmining.plugins.directlyfollowsgraph.mining.DFMMiningParameters;

/**
 * Moved to the DirectlyFollowsModelMiner package
 * 
 * @author sander
 *
 */
@Deprecated
public class DirectlyFollowsModelMinerPlugin {
	@Plugin(name = "Mine directly follows model using DFMM", level = PluginLevel.Regular, returnLabels = {
			"Directly follows model" }, returnTypes = {
					DirectlyFollowsGraph.class }, parameterLabels = { "Log" }, userAccessible = false)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public DirectlyFollowsGraph mineGuiProcessTree(final UIPluginContext context, XLog xLog) {
		DirectlyFollowsModelMinerDialog dialog = new DirectlyFollowsModelMinerDialog(xLog);
		InteractionResult result = context.showWizard("Mine using Directly Follows Model Miner", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		DFMMiningParameters parameters = dialog.getMiningParameters();

		context.log("Mining...");

		return DFMMiner.mine(xLog, parameters, new Canceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		});
	}

}
