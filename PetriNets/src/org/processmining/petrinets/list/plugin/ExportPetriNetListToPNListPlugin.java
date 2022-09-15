package org.processmining.petrinets.list.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.petrinets.list.PetriNetList;
import org.processmining.plugins.pnml.base.Pnml.PnmlType;
import org.processmining.plugins.pnml.exporting.PnmlExportNet;

@Plugin(name = "Petri net List Export (.pnlist)", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"Petri net List", "File" }, userAccessible = true)
@UIExportPlugin(description = ".pnlist files", extension = "pnlist")
public class ExportPetriNetListToPNListPlugin {

	@PluginVariant(variantLabel = "PNML export (Petri net)", requiredParameterLabels = { 0, 1 })
	public static void export(PluginContext context, PetriNetList list, File file) throws IOException {
		String res = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<pnlist>\n";
		for (int i = 0; i < list.size(); i++) {
			res += "\t"
					+ new PnmlExportNet().exportPetriNetToPNMLOrEPNMLString(context, list.get(i), PnmlType.PNML, false);
			res += "\n";
		}
		res += "</pnlist>";
		FileWriter fw = new FileWriter(file, false);
		fw.write(res);
		fw.flush();
		fw.close();
	}

}
