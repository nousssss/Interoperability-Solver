package org.processmining.models.semantics.petrinet.providers;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;

@Plugin(name = "Petrinet Semantics Provider", parameterLabels = {}, returnLabels = { "Petrinet Semantics" }, returnTypes = { PetrinetSemantics.class }, userAccessible = true)
public class PetrinetSemanticsProvider {

	@PluginVariant(variantLabel = "Elementary Net Semantics", requiredParameterLabels = {})
	public PetrinetSemantics provideElementary(PluginContext context) {
		context.getFutureResult(0).setLabel("Elementary net semantics");
		return PetrinetSemanticsFactory.elementaryPetrinetSemantics(Petrinet.class);
	}

	@PluginVariant(variantLabel = "Regular Semantics", requiredParameterLabels = {})
	public PetrinetSemantics provideNormal(PluginContext context) {
		context.getFutureResult(0).setLabel("Regular semantics");
		return PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);
	}

}
