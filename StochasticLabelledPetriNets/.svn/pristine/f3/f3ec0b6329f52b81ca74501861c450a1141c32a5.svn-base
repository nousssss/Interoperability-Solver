package org.processmining.stochasticlabelledpetrinets.plugins;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSimpleWeights;

public class StochasticLabelledPetriNetSimpleWeightsVisualisationPlugin
		extends StochasticLabelledPetriNetVisualisationPlugin<StochasticLabelledPetriNetSimpleWeights> {
	@Plugin(name = "Stochastic labelled Petri net (simple weights) visualisation", returnLabels = {
			"Dot visualization" }, returnTypes = { JComponent.class }, parameterLabels = {
					"stochastic labelled Petri net", "canceller" }, userAccessible = true, level = PluginLevel.Regular)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Stochastic labelled Petri net visualisation", requiredParameterLabels = { 0, 1 })
	public JComponent visualise(final PluginContext context, StochasticLabelledPetriNetSimpleWeights net,
			ProMCanceller canceller) {
		return visualise(net);
	}

	public void decoratePlace(StochasticLabelledPetriNetSimpleWeights net, int place, DotNode dotNode) {

	}

	public void decorateTransition(StochasticLabelledPetriNetSimpleWeights net, int transition, DotNode dotNode) {
		dotNode.setOption("xlabel", net.getTransitionWeight(transition) + "");
	}

}