package org.processmining.plugins.inductiveminer2.plugins;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.directlyfollowsgraph.DirectlyFollowsGraph;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd2Dot;

public class DfgMsdVisualisationPlugin {
	@Plugin(name = "Directly follows model visualisation", returnLabels = { "Dot visualization" }, returnTypes = {
			JComponent.class }, parameterLabels = { "Directly follows model" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise process tree", requiredParameterLabels = { 0 })
	public DotPanel fancy(PluginContext context, DirectlyFollowsGraph dfgMsd) throws UnknownTreeNodeException {
		return fancy(dfgMsd);
	}

	public static DotPanel fancy(DirectlyFollowsGraph dfgMsd) {
		Dot dot;
		if (dfgMsd.getNumberOfActivities() > 50) {
			dot = new Dot();
			dot.addNode("Graphs with more than 50 nodes are not visualised to prevent hanging...");
		} else {
			dot = DfgMsd2Dot.visualise(dfgMsd);
		}
		return new DotPanel(dot);
	}
}
