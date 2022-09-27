package org.processmining.plugins.interoperability;


import java.util.Collection;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.interoperability.models.LabelledPetrinet;

/**
 * Interoperability solving plug-in that annotates and repaires a process model after
 *   checking it for conformance with an event log.
 * 
 * @author nousssss
 * 
 */
@Plugin
(
	name = "Interoperability Solver", 
	parameterLabels = {"Process Model" , "Event Log"},
    returnLabels = { "Annotated Repaired Model" }, 
    returnTypes = { LabelledPetrinet.class },
    userAccessible = true
 )

public class InteroperabilitySolver {
	
	/**
	 * Variant 1 : The process model and the event are provided.
	 * 
	 * @param context
	 *            The given plug-in context.
	 * @param log
	 *            The given event log.
	 * @param net
	 *            The given process model.
	 * @return an annotated repaired model.
	 *            
	 */
	@UITopiaVariant
	     (
			affiliation = "CDTA", 
			author = "Bachiri Ines", 
			email = "ji_bachiri@esi.dz"
	      )
	@PluginVariant
	      (
	        variantLabel = "Interoperability Solver, given args", 
	        requiredParameterLabels = { 0,1 }
	       )
	public LabelledPetrinet solveGiven(PluginContext context, Petrinet net, XLog log) {
		Collection<InteroperabilitySolverConnection> connections;
		try {
			connections = context.getConnectionManager().getConnections(InteroperabilitySolverConnection.class, context, log);
			for (InteroperabilitySolverConnection connection : connections) {
				if (connection.getObjectWithRole(InteroperabilitySolverConnection.LOG).equals(log)
						&& connection.getObjectWithRole(InteroperabilitySolverConnection.NET).equals(net)) {
					return connection.getObjectWithRole(InteroperabilitySolverConnection.LABELLED);
				}
			}
		} catch (ConnectionCannotBeObtained e) {
		}
		LabelledPetrinet labelled = solver(context, net, log);
		context.addConnection(new InteroperabilitySolverConnection(log,net,labelled));
		return labelled;
	}

	/**
	 * Variant 2 : The process model and the event are to be imported.
	 * 
	 * @param context
	 *            The given plug-in context.
	 * @return an annotated repaired model.           
	 */
	@UITopiaVariant
	     (
			affiliation = "CDTA", 
			author = "Bachiri Ines", 
			email = "ji_bachiri@esi.dz"
	      )
	@PluginVariant
	      (
	        variantLabel = "Interoperability Solver, imported args", 
	        requiredParameterLabels = {}
	       )
	public LabelledPetrinet solveImported(UIPluginContext context) {
		XLog log = null;
		Petrinet net = null;
		SolverDialog dialog = new SolverDialog(log, net);
		InteractionResult result = context.showWizard("Interoperability Solver", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		return solveGiven(context, net, log);
	}

	/*
	 * The actual work.
	 */
	private LabelledPetrinet solver(PluginContext context,Petrinet net, XLog log) {
		return null;
	}
}

/* //import the log 
XLog log = ImportLog.readLogFromFile();
System.out.println("I imported the xes");

 // import the petrinet
Object[] petri = ImportPetriNets.readPNFromFile();
Petrinet net = (Petrinet) petri[0];
Marking initialMarking = (Marking) petri[1];
System.out.println("I imported the pnml");

   // creating a dummy event class (for transitions with no corresponding stuff in map
XEventClass dummyEvClass = new XEventClass("DUMMY", 99999);

XEventClassifier eventClassifier = XLogInfoImpl.STANDARD_CLASSIFIER;

   // map between the log and the petrinet
TransEvClassMapping mapping = Alignment.constructMapping(net, log, dummyEvClass,eventClassifier); 
System.out.println("I did the mapping");

   // creating a connection to be stored
EvClassLogPetrinetConnection evClassLogPetrinetConnection = new
EvClassLogPetrinetConnection("", net, log, eventClassifier, mapping);

  // Do the replay
return Alignment.replayLogGUI(context,net,log,initialMarking,mapping) ; */