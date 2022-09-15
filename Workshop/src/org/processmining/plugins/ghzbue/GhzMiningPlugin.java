package org.processmining.plugins.ghzbue;

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
import org.processmining.models.ghzbue.GhzModel;

/**
 * Mining plug-in that mines an event log for a workshop model.
 * 
 * @author ghz
 * 
 */
@Plugin(name = "BUE miner ... ^_^", returnLabels = { "Ghz Model" }, returnTypes = { GhzModel.class }, parameterLabels = {
		"Log", "Configurations" }, userAccessible = true)
public class GhzMiningPlugin {

	/**
	 * Mining using default parameter values.
	 * 
	 * @param context
	 *            The given plug-in context.
	 * @param log
	 *            The given event log.
	 * @return The workshop model mined from the given log using the default
	 *         parameter values.
	 */
	@UITopiaVariant(affiliation = "BUE", author = "bueian", email = "ian@bue.com")
	@PluginVariant(variantLabel = "BUE miner, default -- NO CONFIG", requiredParameterLabels = { 0 })
	public GhzModel mineDefault(PluginContext context, XLog log) {
		return mineConfigured(context, log, new GhzMiningConfiguration());
	}

	/**
	 * Mining using given parameter values.
	 * 
	 * @param context
	 *            The given plug-in context.
	 * @param log
	 *            The given event log.
	 * @param parameters
	 *            The given parameter values.
	 * @return The workshop model mined from the given log using the given
	 *         parameter values.
	 */
	@UITopiaVariant(affiliation = "BUE", author = "bueian", email = "ian@bue.com")
	@PluginVariant(variantLabel = "BUE miner, Configured", requiredParameterLabels = { 0, 1 })
	public GhzModel mineConfigured(PluginContext context, XLog log, GhzMiningConfiguration config) {
		Collection<GhzMiningConnection> connections;
		try {
			connections = context.getConnectionManager().getConnections(GhzMiningConnection.class, context, log);
			for (GhzMiningConnection connection : connections) {
				if (connection.getObjectWithRole(GhzMiningConnection.LOG).equals(log)
						&& connection.getConfiguration().equals(config)) {
					return connection.getObjectWithRole(GhzMiningConnection.MODEL);
				}
			}
		} catch (ConnectionCannotBeObtained e) {
		}
		GhzModel model = mine(context, log, config);
		context.addConnection(new GhzMiningConnection(log, model, config));
		return model;
	}

	/**
	 * Mining using user-provided parameter values.
	 * 
	 * @param context
	 *            The given GUI-aware plug-in context.
	 * @param log
	 *            The given event log.
	 * @return The workshop model mined from the given log using the
	 *         user-provided parameter values.
	 */
	@UITopiaVariant(affiliation = "BUE", author = "bueian", email = "ian@bue.com")
	@PluginVariant(variantLabel = "BUE miner, USER INTERFACE", requiredParameterLabels = { 0 })
	public GhzModel mineDefault(UIPluginContext context, XLog log) {
		GhzMiningConfiguration config = new GhzMiningConfiguration();
		GhzMiningDialog dialog = new GhzMiningDialog(log, config);
		InteractionResult result = context.showWizard("Ghz Miner", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		return mineConfigured(context, log, config);
	}

	/*
	 * The actual mining of an event log for a workshop model given parameter
	 * values.
	 */
	private GhzModel mine(PluginContext context, XLog log, GhzMiningConfiguration config) {
		/*
		 * Create event classes based on the given classifier.
		 */
		XLogInfo info = XLogInfoFactory.createLogInfo(log, config.getClassifier());
		/*
		 * Create an empty model.
		 */
		GhzModel model = new GhzModel(info.getEventClasses());

		/*
		 * Inform the progress bar when we're done.
		 */
		context.getProgress().setMaximum(log.size());
		/*
		 * Fill the model based on the direct succession as encountered in the
		 * log.
		 */
		XEventClass fromEventClass = null, toEventClass = null;
		for (XTrace trace : log) {
			XEvent fromEvent = null;
			for (XEvent toEvent : trace) {
				fromEventClass = toEventClass;
				toEventClass = info.getEventClasses().getClassOf(toEvent);
				if (fromEvent != null) {
					model.addDirectSuccession(fromEventClass, toEventClass, 1);
				}
				fromEvent = toEvent;
			}
			/*
			 * Advance the progress bar.
			 */
			context.getProgress().inc();
		}

		/*
		 * Return the model.
		 */
		return model;
	}
}
