package org.processmining.models.semantics.petrinet.providers;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.semantics.petrinet.InhibitorNetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;

@Plugin(name = "Inhibitor Semantics Provider", parameterLabels = {}, returnLabels = { "Inhibitor net Semantics" }, returnTypes = { InhibitorNetSemantics.class }, userAccessible = true)
public class InhibitorNetSemanticsProvider {

	@PluginVariant(variantLabel = "Regular Semantics", requiredParameterLabels = {})
	public InhibitorNetSemantics provideNormal(PluginContext context) {
		context.getFutureResult(0).setLabel("Regular semantics");
		return PetrinetSemanticsFactory.regularInhibitorNetSemantics(InhibitorNet.class);
	}

	@PluginVariant(variantLabel = "Elementary Net Semantics", requiredParameterLabels = {})
	public InhibitorNetSemantics provideElementary(PluginContext context) {
		context.getFutureResult(0).setLabel("Elementary net semantics");
		return PetrinetSemanticsFactory.elementaryInhibitorNetSemantics(InhibitorNet.class);
	}

}
