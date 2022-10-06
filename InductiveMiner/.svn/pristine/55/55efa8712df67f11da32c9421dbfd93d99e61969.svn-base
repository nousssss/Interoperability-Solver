package org.processmining.plugins.inductiveminer2.plugins;

import javax.swing.JOptionPane;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.inductiveminer2.withoutlog.InductiveMinerWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.MiningParametersWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class InductiveMinerWithoutLogPlugin {
	@Plugin(name = "Mine efficient tree with Inductive Miner", level = PluginLevel.Regular, returnLabels = {
			"Efficient Tree" }, returnTypes = { EfficientTree.class }, parameterLabels = {
					"Directly follows graph + minimum self-distance graph" }, userAccessible = true)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public EfficientTree mineGuiProcessTree(final UIPluginContext context, DfgMsd graph) {
		InductiveMinerWithoutLogDialog dialog = new InductiveMinerWithoutLogDialog(graph);
		InteractionResult result = context.showWizard("Mine using Inductive Miner", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		MiningParametersWithoutLog parameters = dialog.getMiningParameters();

		//check that the log is not too big and mining might take a long time
		if (!confirmLargeLogs(context, graph, dialog)) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		context.log("Mining...");

		return InductiveMinerWithoutLog.mineEfficientTree(graph, parameters, new Canceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		});
	}

	@Plugin(name = "Mine accepting Petri net with Inductive Miner", level = PluginLevel.Regular, returnLabels = {
			"Accepting Petri net" }, returnTypes = { AcceptingPetriNet.class }, parameterLabels = {
					"Directly follows graph + minimum self-distance graph" }, userAccessible = true)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email, uiHelp = "Running this plug-in equals running:<br>1) \"Mine efficient tree with Inductive Miner\", <br>2) \"Reduce efficient tree language-equivalently for size\"<br>3) \"Convert efficient tree to Accepting Petri Net and reduce\" ")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public AcceptingPetriNet mineGuiAcceptingPetriNet(final UIPluginContext context, DfgMsd graph)
			throws UnknownTreeNodeException, ReductionFailedException {
		EfficientTree tree = mineGuiProcessTree(context, graph);
		return InductiveMinerPlugin.postProcessTree2PetriNet(tree, new Canceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		});
	}

	public static EfficientTree mineTree(DfgMsd graph, MiningParametersWithoutLog parameters, Canceller canceller) {
		return InductiveMinerWithoutLog.mineEfficientTree(graph, parameters, canceller);
	}

	public static AcceptingPetriNet minePetriNet(DfgMsd graph, MiningParametersWithoutLog parameters,
			Canceller canceller) throws UnknownTreeNodeException, ReductionFailedException {
		EfficientTree tree = mineTree(graph, parameters, canceller);
		return InductiveMinerPlugin.postProcessTree2PetriNet(tree, canceller);
	}

	public static boolean confirmLargeLogs(final UIPluginContext context, DfgMsd graph,
			InductiveMinerWithoutLogDialog dialog) {
		if (dialog.getVariant().getWarningThreshold() > 0) {
			int numberOfActivities = graph.getNumberOfActivities();
			if (numberOfActivities > dialog.getVariant().getWarningThreshold()) {
				int cResult = JOptionPane.showConfirmDialog(null,
						dialog.getVariant().toString() + " might take a long time, as the graph contains "
								+ numberOfActivities
								+ " activities.\nThe chosen variant of Inductive Miner is exponential in the number of activities.\nAre you sure you want to continue?",
						"Inductive Miner might take a while", JOptionPane.YES_NO_OPTION);

				return cResult == JOptionPane.YES_OPTION;
			}
		}
		return true;
	}
}
