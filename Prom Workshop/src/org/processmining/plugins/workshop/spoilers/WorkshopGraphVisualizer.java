package org.processmining.plugins.workshop.spoilers;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.workshop.graph.WorkshopGraph;

/**
 * Visualizer plug-in for workshop graphs. Uses PromJGraphVisualizer.
 * 
 * @author hverbeek
 * 
 */
public class WorkshopGraphVisualizer {

	/**
	 * Visualizes the given workshop graph.
	 * 
	 * @param context
	 *            The given plug-in context.
	 * @param graph
	 *            The given workshop graph.
	 * @return A JComponent that visualizes the given workshop graph.
	 */
	@Plugin(name = "Show Workshop Graph", returnLabels = { "Visualization of Workshop Graph" }, returnTypes = { JComponent.class }, parameterLabels = { "Workshop Graph" }, userAccessible = true)
	@Visualizer
	public JComponent visualize(PluginContext context, WorkshopGraph graph) {
		return ProMJGraphVisualizer.instance().visualizeGraph(context, graph);
	}
}
