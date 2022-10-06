package org.processmining.plugins.inductiveminer2.plugins;

import javax.swing.JOptionPane;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParametersForPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.InductiveMiner.reduceacceptingpetrinet.ReduceAcceptingPetriNetKeepLanguage;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MiningParameters;

public class InductiveMinerPlugin {
	@Plugin(name = "Mine efficient tree with Inductive Miner", level = PluginLevel.Regular, returnLabels = {
			"Efficient Tree" }, returnTypes = {
					EfficientTree.class }, parameterLabels = { "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public EfficientTree mineGuiProcessTree(final UIPluginContext context, XLog xLog) {
		InductiveMinerDialog dialog = new InductiveMinerDialog(xLog);
		InteractionResult result = context.showWizard("Mine using Inductive Miner", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		MiningParameters parameters = dialog.getMiningParameters();
		IMLog log = parameters.getIMLog(xLog);

		//check that the log is not too big and mining might take a long time
		if (!confirmLargeLogs(context, log, dialog)) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		context.log("Mining...");

		return InductiveMiner.mineEfficientTree(log, parameters, new Canceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		});
	}

	@Plugin(name = "Mine accepting Petri net with Inductive Miner", level = PluginLevel.Regular, returnLabels = {
			"Accepting Petri net" }, returnTypes = {
					AcceptingPetriNet.class }, parameterLabels = { "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email, uiHelp = "Running this plug-in equals running:<br>1) \"Mine efficient tree with Inductive Miner\", <br>2) \"Reduce efficient tree language-equivalently for size\"<br>3) \"Convert efficient tree to Accepting Petri Net and reduce\" ")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public AcceptingPetriNet mineGuiAcceptingPetriNet(final UIPluginContext context, XLog xLog)
			throws UnknownTreeNodeException, ReductionFailedException {
		EfficientTree tree = mineGuiProcessTree(context, xLog);
		return postProcessTree2PetriNet(tree, new Canceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		});
	}

	public static EfficientTree mineTree(IMLog log, MiningParameters parameters, Canceller canceller) {
		return InductiveMiner.mineEfficientTree(log, parameters, canceller);
	}

	public static AcceptingPetriNet minePetriNet(IMLog log, MiningParameters parameters, Canceller canceller)
			throws UnknownTreeNodeException, ReductionFailedException {
		EfficientTree tree = mineTree(log, parameters, canceller);
		return postProcessTree2PetriNet(tree, canceller);
	}

	public static AcceptingPetriNet postProcessTree2PetriNet(EfficientTree tree, Canceller canceller)
			throws UnknownTreeNodeException, ReductionFailedException {
		if (tree == null || canceller.isCancelled()) {
			return null;
		}

		EfficientTreeReduce.reduce(tree, new EfficientTreeReduceParametersForPetriNet(false));

		AcceptingPetriNet net = EfficientTree2AcceptingPetriNet.convert(tree);

		if (net == null || canceller.isCancelled()) {
			return null;
		}

		ReduceAcceptingPetriNetKeepLanguage.reduce(net, canceller);
		return net;
	}

	public static boolean confirmLargeLogs(final UIPluginContext context, IMLog log, InductiveMinerDialog dialog) {
		if (dialog.getVariant().getWarningThreshold() > 0) {
			int numberOfActivities = log.getNumberOfActivities();
			if (numberOfActivities > dialog.getVariant().getWarningThreshold()) {
				int cResult = JOptionPane.showConfirmDialog(null,
						dialog.getVariant().toString() + " might take a long time, as the event log contains "
								+ numberOfActivities
								+ " activities.\nThe chosen variant of Inductive Miner is exponential in the number of activities.\nAre you sure you want to continue?",
						"Inductive Miner might take a while", JOptionPane.YES_NO_OPTION);

				return cResult == JOptionPane.YES_OPTION;
			}
		}
		return true;
	}
}
