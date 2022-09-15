package org.processmining.models.workshop.klunnel;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.workshop.graph.WorkshopGraph;

public class KlunnelGraphVisualizer {
	@Plugin(name = "Show Workshop Graph", parameterLabels = { "Workshop Graph" },
			returnLabels = { "Visualization of Workshop Graph" }, returnTypes = { JComponent.class },
			userAccessible = true)
	@Visualizer
	public JComponent visualize(PluginContext context, WorkshopGraph graph) {
		return ProMJGraphVisualizer.instance().visualizeGraph(context, graph);
	}
}
