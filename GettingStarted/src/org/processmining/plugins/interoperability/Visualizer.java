package org.processmining.plugins.interoperability;

import javax.swing.JComponent;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.jgraph.ProMJGraphVisualizer;


public class Visualizer {

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
	public JComponent visualize(PluginContext context, PetrinetGraph graph) {
		return ProMJGraphVisualizer.instance().visualizeGraph(context, graph);
	}
}