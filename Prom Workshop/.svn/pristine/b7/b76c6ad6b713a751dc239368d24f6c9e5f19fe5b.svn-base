package org.processmining.models.workshop.klunnel;

import java.io.File;
import java.io.IOException;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.workshop.WorkshopModel;

@Plugin(name = "WorM export (Workshop model)", parameterLabels = { "Workshop model", "File" }, returnLabels = {},
		returnTypes = {}, userAccessible = true)
@UIExportPlugin(description = "WorM files", extension = "worm")
public class KlunnelExportPlugin {
	@PluginVariant(variantLabel = "WorM export (Workshop model)", requiredParameterLabels = { 0, 1 })
	public void export(PluginContext context, WorkshopModel model, File file) throws IOException {
		model.exportToFile(file);
	}
}