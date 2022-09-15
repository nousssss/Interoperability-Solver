package org.processmining.models.workshop.klunnel;

import java.io.InputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.workshop.WorkshopModel;

@Plugin(name = "Import Workshop model from WorM file", parameterLabels = { "Filename" },
		returnLabels = { "Workshop model" }, returnTypes = { WorkshopModel.class })
@UIImportPlugin(description = "WorM files", extensions = { "worm" })
public class KlunnelImportPlugin extends AbstractImportPlugin {

	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("WorM files", "worm");
	}

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		WorkshopModel model = new WorkshopModel(input);
		return model;
	}

}
