package org.processmining.plugins.pnml.importing;

import java.io.InputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.opennet.OpenNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.Pnml;

@Plugin(name = "Import Open net from PNML file", parameterLabels = { "Filename" }, returnLabels = { "Open Net",
		"Marking" }, returnTypes = { OpenNet.class, Marking.class })
@UIImportPlugin(description = "PNML Open net files", extensions = { "pnml" })
public class PnmlImportOpenNet extends AbstractImportPlugin {

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
		 * regular Petri net.
		 */
		OpenNet openNet = new OpenNet(pnml.getLabel() + " (imported from " + filename + ")");

		return utils.connectOpenNet(context, pnml, openNet);
	}
}
