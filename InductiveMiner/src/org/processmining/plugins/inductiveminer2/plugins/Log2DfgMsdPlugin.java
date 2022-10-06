package org.processmining.plugins.inductiveminer2.plugins;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.Log2DfgMsd;

public class Log2DfgMsdPlugin {
	@Plugin(name = "Convert log to directly follows graph + minimum self-distance graph", returnLabels = {
			"Directly follows + minimum self-distance graph" }, returnTypes = { DfgMsd.class }, parameterLabels = {
					"Log" }, userAccessible = true, help = "Convert a log into a directly follows + minimum self-distance graph.")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public DfgMsd log2Dfg(UIPluginContext context, XLog log) {
		context.getFutureResult(0).setLabel(
				"Directly follows + minimum self-distance graph of " + XConceptExtension.instance().extractName(log));
		return Log2DfgMsd.convert(log, MiningParameters.getDefaultClassifier(),
				MiningParameters.getDefaultLifeCycleClassifier());
	}
}
