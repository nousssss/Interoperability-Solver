package org.processmining.plugins.pnml.importing;

import java.io.InputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableResetInhibitorNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.Pnml;

@Plugin(name = "Import Configurable Reset/Inhibitor net from PNML file", parameterLabels = { "Filename" }, returnLabels = {
		"Configurable Reset/Inhibitor Net", "Marking" }, returnTypes = { ConfigurableResetInhibitorNet.class, Marking.class })
@UIImportPlugin(description = "PNML Configurable Reset/Inhibitor net files", extensions = { "pnml" })
public class PnmlImportCRINet extends AbstractImportPlugin {

	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("PNML files", "pnml");
	}

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		PnmlImportUtils utils = new PnmlImportUtils();
		Pnml pnml = utils.importPnmlFromStream(context, input, filename, fileSizeInBytes);
		if (pnml == null) {
			/*
			 * No PNML found in file. Fail.
			 */
			return null;
		}
		/*
		 * PNML file has been imported. Now we need to convert the contents to a
		 * Reset/Inhibitor net.
		 */
		PetrinetGraph net = new ConfigurableResetInhibitorNet(pnml.getLabel() + " (imported from " + filename + ")");

		return utils.connectNet(context, pnml, net);
	}

}
