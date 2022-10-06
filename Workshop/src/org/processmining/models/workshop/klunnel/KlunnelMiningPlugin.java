package org.processmining.models.workshop.klunnel;

import java.util.Collection;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.workshop.WorkshopModel;
import org.processmining.plugins.workshop.spoilers.WorkshopMiningConnection;

/**
 * Mining plug-in that mines and event log for a workshop model.
 * 
 * @author M.D. Brunings
 *
 */
@Plugin(name = "Mine a Workshop Model", returnLabels = { "Workshop Model" }, returnTypes = { WorkshopModel.class },
		parameterLabels = { "Log", "Parameters" }, userAccessible = true)
public class KlunnelMiningPlugin {

	/**
	 * KlunnelMine with default parameters.
	 * 
	 * @param context
	 *            The plug-in context.
	 * @param log
	 *            The log to mine.
	 * @return The Workshop model mined from the given log.
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "M.D. Brunings", email = "m.d.brunings@student.tue.nl")
	@PluginVariant(variantLabel = "Mine a Workshop Model, default", requiredParameterLabels = { 0 })
	public WorkshopModel mineDefault(PluginContext context, XLog log) {
		return mineParameters(context, log, new KlunnelMiningParameters());
	}

	/**
	 * KlunnelMine with custom provided parameters.
	 * 
	 * @param context
	 * @param log
	 * @param parameters
	 * @return
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "M.D. Brunings", email = "m.d.brunings@student.tue.nl")
	@PluginVariant(variantLabel = "Mine a Workshop Model, parameterized", requiredParameterLabels = { 0, 1 })
	public WorkshopModel mineParameters(PluginContext context, XLog log, KlunnelMiningParameters parameters) {
		Collection<KlunnelMiningConnection> connections;
		try {
			connections = context.getConnectionManager().getConnections(KlunnelMiningConnection.class, context, log);
			for (KlunnelMiningConnection connection : connections) {
				if (connection.getObjectWithRole(KlunnelMiningConnection.LOG).equals(log)
						&& connection.getParameters().equals(parameters)) {
					return connection.getObjectWithRole(WorkshopMiningConnection.MODEL);
				}
			}
		} catch (ConnectionCannotBeObtained e) {
		}
		WorkshopModel model = mine(context, log, parameters);
		context.addConnection(new KlunnelMiningConnection(log, model, parameters));
		return model;
	}

	/**
	 * KlunnelMine with parameter dialog.
	 * 
	 * @param context
	 * @param log
	 * @return
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "M.D. Brunings", email = "m.d.brunings@student.tue.nl")
	@PluginVariant(variantLabel = "Mine a Workshop Model, dialog", requiredParameterLabels = { 0 })
	public WorkshopModel mineDialog(UIPluginContext context, XLog log) {
		KlunnelMiningParameters parameters = new KlunnelMiningParameters();
		KlunnelMiningDialog dialog = new KlunnelMiningDialog(log, parameters);
		InteractionResult result = context.showWizard("KlunnelMiner", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		return mineParameters(context, log, parameters);
	}

	/**
	 * The actual mining of an event log for a workshop model given parameter
	 * values.
	 * 
	 * @param context
	 *            The context
	 * @param log
	 *            The event log
	 * @param parameters
	 *            The parameters
	 * @return The workshop model mined from the event log with given parameters
	 */
	private WorkshopModel mine(PluginContext context, XLog log, KlunnelMiningParameters parameters) {
		XLogInfo info = XLogInfoFactory.createLogInfo(log, parameters.getClassifier());
		WorkshopModel model = new WorkshopModel(info.getEventClasses());
		context.getProgress().setMaximum(log.size());
		for (XTrace trace : log) {
			XEventClass fromClass = null;
			for (XEvent event : trace) {
				XEventClass toClass = info.getEventClasses().getClassOf(event);
				if (fromClass != null) {
					model.addDirectSuccession(fromClass, toClass, 1);
				}
				fromClass = toClass;
			}
			context.getProgress().inc();
		}

		return model;
	}
}
