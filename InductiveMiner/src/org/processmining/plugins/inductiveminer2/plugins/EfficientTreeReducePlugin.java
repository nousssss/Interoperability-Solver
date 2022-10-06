package org.processmining.plugins.inductiveminer2.plugins;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParameters;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParametersForPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;

public class EfficientTreeReducePlugin {

	@Plugin(name = "Reduce efficient tree language-equivalently for size", returnLabels = {
			"Efficient Tree" }, returnTypes = {
					EfficientTree.class }, parameterLabels = { "Efficient Tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl", uiHelp = "Reduce the tree, while optimising for small Petri nets. Does not provide the structural guarantees mentioned in my thesis.")
	@PluginVariant(variantLabel = "Reduce Efficient Tree Language-equivalently, default", requiredParameterLabels = {
			0 })
	public EfficientTree reduceTree(PluginContext context, EfficientTree tree)
			throws UnknownTreeNodeException, ReductionFailedException, CloneNotSupportedException {
		return reduceTree(tree, new EfficientTreeReduceParametersForPetriNet(false));
	}

	public static EfficientTree reduceTree(EfficientTree tree, EfficientTreeReduceParameters reduceParameters)
			throws UnknownTreeNodeException, ReductionFailedException, CloneNotSupportedException {
		EfficientTree result = tree.clone();
		EfficientTreeReduce.reduce(result, reduceParameters);
		return result;
	}
}
