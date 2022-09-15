package org.processmining.plugins.workshop.sebas;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.workshop.WorkshopModel;

/**
 * Mining plugin that mines a log for a WorkshopModel
 * @author scandel
 */

/**
 * Annotation:
 * @Plugin
 * Plugin name, return type, which parameters there possibly can be, with human readable names, 
 * whether it is usable by a user or not (hidden)
 */

@Plugin(name = "Mine a Workshop Model_test", returnLabels = { "Workshop Model " }, 
	returnTypes = { WorkshopModel.class }, parameterLabels = { "Log", "Parameters" }, 
	userAccessible = true)

public class SebasMiningPlugin {
	
/**
 * Mine using default parameter values.
 * @param context
 * @param log
 * 
 * Annotations:
 * @UITopiaVariant:
 * affiliation, author, contact email
 * @PluginVariant: 
 * Variant Label, specifies which of the parameters are actually required. 0 means, only the 0th (first) parameter, the log, is required
 * ( There are 2 input parameters, a log and model parameters)
 */
	@UITopiaVariant(author = "S.F.T.J. Candel", email = "s.f.t.j.candel@student.tue.nl", affiliation = "TU Eindhoven")
	@PluginVariant(variantLabel = "Mine a Workshop Model, default", requiredParameterLabels = { 0 })
	public WorkshopModel mineDefault(PluginContext context, XLog log){
		return mineParameters(context, log, new SebasModelParameters());
	}
	
	/**
	 * Mine using given parameter values.
	 * @param context
	 * @param log
	 * @param parameters
	 * @return
	 * 
	 * Annotations:
	 * @UITopiaVariant:
	 * affiliation, author, contact email
	 * @PluginVariant: 
	 * Variant Label, required parameters label
	 */
	
	@UITopiaVariant(author = "S.F.T.J. Candel", email = "s.f.t.j.candel@student.tue.nl", affiliation = "TU Eindhoven")
	@PluginVariant(variantLabel = "Mine a Workshop Model, dialog", requiredParameterLabels = { 0 })
	public WorkshopModel mineParameters(PluginContext context, XLog log, SebasModelParameters parameters) {
		// todo -- placeholder code!
		return mine(context, log, parameters);
	}
	
	/**
	 * This class has no annotation, and thus is not part of the plugin.
	 * This class does all the actual work: logic separate from plugin specifics. Could be put in a separate class file!
	 * @return
	 */
	
	private WorkshopModel mine(PluginContext context, XLog log, SebasModelParameters parameters){
		// 05:00 workshop video part 2
		/*
		 * Create event classes based on the given classifier
		 */
		XLogInfo info = XLogInfoFactory.createLogInfo(log, parameters.getClassifier());
		
		/*
		 * Create an empty model
		 */
		WorkshopModel model = new WorkshopModel(info.getEventClasses());
		
		/*
		 * Inform the progress bar when we're done
		 */
		context.getProgress().setMaximum(log.size());
		
		/*
		 * Fill the model based on the direct succession as encountered in the log
		 */
		XEventClass fromEventClass = null, toEventClass = null;
		for (XTrace trace : log) {
			XEvent fromEvent = null;
			for (XEvent toEvent: trace){
				fromEventClass = toEventClass;
				toEventClass = info.getEventClasses().getClassOf(toEvent);
				if (fromEvent != null){
					model.addDirectSuccession(fromEventClass, toEventClass, 1);
				}
				fromEvent = toEvent;
			}
			/*
			 * Advance the progress bar
			 */
			context.getProgress().inc();
		}
		
		/*
		 * Return the model
		 */
		return model;
		
		
	}
	
	
	
}
