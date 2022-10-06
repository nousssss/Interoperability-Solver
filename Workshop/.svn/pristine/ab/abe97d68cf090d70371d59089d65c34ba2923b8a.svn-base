package org.processmining.plugins.workshop.spoilers;

import java.io.InputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.workshop.WorkshopModel;

/**
 * Impor tplug-in for workshop models. Workshop models are imported from CSV
 * files. The first row contains target event class names. The first column
 * contains source event class names. A field contains the cardinality of the
 * direct succession relation from the source event class (see leftmost column)
 * to the target event class (see top row). If no direct succession relation,
 * the field contains 0.
 * 
 * @author hverbeek
 * 
 */
@Plugin(name = "Import Workshop model from WorM file", parameterLabels = { "Filename" }, returnLabels = { "Workshop model" }, returnTypes = { WorkshopModel.class })
@UIImportPlugin(description = "WorM files", extensions = { "worm" })
public class WorkshopImportPlugin extends AbstractImportPlugin {

	/**
	 * Gets the file filter for this import plug-in. Description is "WorM files",
	 * extension is "worm".
	 * 
	 * @return
	 */
	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("WorM files", "worm");
	}

	/**
	 * Imports the workshop model from the CSV file.
	 */
	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		WorkshopModel model = new WorkshopModel(input);
		return model;
	}

}
