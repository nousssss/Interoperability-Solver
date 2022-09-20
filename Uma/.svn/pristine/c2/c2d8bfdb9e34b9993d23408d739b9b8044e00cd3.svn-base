package org.processmining.plugins.log.filter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.log.filter.LogEventUnifier.LogEventUnifierMapping;

@Plugin(name = "Unify Events in Log",
	parameterLabels = { "a log", "Petri net", "event unifier mapping", "log event unifier"}, //
	returnLabels = { "filtered log", "event unifier mapping" },
	returnTypes = { XLog.class, LogEventUnifierMapping.class }, 
	help = "Rename events in the log to match transition labels. The particular use case is a log where the same activity is represented in different cases by different event types. The plugin provides a UI to relate several different event classifiers in the log to the same transition. Upon plugin completion, all events mapped to a transition are renamed.",
	userAccessible = true,
	mostSignificantResult = 1)
public class LogEventUnifierPlugin {
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://www.processmining.org/",
			pack="Uma")
	@PluginVariant(variantLabel = "Unify Events in Log", requiredParameterLabels = { 0, 1 })
	public Object[] filterLog(UIPluginContext context, XLog log, Petrinet net) {
		
		LogEventUnifier unifier = new LogEventUnifier(LogEventUnifier.getNamesFromModel(net), log);
		LogEventUnifier_UI ui = new LogEventUnifier_UI(unifier);
		if (ui.setParameters(context, unifier) != InteractionResult.CANCEL)
			return filterLog(context, log, unifier);
		else
			return cancel(context, "Canceled by user.");
				
	}
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://www.processmining.org/",
			pack="Uma")
	@PluginVariant(variantLabel = "Unify Events in Log", requiredParameterLabels = { 0, 2 })
	public Object[] filterLog(UIPluginContext context, XLog log, LogEventUnifierMapping mapping) {
		
		LogEventUnifier unifier = new LogEventUnifier(mapping);
		LogEventUnifier_UI ui = new LogEventUnifier_UI(unifier);
		if (ui.setParameters(context, unifier) != InteractionResult.CANCEL)
			return filterLog(context, log, unifier);
		else
			return cancel(context, "Canceled by user.");
				
	}

	@PluginVariant(variantLabel = "Unify Events in Log", requiredParameterLabels = { 0, 3 })
	public Object[] filterLog(PluginContext context, XLog log, LogEventUnifier unifier) {

		XFactory f = XFactoryRegistry.instance().currentDefault();
		
		// create new log, copy original attributes
		XLog filtered = f.createLog(log.getAttributes());
		
		for (XTrace t : log) {
			XTrace tNew = f.createTrace(t.getAttributes());
			for (XEvent e : t) {
				XEvent eNew = f.createEvent(e.getAttributes());
				
				String oldName = ((XAttributeLiteral)eNew.getAttributes().get("concept:name")).getValue();
				oldName = oldName.replace('\u00E9', '\'');
				String newName = unifier.getEventClassification().get(oldName.toLowerCase());
				eNew.getAttributes().put("concept:name",
						   f.createAttributeLiteral("concept:name", newName,
						   XConceptExtension.instance()));
				tNew.add(eNew);
			}
			
			filtered.add(tNew);
		}
		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String timeString = dateFormat.format(date);
		
		String logName = log.getAttributes().get("concept:name").toString()+" (unified @ "+timeString+")";
		context.getFutureResult(0).setLabel(logName);
		context.getFutureResult(1).setLabel("event mapping for "+logName);
		
		return new Object[] { filtered, unifier.mapping };
	}

	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[AttributeFilter]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		context.getFutureResult(1).cancel(true);
		return null;
	}
}
