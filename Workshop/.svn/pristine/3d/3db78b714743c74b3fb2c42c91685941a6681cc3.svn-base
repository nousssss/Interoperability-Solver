package org.processmining.plugins.ghzbue;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.ghzbue.graph.GhzGraph;
import org.processmining.models.jgraph.ProMJGraphVisualizer;

/**
 * Visualizer plug-in for Ghz graphs. Uses PromJGraphVisualizer.
 * 
 * @author Ghzzz
 * 
 */
public class GhzGraphVisualizer {
	
	/**
	 * Visualizes the given workshop graph.
	 * 
	 * @param context
	 *            The given plug-in context.
	 * @param graph
	 *            The given workshop graph.
	 * @return A JComponent that visualizes the given workshop graph.
	 */
	@Plugin(name = "Show Ghz Graph", returnLabels = { "Visualization of Ghz Graph" }, returnTypes = { JComponent.class }, parameterLabels = { "Ghz Graph" }, userAccessible = true)
	@Visualizer
	public JComponent visualize(PluginContext context, GhzGraph graph) {
		return ProMJGraphVisualizer.instance().visualizeGraph(context, graph);
	}
}
