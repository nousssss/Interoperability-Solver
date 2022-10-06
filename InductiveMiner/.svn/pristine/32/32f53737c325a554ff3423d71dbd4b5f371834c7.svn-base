package org.processmining.plugins.inductiveminer2.plugins;

import java.util.Set;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

public class IdentifyPartialTracesPlugin {
	@Plugin(name = "Identify partial traces (in place)", level = PluginLevel.Regular, returnLabels = {
			"log" }, returnTypes = { XLog.class }, parameterLabels = { "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public XLog identify(final UIPluginContext context, XLog xLog) {
		IdentifyPartialTracesDialog dialog = new IdentifyPartialTracesDialog(xLog);
		InteractionResult result = context.showWizard("Identify partial traces", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		String attribute = dialog.getAttribute();
		Set<String> startValues = dialog.getStartValues();
		Set<String> endValues = dialog.getEndValues();

		for (XTrace xTrace : xLog) {
			if (!xTrace.isEmpty()) {

				//test and set start reliability
				{
					XEvent start = xTrace.get(0);
					XAttribute startAttribute = start.getAttributes().get(attribute);
					xTrace.getAttributes().put("startReliable", new XAttributeBooleanImpl("startReliable",
							startAttribute != null && startValues.contains(startAttribute.toString())));
				}

				//test and set end reliability
				{
					XEvent end = xTrace.get(xTrace.size() - 1);
					XAttribute endAttribute = end.getAttributes().get(attribute);
					xTrace.getAttributes().put("endReliable", new XAttributeBooleanImpl("endReliable",
							endAttribute != null && endValues.contains(endAttribute.toString())));
				}
			} else {
				xTrace.getAttributes().put("startReliable",
						new XAttributeBooleanImpl("startReliable", dialog.emptyTracesAreReliable()));
				xTrace.getAttributes().put("endReliable",
						new XAttributeBooleanImpl("endReliable", dialog.emptyTracesAreReliable()));
			}
		}

		return xLog;
	}
}
