package org.processmining.models.semantics.petrinet.providers;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.semantics.petrinet.ResetNetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;

@Plugin(name = "ResetNet Semantics Provider", parameterLabels = {}, returnLabels = { "Reset net Semantics" }, returnTypes = { ResetNetSemantics.class }, userAccessible = true)
public class ResetNetSemanticsProvider {

	@PluginVariant(variantLabel = "Regular Semantics", requiredParameterLabels = {})
	public ResetNetSemantics provideNormal(PluginContext context) {
		context.getFutureResult(0).setLabel("Regular semantics");
		return PetrinetSemanticsFactory.regularResetNetSemantics(ResetNet.class);
	}

	@PluginVariant(variantLabel = "Elementary Net Semantics", requiredParameterLabels = {})
	public ResetNetSemantics provideElementary(PluginContext context) {
		context.getFutureResult(0).setLabel("Elementary net semantics");
		return PetrinetSemanticsFactory.elementaryResetNetSemantics(ResetNet.class);
	}

}
