package org.processmining.models.workshop.klunnel;

import java.util.Collection;

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
import org.processmining.plugins.workshop.spoilers.WorkshopConversionConnection;

@Plugin(name = "Workshop Model to Graph conversion", parameterLabels = { "Workshop Model", "Parameters" },
		returnLabels = { "Workshop Graph" }, returnTypes = { WorkshopGraph.class }, userAccessible = true)
public class KlunnelConversionPlugin {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "M.D. Brunings", email = "m.d.brunings@student.tue.nl")
	@PluginVariant(variantLabel = "Convert a Workshop Model to a Graph, default", requiredParameterLabels = { 0 })
	public WorkshopGraph convertDefault(PluginContext context, WorkshopModel model) {
		return convertParameters(context, model, new KlunnelConversionParameters());
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "M.D. Brunings", email = "m.d.brunings@student.tue.nl")
	@PluginVariant(variantLabel = "Convert a Workshop Model to a Graph, parameterized", requiredParameterLabels = { 0, 1 })
	public WorkshopGraph convertParameters(PluginContext context, WorkshopModel model,
			KlunnelConversionParameters parameters) {
		Collection<KlunnelConversionConnection> connections;
		try {
			connections = context.getConnectionManager().getConnections(KlunnelConversionConnection.class, context,
					model);
			for (KlunnelConversionConnection connection : connections) {
				if (connection.getObjectWithRole(KlunnelConversionConnection.MODEL).equals(model)
						&& connection.getParameters().equals(parameters)) {
					return connection.getObjectWithRole(WorkshopConversionConnection.GRAPH);
				}
			}
		} catch (ConnectionCannotBeObtained e) {
		}
		WorkshopGraph graph = convert(context, model, parameters);
		context.addConnection(new KlunnelConversionConnection(model, graph, parameters));
		return graph;
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "M.D. Brunings", email = "m.d.brunings@student.tue.nl")
	@PluginVariant(variantLabel = "Convert a Workshop Model to a Graph, dialog", requiredParameterLabels = { 0 })
	public WorkshopGraph convertDialog(UIPluginContext context, WorkshopModel model) {
		KlunnelConversionParameters parameters = new KlunnelConversionParameters();
		KlunnelConversionDialog dialog = new KlunnelConversionDialog(model, parameters);
		InteractionResult result = context.showWizard("KlunnelModel", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		return convertParameters(context, model, parameters);
	}

	private WorkshopGraph convert(PluginContext context, WorkshopModel model, KlunnelConversionParameters parameters) {
		WorkshopGraph graph = new WorkshopGraph("Workshop Graph");
		int size = model.getEventClasses().size();
		context.getProgress().setMaximum(size * ++ size);
		for (XEventClass eventClass : model.getEventClasses()) {
			graph.addNode(new WorkshopNode(eventClass, graph));
			context.getProgress().inc();
		}
		for (WorkshopNode fromNode : graph.getNodes()) {
			for (WorkshopNode toNode : graph.getNodes()) {
				int cardinality = model.getDirectSuccession(fromNode.getEventClass(), toNode.getEventClass());
				if (cardinality >= parameters.getMinCardinality()) {
					graph.addEdge(new WorkshopEdge(fromNode, toNode, cardinality));
				}
				context.getProgress().inc();
			}
		}
		return graph;
	}
}
