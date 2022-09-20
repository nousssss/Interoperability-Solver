package org.processmining.plugins.log.filter;

import java.io.File;
import java.io.IOException;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.log.filter.LogEventUnifier.LogEventUnifierMapping;
import org.processmining.plugins.serialize.XStreamObjectExport;

@Plugin(name = "Export Event Unifier Mapping", returnLabels = {}, returnTypes = {}, parameterLabels = { "Petri net", "File" }, userAccessible = true)
@UIExportPlugin(description = "Event Unifier Mapping (*.unify_xml)", extension = "unify_xml")
public class LogEventUnifier_Export extends XStreamObjectExport {
	
	public String getFileExtension() {
		return "unify_xml";
	}
	
	@PluginVariant(variantLabel = "Export Event Unifier Mapping", requiredParameterLabels = { 0, 1 })
	public void exportPetriNetToPNMLFile(UIPluginContext context, LogEventUnifierMapping map, File file) throws IOException {
		exportXStreamObjectToFile(context, map, file);
	}
}
