package org.processmining.plugins.ghzbue;

import java.io.File;
import java.io.IOException;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.ghzbue.GhzModel;

/**
 * Export plug-in for workshop models. Workshop models are exported to CSV
 * files. The first row contains target event class names. The first column
 * contains source event class names. A field contains the cardinality of the
 * direct succession relation from the source event class (see leftmost column)
 * to the target event class (see top row). If no direct succession relation,
 * the field contains 0.
 * 
 * @author MGHz
 * 
 */

@Plugin(name = "GhzM export (Ghz Model)", returnLabels = {}, returnTypes = {}, parameterLabels = {
		"GhzModel", "File" }, userAccessible = true)
@UIExportPlugin(description = "GhzM files", extension = "ghzm" )
public class GhzModelExportPlugin {
	
	/**
	 * Exports the given GHZ model to the given file.
	 * 
	 * @param context
	 *            The given plug-in context.
	 * @param model
	 *            The given Ghz model.
	 * @param file
	 *            The given file.
	 * @throws IOException
	 */
	@PluginVariant(variantLabel = "GhzM export (Ghz Model)", requiredParameterLabels = {0, 1})
	public void export(PluginContext context, GhzModel model, File file) throws IOException {
		model.exportToFile(file);
	}

}
