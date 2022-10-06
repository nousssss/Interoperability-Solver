package org.processmining.plugins.workshop.spoilers;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.workshop.WorkshopModel;
import org.processmining.models.workshop.graph.WorkshopGraph;

/**
 * Visualizer plug-in for workshop models. Converts model to graph using some
 * undetermined algorithm and uses PromJGraphVisualizer. As there is only one
 * such algorithm (the WorkshopConversionPlugin that uses the default parameter
 * values), this algorithm will be used.
 * 
 * @author hverbeek
 * 
 */
public class WorkshopModelVisualizer {

	@Plugin(name = "Show Workshop Model", returnLabels = { "Visualization of Workshop Model" }, returnTypes = { JComponent.class }, parameterLabels = { "Workshop Model" }, userAccessible = false)
	@Visualizer
	public JComponent visualize(PluginContext context, WorkshopModel model) throws ConnectionCannotBeObtained {
		/*
		 * Have the framework convert the workshop model to a workshop graph.
		 */
		WorkshopGraph graph = context.tryToFindOrConstructFirstObject(WorkshopGraph.class,
				WorkshopConversionConnection.class, WorkshopConversionConnection.MODEL, model);
		/*
		 * Visualize the resulting workshop graph.
		 */
		return ProMJGraphVisualizer.instance().visualizeGraph(context, graph);
	}
}
