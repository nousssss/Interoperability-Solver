package org.processmining.models.workshop.klunnel;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.workshop.WorkshopModel;
import org.processmining.models.workshop.graph.WorkshopGraph;

public class KlunnelModelVisualizer {
	@Plugin(name = "Show Workshop Model", parameterLabels = { "Workshop Model" },
			returnLabels = { "Visualization of Workshop Model" }, returnTypes = { JComponent.class },
			userAccessible = true)
	@Visualizer
	public JComponent visualize(PluginContext context, WorkshopModel model) throws ConnectionCannotBeObtained {
		WorkshopGraph graph = context.tryToFindOrConstructFirstObject(WorkshopGraph.class,
				KlunnelConversionConnection.class, KlunnelConversionConnection.MODEL, model);
		return ProMJGraphVisualizer.instance().visualizeGraph(context, graph);
	}
}
