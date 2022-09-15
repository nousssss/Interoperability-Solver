/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package org.processmining.plugins.connectionfactories.logpetrinet;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.connections.logmodel.LogPetrinetConnection;
import org.processmining.connections.logmodel.LogPetrinetConnectionImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;

/**
 * Factory to create a Log / PetriNet connection, mapping Transitions and
 * EventClasses.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
@ConnectionObjectFactory
@Plugin(name = "Log/Petrinet connection factory", parameterLabels = { "Log", "Petrinet" }, returnLabels = "connection", returnTypes = LogPetrinetConnection.class, userAccessible = false)
public class LogPetrinetConnectionFactory {

	@PluginVariant(variantLabel = "Petrinet", requiredParameterLabels = { 0, 1 })
	public LogPetrinetConnection connect(UIPluginContext context, XLog log, PetrinetGraph net) {

		//Get EventClasses of the Events in the Log

		//Build and show the UI to make the mapping
		// list possible classifiers
		List<XEventClassifier> classList = new ArrayList<XEventClassifier>(log.getClassifiers());
		// add default classifiers
		if (!classList.contains(XLogInfoImpl.RESOURCE_CLASSIFIER)) {
			classList.add(0, XLogInfoImpl.RESOURCE_CLASSIFIER);
		}
		if (!classList.contains(XLogInfoImpl.NAME_CLASSIFIER)) {
			classList.add(0, XLogInfoImpl.NAME_CLASSIFIER);
		}
		if (!classList.contains(XLogInfoImpl.STANDARD_CLASSIFIER)) {
			classList.add(0, XLogInfoImpl.STANDARD_CLASSIFIER);
		}

		Object[] availableEventClass = classList.toArray(new Object[classList.size()]);
		
		LogPetrinetConnectionFactoryUI ui = new LogPetrinetConnectionFactoryUI(log, net, availableEventClass);
		InteractionResult result = context.showWizard("Mapping Petrinet - Log", true, true, ui);

		//Create the connection or not according to the button pressed in the UI
		LogPetrinetConnection con = null;
		if (result == InteractionResult.FINISHED) {
			con = new LogPetrinetConnectionImpl(log, ui.getClasses(), net, ui.getMap());
		}

		//Return the connection (or null if the connection hasn't been created)
		return con;
	}

}
