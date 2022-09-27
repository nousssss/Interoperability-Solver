package org.processmining.plugins.interoperability;


import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.jgraph.ProMJGraphVisualizer;


public class LabelledPetrinetVis {


		/**
		 * Visualizes the given workshop graph.
		 * 
		 * @param context
		 *            The given plug-in context.
		 * @param graph
		 *            The given workshop graph.
		 * @return A JComponent that visualizes the given workshop graph.
		 */
		@Plugin(name = "Show Labelled petrinet", 
				returnLabels = { "Visualization of labelled petrinet" }, 
				returnTypes = { JComponent.class }, 
				parameterLabels = { "Labelled Petrinet" }, 
				userAccessible = true)
		
		@Visualizer
		public JComponent visualize(PluginContext context, LabelledPetrinetGraph graph) {
			return ProMJGraphVisualizer.instance().visualizeGraph(context, graph);
		}
	}
