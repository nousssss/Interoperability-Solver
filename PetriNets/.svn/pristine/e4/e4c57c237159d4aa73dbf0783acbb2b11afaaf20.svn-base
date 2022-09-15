package org.processmining.petrinets.list.plugin;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.petrinets.list.PetriNetList;
import org.processmining.petrinets.list.factory.PetriNetListFactory;

@Plugin(name = "Create Petri Net List", parameterLabels = { "Petri nets" }, returnLabels = {
		"Petri Net List" }, returnTypes = { PetriNetList.class }, categories = {
				PluginCategory.Enhancement }, keywords = { "petri net", "petri nets", "petri net list" })
public class CreatePetriNetListPlugin {

	@UITopiaVariant(affiliation = "Eindhoven University of Technology", author = "S.J. van Zelst", email = "s.j.v.zelst@tue.nl")
	@PluginVariant(requiredParameterLabels = { 0 })
	public static PetriNetList runPlugin(PluginContext context, Petrinet... nets) {
		return runPlugin(nets);
	}

	public static PetriNetList runPlugin(Petrinet... nets) {
		return PetriNetListFactory.createPetriNetList(nets);
	}
}
