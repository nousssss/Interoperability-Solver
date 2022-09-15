/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/
package org.processmining.plugins.etconformance;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;


/**
 * Plug-in to perform the ETConformance analysis. ETConformance is a Process
 * Conformance evaluation between an Event Log and a Petri Net. The plug-in
 * returns the values for some metrics (and some other information) and the
 * Minimal Disconformant Traces [MDT] (traces that helps to identify the exact
 * point where the model begins to allow more behavior than the log).
 * 
 * The details of the approach can be seen in the paper "A fresh look at 
 * Precision in Process Conformance" by Jorge Munoz-Gama and Josep Carmona,
 * in Proceedings of "Business Processes Management 2010 (BPM 2010)".
 * 
 * ETConformance was developed by Jorge Munoz-Gama and Josep Carmona from
 * "BarcelonaTech - Universitat Politecnica de Catalunya (UPC)".
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
@Plugin(name = "ETConformance", 
		parameterLabels = { "Log", "PetriNet","Initial Marking", 
							"Log-Petrinet Connection", "Results" }, 
		returnLabels = { "ETConformance Results", "MDT"}, 
		returnTypes = { ETCResults.class,XLog.class })
public class ETCPlugin {
	
	/**
	 * ETConformance variant with only Log and PetriNet.
	 */
	@UITopiaVariant(uiLabel = "Check Conformance using ETConformance", 
			affiliation = "Universitat Politecnica de Catalunya", 
			author = " J.Munoz-Gama & J.Carmona", 
			email ="jmunoz" + (char) 0x40 + "lsi.upc.edu",
			website = "http://www.lsi.upc.edu/~jmunoz",
			pack="ETConformance"
			)
	@PluginVariant(variantLabel = "Log and PetriNet", requiredParameterLabels = { 0, 1 })
	public Object[] doETC(UIPluginContext context, XLog log, Petrinet net) {

		//Get PetriNet Initial Marking
		Marking marking;
		try {
			InitialMarkingConnection connection = context.getConnectionManager().getFirstConnection(
					InitialMarkingConnection.class, context, net);
			marking = connection.getObjectWithRole(InitialMarkingConnection.MARKING);
		} catch (ConnectionCannotBeObtained ex) {
			return cancel(context, "Petri Net lacks initial marking");
		}

		return doETC(context, log, net, marking);
	}

	
	/**
	 * ETConformance variant with Log, PetriNet and Initial Marking.
	 */
	@PluginVariant(variantLabel = "Log,PetriNet and Marking", requiredParameterLabels = { 0, 1, 2 })
	public Object[] doETC(UIPluginContext context, XLog log, Petrinet net, Marking marking) {

		//Get PetriNet - Log Connection
		EvClassLogPetrinetConnection con;
		try {
			con = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, log, net);
		} catch (ConnectionCannotBeObtained ex) {
			return cancel(context, "No connection between Log and Petri Net");
		}

		return doETC(context, log, net, marking, con);
	}

	
	/**
	 * ETConformance variant with Log, PetriNet, Initial Marking and
	 * Log-Petrinet Connection.
	 */
	@PluginVariant(variantLabel = "Log, PetriNet, Marking and Log-Petrinet Connection", requiredParameterLabels = { 0,
			1, 2, 3 })
	public Object[] doETC(UIPluginContext context, XLog log, Petrinet net, Marking marking, EvClassLogPetrinetConnection con) {

		//Show and set the Settings
		ETCResults res = new ETCResults();//Create the result object to store the settings on it
		ETCSettings sett = new ETCSettings(res);
		InteractionResult result = context.showWizard("ETConformance Settings", true, true, sett.initComponents());

		switch (result) {
			case CANCEL :
				return cancel(context, "The user has cancelled ETConformance!");
			case FINISHED :
				sett.setSettings();//Get and store the settings
				return doETC(context, log, net, marking, con, res);
			default :
				return cancel(context, "Problem with the Settings");
		}
	}

	
	/**
	 * ETConformance variant with Log, PetriNet, Initial Marking and
	 * Log-Petrinet Connection and Results.
	 */
	@PluginVariant(variantLabel = "Log, PetriNet, Marking, Log-Petrinet Connection and Settings", requiredParameterLabels = {
			0, 1, 2, 3, 4 })
	public Object[] doETC(PluginContext context, XLog log, Petrinet net, Marking marking, EvClassLogPetrinetConnection con,
			ETCResults res) {
		
		TransEvClassMapping mapping = con.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);
		return doETC(context,log,net,marking,mapping,res);
		
	}
	
	
	/**
	 * ETConformance variant with Log, PetriNet, Initial Marking and
	 * Log-Petrinet Connection and Results.
	 */
	@PluginVariant(variantLabel = "Log, PetriNet, Marking, Log-Petrinet Connection and Settings", requiredParameterLabels = {
			0, 1, 2, 3, 4 })
	public Object[] doETC(PluginContext context, XLog log, Petrinet net, Marking marking, TransEvClassMapping mapping,
			ETCResults res) {
		
		
		//TODO Delete Only to show the version of the plugin
//		System.out.println("ETConformance Plug-in - Version Dummy Aware");
		

		//Get the name of the log and the Model
		String logName = log.getAttributes().get("concept:name").toString();
		String modelName = net.getLabel();
		
		//Set the name of the result objects
//		context.getFutureResult(0).setLabel("(" + modelName + "+" + logName + ")");
//		context.getFutureResult(1).setLabel("MDT:(" + modelName + "+" + logName + ")");
		res.setModelName(modelName);
		res.setLogName(logName);
		
		
		//Perform the analysis and create the Results and the MDT
		XLog MDT = null;
		try{
			MDT = ETCAlgorithm.exec(context, log, net, marking, mapping, res);
			if(!res.isMdt()){
				//Not MDT is not required to be returned (and it is null)
				//context.getFutureResult(1).cancel(true);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			return cancel(context, "[ERROR]" + ex.getMessage());
		}
		
		//Return the results and the MDT
		return new Object[] {res,MDT};

	}

	/**
	 * Cancel the future results, display a error message and return all nulls.
	 * @param context Context of the plug-in.
	 * @param msg Error message.
	 * @return Return an object array of nulls;
	 */
	private Object[] cancel(PluginContext context, String msg) {
		context.log(msg);
		context.getFutureResult(0).cancel(true);
		context.getFutureResult(1).cancel(true);
		return new Object[] { null, null };
	}

}
