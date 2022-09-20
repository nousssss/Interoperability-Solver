package org.processmining.plugins.log.filter;

import java.util.HashMap;
import java.util.Map;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.plugins.log.filter.LogEventUnifier.LogEventUnifierMapping;
import org.processmining.plugins.serialize.XStreamObjectImport;

@Plugin(name = "Import Event Unifier Mapping",
	parameterLabels = { "Filename" },
	returnLabels = { "Event Unifier Mapping" },
	returnTypes = { LogEventUnifierMapping.class })
@UIImportPlugin(description = "Event Unifier Mapping (*.unify_xml)", extensions = { "unify_xml" })
public class LogEventUnifier_Import extends XStreamObjectImport {

	protected Map<String, Class<? extends Object>> getAliasMap() {
		Map<String, Class<? extends Object>> map = new HashMap<String, Class<? extends Object>>();
		map.put("unifier", LogEventUnifierMapping.class);
		return map;
	}
	
}
