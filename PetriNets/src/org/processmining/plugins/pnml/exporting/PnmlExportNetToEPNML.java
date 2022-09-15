package org.processmining.plugins.pnml.exporting;

import java.io.File;
import java.io.IOException;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.opennet.OpenNet;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableResetInhibitorNet;
import org.processmining.plugins.pnml.base.Pnml;

@Plugin(name = "EPNML export (Petri net)", returnLabels = {}, returnTypes = {}, parameterLabels = { "Petri net",
		"Open net", "Reset net" ,"Configurable net","Reset Inhibitor net","Inhibitor net","File" }, userAccessible = true)
@UIExportPlugin(description = "EPNML files", extension = "pnml")
public class PnmlExportNetToEPNML extends PnmlExportNet {

	@PluginVariant(variantLabel = "EPNML export (Petri net)", requiredParameterLabels = { 0, 6 })
	public void exportPetriNetToEPNMLFile(PluginContext context, Petrinet net, File file) throws IOException {
		exportPetriNetToPNMLOrEPNMLFile(context, net, file, Pnml.PnmlType.EPNML);
	}

	@PluginVariant(variantLabel = "EPNML export (Open net)", requiredParameterLabels = { 1, 6 })
	public void exportPetriNetToEPNMLFile(PluginContext context, OpenNet openNet, File file) throws IOException {
		exportPetriNetToPNMLOrEPNMLFile(context, openNet, file, Pnml.PnmlType.EPNML);
	}

	@PluginVariant(variantLabel = "EPNML export (Reset net)", requiredParameterLabels = { 2, 6 })
	public void exportPetriNetToPNMLFile(PluginContext context, ResetNet resetNet, File file) throws IOException {
		exportPetriNetToPNMLOrEPNMLFile(context, resetNet, file, Pnml.PnmlType.EPNML);
	}
	
	@PluginVariant(variantLabel = "PNML export (Configurable net)", requiredParameterLabels = { 3, 6 })
	public void exportPetriNetToPNMLFile(PluginContext context, ConfigurableResetInhibitorNet configurableNet, File file) throws IOException {
		exportPetriNetToPNMLOrEPNMLFile(context, configurableNet, file, Pnml.PnmlType.EPNML);
	}
	
	@PluginVariant(variantLabel = "PNML export (Reset Inhibitor net)", requiredParameterLabels = { 4, 6 })
	public void exportPetriNetToPNMLFile(PluginContext context, ResetInhibitorNet resetInhibitorNet, File file) throws IOException {
		exportPetriNetToPNMLOrEPNMLFile(context, resetInhibitorNet, file, Pnml.PnmlType.EPNML);
	}

	@PluginVariant(variantLabel = "PNML export (Inhibitor net)", requiredParameterLabels = { 5, 6 })
	public void exportPetriNetToPNMLFile(PluginContext context, InhibitorNet inhibitorNet, File file) throws IOException {
		exportPetriNetToPNMLOrEPNMLFile(context, inhibitorNet, file, Pnml.PnmlType.EPNML);
	}
}
