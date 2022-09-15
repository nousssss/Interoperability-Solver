package org.processmining.plugins.workshop.nouss;

import java.util.Collection;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.workshop.WorkshopModel;

// The model shall later become a petri net, so we will basically use this code to mine a petrinet from an event log.


@Plugin (
	  name = "Mining Plugin nouss",
	  parameterLabels = {"Event log", "Parameters"} ,
	  returnLabels = { "Model"} ,
	  returnTypes = {WorkshopModel.class} ,
	  userAccessible = true,
	  help = ""
		
		)

public class MiningPlugin {
	
	@UITopiaVariant(
			affiliation = "CDTA Algiers", 
			author = "Bachiri Inas", 
			email = "ji_bachiri@esi.dz"
			)
	@PluginVariant(
			variantLabel = "Mine a Workshop Model, default", 
			requiredParameterLabels = { 0 }
			)
	
	public WorkshopModel mineDefault(PluginContext context, XLog log) {
		return mineParameters(context,log,new MiningParameters());
	}
	
	@UITopiaVariant(
			affiliation = "CDTA Algiers", 
			author = "Bachiri Inas", 
			email = "ji_bachiri@esi.dz"
			)
	@PluginVariant(
			variantLabel = "Mine a Workshop Model, parameters", 
			requiredParameterLabels = { 0,1 }
			)
	
	public WorkshopModel mineParameters(PluginContext context, XLog log, MiningParameters param) {
		
		Collection<MiningConnection> connections; 
		try {
			// get all connections
			connections = context.getConnectionManager().getConnections(MiningConnection.class, context, log);
			//check if we already have the model mined before
			for (MiningConnection connection : connections) {
				if (connection.getObjectWithRole(MiningConnection.LOG).equals(log)
						&& connection.getParameters().equals(param)) {
					// return the model associated with our log and parameters (lginah mined deja)
					return connection.getObjectWithRole(MiningConnection.MODEL);
				}
			}
		} catch (ConnectionCannotBeObtained e) {
			// just ignore it
		}
		
		// we haven't mined it before, let's do it now
		WorkshopModel model = mine(context, log, param);
		// Add the connection between our log,model and parameters
		context.addConnection(new MiningConnection(log, model, param));
		return model;
	}
	
	@UITopiaVariant(
			affiliation = "CDTA Algiers", 
			author = "Bachiri Inas", 
			email = "ji_bachiri@esi.dz"
			)
	@PluginVariant(
			variantLabel = "Mine a Workshop Model, dialog", 
			requiredParameterLabels = { 0 }
			)
	
	public WorkshopModel mineDefault(UIPluginContext context, XLog log) {
		MiningParameters param = new MiningParameters();
		MiningDialog dialog = new MiningDialog(log,param);
		InteractionResult result = context.showWizard("My mining", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		return mineParameters(context,log,param);
	}
	
	
	private WorkshopModel mine(PluginContext context, XLog log, MiningParameters param) {
		// Obtenir des infos pour un log, étant donné le classificateur :
		XLogInfo info = XLogInfoFactory.createLogInfo(log, param.getClassifier());
		
		// Create an empty model
		WorkshopModel model = new WorkshopModel(info.getEventClasses());
		
		// Inform the progress bar when we're done.
		context.getProgress().setMaximum(log.size());
		
        //Fill the model based on the direct succession as encountered in the log
		XEventClass fromEventClass = null, toEventClass = null;
		for (XTrace trace : log) {
			
			XEvent fromEvent = null;
			
			for (XEvent toEvent : trace) {
				fromEventClass = toEventClass;
				toEventClass = info.getEventClasses().getClassOf(toEvent);
				if (fromEvent != null) {
					model.addDirectSuccession(fromEventClass, toEventClass, 1);
				}
				fromEvent = toEvent;
			}

			// Advance the progress bar.
			context.getProgress().inc();
		}

		// Return the model.

		return model;
	}
		
}
       

