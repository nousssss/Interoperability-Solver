package org.processmining.plugins.workshop.spoilers;

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

/**
 * Mining plug-in that mines an event log for a workshop model.
 * 
 * @author hverbeek
 * 
 */
@Plugin(name = "Mine a Workshop Model_test", returnLabels = { "Workshop Model" }, returnTypes = { WorkshopModel.class }, parameterLabels = {
		"Log", "Parameters" }, userAccessible = true)
public class WorkshopMiningPlugin {

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
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	@PluginVariant(variantLabel = "Mine a Workshop Model, default", requiredParameterLabels = { 0 })
	public WorkshopModel mineDefault(PluginContext context, XLog log) {
		return mineParameters(context, log, new WorkshopMiningParameters());
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
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	@PluginVariant(variantLabel = "Mine a Workshop Model, parameterized", requiredParameterLabels = { 0, 1 })
	public WorkshopModel mineParameters(PluginContext context, XLog log, WorkshopMiningParameters parameters) {
		Collection<WorkshopMiningConnection> connections;
		try {
			connections = context.getConnectionManager().getConnections(WorkshopMiningConnection.class, context, log);
			for (WorkshopMiningConnection connection : connections) {
				if (connection.getObjectWithRole(WorkshopMiningConnection.LOG).equals(log)
						&& connection.getParameters().equals(parameters)) {
					return connection.getObjectWithRole(WorkshopMiningConnection.MODEL);
				}
			}
		} catch (ConnectionCannotBeObtained e) {
		}
		WorkshopModel model = mine(context, log, parameters);
		context.addConnection(new WorkshopMiningConnection(log, model, parameters));
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
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	@PluginVariant(variantLabel = "Mine a Workshop Model, dialog", requiredParameterLabels = { 0 })
	public WorkshopModel mineDefault(UIPluginContext context, XLog log) {
		WorkshopMiningParameters parameters = new WorkshopMiningParameters();
		WorkshopMiningDialog dialog = new WorkshopMiningDialog(log, parameters);
		InteractionResult result = context.showWizard("Workshop Miner", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		return mineParameters(context, log, parameters);
	}

	/*
	 * The actual mining of an event log for a workshop model given parameter
	 * values.
	 */
	private WorkshopModel mine(PluginContext context, XLog log, WorkshopMiningParameters parameters) {
		/*
		 * Create event classes based on the given classifier.
		 */
		XLogInfo info = XLogInfoFactory.createLogInfo(log, parameters.getClassifier());
		/*
		 * Create an empty model.
		 */
		WorkshopModel model = new WorkshopModel(info.getEventClasses());

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
