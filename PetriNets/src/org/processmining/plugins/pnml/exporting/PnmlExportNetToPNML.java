package org.processmining.plugins.pnml.exporting;

import java.io.File;
import java.io.IOException;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.opennet.OpenNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableResetInhibitorNet;
import org.processmining.plugins.pnml.base.Pnml;

@Plugin(name = "PNML export (Petri net)", level = PluginLevel.PeerReviewed, returnLabels = {}, returnTypes = {}, parameterLabels = { "Petri net",
		"Open net", "Configurable net", "File" }, userAccessible = true)
@UIExportPlugin(description = "PNML files", extension = "pnml")
public class PnmlExportNetToPNML extends PnmlExportNet {

	@PluginVariant(variantLabel = "PNML export (Petri net)", requiredParameterLabels = { 0, 3 })
	public void exportPetriNetToPNMLFile(PluginContext context, Petrinet net, File file) throws IOException {
		exportPetriNetToPNMLOrEPNMLFile(context, net, file, Pnml.PnmlType.PNML);
	}

	@PluginVariant(variantLabel = "PNML export (Open net)", requiredParameterLabels = { 1, 3 })
	public void exportPetriNetToPNMLFile(PluginContext context, OpenNet openNet, File file) throws IOException {
		exportPetriNetToPNMLOrEPNMLFile(context, openNet, file, Pnml.PnmlType.PNML);
	}

	@PluginVariant(variantLabel = "PNML export (Configurable net)", requiredParameterLabels = { 2, 3 })
	public void exportPetriNetToPNMLFile(PluginContext context, ConfigurableResetInhibitorNet configurableNet, File file) throws IOException {
		exportPetriNetToPNMLOrEPNMLFile(context, configurableNet, file, Pnml.PnmlType.PNML);
	}
}
