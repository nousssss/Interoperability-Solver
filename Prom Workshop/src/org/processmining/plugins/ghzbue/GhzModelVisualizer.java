package org.processmining.plugins.ghzbue;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.ghzbue.GhzModel;
import org.processmining.models.ghzbue.graph.GhzGraph;
import org.processmining.models.jgraph.ProMJGraphVisualizer;

/**
 * Visualizer plug-in for workshop models. Converts model to graph using some
 * undetermined algorithm and uses PromJGraphVisualizer. As there is only one
 * such algorithm (the WorkshopConversionPlugin that uses the default parameter
 * values), this algorithm will be used.
 * 
 * @author Ghzzz
 * 
 */
public class GhzModelVisualizer {
	
	@Plugin(name = "Show Ghz Model", returnLabels = { "Visualization of Ghz Model" }, returnTypes = { JComponent.class }, parameterLabels = { "Ghz Model" }, userAccessible = false)
	@Visualizer
	public JComponent visualize(PluginContext context, GhzModel model) throws ConnectionCannotBeObtained {
		/*
		 * Have the framework convert the workshop model to a workshop graph.
		 */
		GhzGraph graph = context.tryToFindOrConstructFirstObject(GhzGraph.class,
				GhzConversionConnection.class, GhzConversionConnection.MODEL, model);
		/*
		 * Visualize the resulting workshop graph.
		 */
		return ProMJGraphVisualizer.instance().visualizeGraph(context, graph);
	}
}
