package org.processmining.plugins.ghzbue;

import java.io.InputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.ghzbue.GhzModel;

/**
 * Import plug-in for workshop models. Workshop models are imported from CSV
 * files. The first row contains target event class names. The first column
 * contains source event class names. A field contains the cardinality of the
 * direct succession relation from the source event class (see leftmost column)
 * to the target event class (see top row). If no direct succession relation,
 * the field contains 0.
 * 
 * @author hverbeek
 * 
 */
@Plugin(name = "Import Ghz model from GhzM file", parameterLabels = { "Filename" }, returnLabels = { "GhzModel model" }, returnTypes = { GhzModel.class })
@UIImportPlugin(description = "GhzM files", extensions = { "ghzm" })
public class GhzModelImportPlugin extends AbstractImportPlugin {
	
	/**
	 * Gets the file filter for this import plug-in. Description is "GhzM files",
	 * extension is "ghzm".
	 * 
	 * @return
	 */
	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("GhzM files", "ghzm");
	}
	
	/**
	 * Imports the workshop model from the CSV file.
	 */
	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		GhzModel model = new GhzModel(input);
		return model;
	}

}
