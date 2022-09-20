package org.processmining.plugins.ghzbue;

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
import org.processmining.models.ghzbue.GhzModel;
import org.processmining.models.ghzbue.graph.GhzEdge;
import org.processmining.models.ghzbue.graph.GhzGraph;
import org.processmining.models.ghzbue.graph.GhzNode;

/**
 * Conversion plug-in from ghz models to ghz graphs.
 * 
 * @author Ghzzz
 * 
 */
@Plugin(name = "Convert to Ghz Graph", returnLabels = { "Ghz Graph" }, returnTypes = { GhzGraph.class }, parameterLabels = {
		"Ghz Model", "Config" }, userAccessible = true)
public class GhzConversionPlugin {
	
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
	@UITopiaVariant(affiliation = "BUE", author = "Ghz", email = "Ghz@bue.com")
	@PluginVariant(variantLabel = "Convert to Ghz Graph, default", requiredParameterLabels = { 0 })
	public GhzGraph convertDefault(PluginContext context, GhzModel model) {
		return convertConfigured(context, model, new GhzConversionConfiguration());
		
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
	@UITopiaVariant(affiliation = "BUE", author = "Ghz", email = "Ghz@bue.com")
	@PluginVariant(variantLabel = "Convert to Ghz Graph, configured", requiredParameterLabels = { 0, 1 })
	public GhzGraph convertConfigured(PluginContext context, GhzModel model,
			GhzConversionConfiguration ghzConversionConfiguration) {
		Collection<GhzConversionConnection> connections;
		try {
			connections  = context.getConnectionManager().getConnections(GhzConversionConnection.class, context, model);
			for (GhzConversionConnection connection : connections) {
				if (connection.getObjectWithRole(GhzConversionConnection.MODEL).equals(model)
						&& connection.getConfigurations().equals(ghzConversionConfiguration)) {
					return connection.getObjectWithRole(GhzConversionConnection.GRAPH);
				}
			}
		} catch (ConnectionCannotBeObtained e) {
		}
		GhzGraph graph = convert(context, model, ghzConversionConfiguration);
		context.addConnection(new GhzConversionConnection(model, graph, ghzConversionConfiguration));
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
	
	@UITopiaVariant(affiliation = "BUE", author = "Ghz", email = "Ghz@bue.com")
	@PluginVariant(variantLabel = "Convert to Ghz Graph, dialog", requiredParameterLabels = { 0 })
	public GhzGraph convertDialog(UIPluginContext context, GhzModel model) {
		GhzConversionConfiguration conf = new GhzConversionConfiguration();
		
		/*
		 * Pop-up a dialog that allows the user to change the parameter values.
		 */
		GhzConversionDialog dialog = new GhzConversionDialog(model, conf);
		InteractionResult result = context.showWizard("Ghz Converter", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		/*
		 * Do the conversion with the user-provided parameter values.
		 */
		return convertConfigured(context, model, conf);
	}
	
	/*
	 * The actual conversion from a given ghz model using the given
	 * parameter values.
	 */
	private GhzGraph convert(PluginContext context, GhzModel model,
			GhzConversionConfiguration ghzConversionConfiguration) {
		GhzGraph graph = new GhzGraph("");
		Map<XEventClass, GhzNode> map = new HashMap<XEventClass, GhzNode>();
		
		/*
		 * Inform the progress bar when we're done.
		 */
		context.getProgress().setMaximum(model.getEventClasses().size() * (model.getEventClasses().size() + 1));
		
		/*
		 * Creates nodes for all event classes. Remember which node corresponds
		 * to which event class.
		 */
		for (XEventClass eventClass : model.getEventClasses()) {
			GhzNode node = new GhzNode(eventClass, graph);
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
				if (cardinality >= ghzConversionConfiguration.getMinCardinality()) {
					/*
					 * Direct succession meets cardinality threshold: Create
					 * edge.
					 */
					GhzEdge edge = new GhzEdge(map.get(fromEventClass), map.get(toEventClass), cardinality);
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
