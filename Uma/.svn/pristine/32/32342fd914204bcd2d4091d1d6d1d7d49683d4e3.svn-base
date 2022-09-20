package org.processmining.plugins.log.filter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.log.filter.LogEventUnifier.LogEventUnifierMapping;

@Plugin(name = "Update log event unifier mapping",
	parameterLabels = { "log event unifier"}, //
	returnLabels = { "event unifier mapping" },
	returnTypes = { LogEventUnifierMapping.class }, 
	help = "Rename events in the log to match transition labels. The particular use case is a log where the same activity is represented in different cases by different event types. The plugin provides a UI to relate several different event classifiers in the log to the same transition. Upon plugin completion, all events mapped to a transition are renamed.",
	userAccessible = true,
	mostSignificantResult = 1)
public class LogEventUnifier2Plugin {
	
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://www.processmining.org/",
			pack="Uma")
	@PluginVariant(variantLabel = "Update log event unifier mapping", requiredParameterLabels = { 0 })
	public LogEventUnifierMapping filterLog(PluginContext context, LogEventUnifierMapping unifier) {
		
		LogEventUnifierMapping newMap = new LogEventUnifierMapping();
		
		for (String key : unifier.e2eMap.keySet()) {
			String newkey_model = "model "+key;
			String newkey_layout = "layout "+key;
			
			newMap.e2eMap.put(newkey_model, new TreeSet<String>());
			newMap.e2eMap.put(newkey_layout, new TreeSet<String>());
			
			for (String value : unifier.e2eMap.get(key)) {
				newMap.e2eMap.get(newkey_model).add("model "+value);
				newMap.e2eMap.get(newkey_layout).add("layout "+value);
			}
		}
		
		for (String value : unifier.unassigned) {
			newMap.unassigned.add("model "+value);
			newMap.unassigned.add("layout "+value);
		}
		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String timeString = dateFormat.format(date);
		
		context.getFutureResult(0).setLabel("updated mapping @ "+timeString);
		
		return newMap;
	}

}
