package org.processmining.plugins.workshop.spoilers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.workshop.WorkshopModel;
import org.processmining.models.workshop.graph.WorkshopEdge;
import org.processmining.models.workshop.graph.WorkshopGraph;
import org.processmining.models.workshop.graph.WorkshopNode;

/**
 * Conversion plug-in from workshop models to workshop graphs.
 * 
 * @author hverbeek
 * 
 */
@Plugin(name = "Convert to Workshop Graph", returnLabels = { "Workshop Graph" }, returnTypes = { WorkshopGraph.class }, parameterLabels = {
		"Workshop Model", "Parameters" }, userAccessible = true)
public class WorkshopConversionPlugin {

	/**
	 * Conversion using default parameter values.
	 * 
	 * @param context
	 *            The given plug-in context.
	 * @param model
	 *            The given workshop model.
	 * @return The workshop graph that results from converting the given model
	 *         with the default parameter values.
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	@PluginVariant(variantLabel = "Convert to Workshop Graph, default", requiredParameterLabels = { 0 })
	public WorkshopGraph convertDefault(PluginContext context, WorkshopModel model) {
		return convertParameters(context, model, new WorkshopConversionParameters());
	}

	/**
	 * Conversion using given parameter values.
	 * 
	 * @param context
	 *            The given GUI-aware plug-in context.
	 * @param model
	 *            The given workshop model.
	 * @param parameters
	 *            The given parameters.
	 * @return The workshop graph that results from converting the given model
	 *         with the given parameter values.
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	@PluginVariant(variantLabel = "Convert to Workshop Graph, parameters", requiredParameterLabels = { 0, 1 })
	public WorkshopGraph convertParameters(PluginContext context, WorkshopModel model,
			WorkshopConversionParameters parameters) {
		Collection<WorkshopConversionConnection> connections;
		try {
			connections = context.getConnectionManager().getConnections(WorkshopConversionConnection.class, context, model);
			for (WorkshopConversionConnection connection : connections) {
				if (connection.getObjectWithRole(WorkshopConversionConnection.MODEL).equals(model)
						&& connection.getParameters().equals(parameters)) {
					return connection.getObjectWithRole(WorkshopConversionConnection.GRAPH);
				}
			}
		} catch (ConnectionCannotBeObtained e) {
		}
		WorkshopGraph graph = convert(context, model, parameters);
		context.addConnection(new WorkshopConversionConnection(model, graph, parameters));
		return graph;
	}

	/**
	 * Conversion using user-provided parameter values.
	 * 
	 * @param context
	 *            The given plug-in context.
	 * @param model
	 *            The given workshop model.
	 * @return The workshop graph that results from converting the given model
	 *         with the user-provided parameter values.
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	@PluginVariant(variantLabel = "Convert to Workshop Graph, dialog", requiredParameterLabels = { 0 })
	public WorkshopGraph convertDialog(UIPluginContext context, WorkshopModel model) {
		WorkshopConversionParameters parameters = new WorkshopConversionParameters();
		/*
		 * Pop-up a dialog that allows the user to change the parameter values.
		 */
		WorkshopConversionDialog dialog = new WorkshopConversionDialog(model, parameters);
		InteractionResult result = context.showWizard("Workshop Converter", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		/*
		 * Do the conversion with the user-provided parameter values.
		 */
		return convertParameters(context, model, parameters);
	}

	/*
	 * The actual conversion from a given workshop model using the given
	 * parameter values.
	 */
	private WorkshopGraph convert(PluginContext context, WorkshopModel model, WorkshopConversionParameters parameters) {
		WorkshopGraph graph = new WorkshopGraph("");
		Map<XEventClass, WorkshopNode> map = new HashMap<XEventClass, WorkshopNode>();
		
		/*
		 * Inform the progress bar when we're done.
		 */
		context.getProgress().setMaximum(model.getEventClasses().size() * (model.getEventClasses().size() + 1));
		/*
		 * Creates nodes for all event classes. Remember which node corresponds
		 * to which event class.
		 */
		for (XEventClass eventClass : model.getEventClasses()) {
			WorkshopNode node = new WorkshopNode(eventClass, graph);
			graph.addNode(node);
			map.put(eventClass, node);
			/*
			 * Advance the progress bar.
			 */
			context.getProgress().inc();
		}
		/*
		 * Creates edges for direct succession that meet the minimal cardinality
		 * as provided by the parameter values.
		 */
		for (XEventClass fromEventClass : model.getEventClasses()) {
			for (XEventClass toEventClass : model.getEventClasses()) {
				int cardinality = model.getDirectSuccession(fromEventClass, toEventClass);
				if (cardinality >= parameters.getMinCardinality()) {
					/*
					 * Direct succession meets cardinality threshold: Create
					 * edge.
					 */
					WorkshopEdge edge = new WorkshopEdge(map.get(fromEventClass), map.get(toEventClass), cardinality);
					graph.addEdge(edge);
				}
				/*
				 * Advance the progress bar.
				 */
				context.getProgress().inc();
			}
		}
		return graph;
	}
}
