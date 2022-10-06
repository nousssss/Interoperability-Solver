package org.processmining.plugins;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.EventLogArray;
import org.processmining.models.impl.DivideAndConquerFactory;

@Plugin(name = "Create an event log array", parameterLabels = { "XLog" }, returnLabels = { "EventLogArray" }, returnTypes = { EventLogArray.class })
public class EventLogArrayCreator {
	/**
	 * Create an event model array
	 * 
	 * @author J.A.J. van Mourik
	 */
	@UITopiaVariant(uiLabel = "Create an event log array", affiliation = UITopiaVariant.EHV, author = "J.A.J. van Mourik", email = "j.a.j.v.mourik@student.tue.nl", pack = "JeroenVanMourik")
	@PluginVariant(variantLabel = "Create an event log array", requiredParameterLabels = { 0 })
	public EventLogArray createArray(PluginContext context, XLog... inputLogs) {
		EventLogArray newArray = DivideAndConquerFactory.createEventLogArray();
		for (XLog inputLog : inputLogs) {
			newArray.addLog(inputLog);
		}
		return newArray;
	}
}