package org.processmining.plugins;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(name = "Filter an Event Log based on custom filter", parameterLabels = { "XLog" }, returnLabels = { "XLog" }, returnTypes = { XLog.class })
public class FilterEventLog {
	/**
	 * Filter an Event Log based on custom filter
	 * 
	 * @author J.A.J. van Mourik
	 */
	@UITopiaVariant(uiLabel = "Filter an Event Log based on custom filter", affiliation = UITopiaVariant.EHV, author = "J.A.J. van Mourik", email = "j.a.j.v.mourik@student.tue.nl", pack = "JeroenVanMourik")
	@PluginVariant(variantLabel = "Filter an Event Log based on custom filter", requiredParameterLabels = { 0 })
	public XLog filterLog(PluginContext context, XLog uLog) {
		XFactoryBufferedImpl localFactory = new XFactoryBufferedImpl();
		XConceptExtension conceptExtension = XConceptExtension.instance();

		XLog fLog = localFactory.createLog();

		String logName = "SYNTH_LT60_LE80";

		context.getFutureResult(0).setLabel(logName);
		conceptExtension.assignName(fLog, logName);

		for (XTrace uTrace : uLog){
			//int examCount = 0;
			//boolean traceAdded = false;
			for (XEvent uEvent : uTrace){
				XAttributeMap uEventAttributes = uEvent.getAttributes();
				// FILTER //
				// NOW: Exam attempts //
				//if (uEventAttributes.get("concept:name").toString().equals("Exam")){
				//	examCount++;
				if ((Double.parseDouble(uEventAttributes.get("age").toString()) > 60) && (Double.parseDouble(uEventAttributes.get("age").toString()) <= 80)){
					//if (examCount >= 3){
						fLog.add(uTrace);
					//	traceAdded = true;
					//} else {
					//	break;
					//}
				}
				//}				
				//
				//if (traceAdded){
					break;
				//}
			}
		}

		return fLog;

	}
}