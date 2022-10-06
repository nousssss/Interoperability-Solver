package org.processmining.plugins.inductiveminer2.plugins;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

public class EfficientTree2AcceptingPetriNetPlugin {
	@Plugin(name = "Convert efficient tree to Accepting Petri Net and reduce", level = PluginLevel.PeerReviewed, returnLabels = {
			"Accepting Petri net" }, returnTypes = {
					AcceptingPetriNet.class }, parameterLabels = { "Efficient Tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Convert Process Tree to Petri Net, default", requiredParameterLabels = { 0 })
	public AcceptingPetriNet convertAndReduce(PluginContext context, EfficientTree tree) {
		return EfficientTree2AcceptingPetriNet.convert(tree);
	}
}
