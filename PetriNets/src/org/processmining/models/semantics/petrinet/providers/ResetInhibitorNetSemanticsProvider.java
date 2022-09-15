package org.processmining.models.semantics.petrinet.providers;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.semantics.petrinet.ResetInhibitorNetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;

@Plugin(name = "ResetInhibitor Net Semantics Provider", parameterLabels = {}, returnLabels = { "Reset-Inhibitor net Semantics" }, returnTypes = { ResetInhibitorNetSemantics.class }, userAccessible = true)
public class ResetInhibitorNetSemanticsProvider {

	@PluginVariant(variantLabel = "Regular Semantics", requiredParameterLabels = {})
	public ResetInhibitorNetSemantics provideNormal(PluginContext context) {
		context.getFutureResult(0).setLabel("Regular semantics");
		return PetrinetSemanticsFactory.regularResetInhibitorNetSemantics(ResetInhibitorNet.class);
	}

	@PluginVariant(variantLabel = "Elementary Net Semantics", requiredParameterLabels = {})
	public ResetInhibitorNetSemantics provideElementary(PluginContext context) {
		context.getFutureResult(0).setLabel("Elementary net semantics");
		return PetrinetSemanticsFactory.elementaryResetInhibitorNetSemantics(ResetInhibitorNet.class);
	}

}
