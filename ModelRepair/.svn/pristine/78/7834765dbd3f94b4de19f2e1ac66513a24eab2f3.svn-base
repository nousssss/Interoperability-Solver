package org.processmining.modelrepair.plugins.align;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.modelrepair.plugins.align.PNLogReplayer.ReplayParams;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

@Plugin(name = "Align Log And Model for Repair (find loops)",
	parameterLabels = { "a Log", "a Petri net"}, //
	returnLabels = { "Alignment" },
	returnTypes = { PNRepResult.class }, 
	help = "Create an alignment between model and log that aides in identifying loops for model repair..",
	userAccessible = true,
	mostSignificantResult = 1)
public class Uma_AlignForLoopDiscovery_Plugin {
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://service-technology.org/uma",
			pack="ModelRepair")
	@PluginVariant(variantLabel = "Align Log And Model for Repair (find loops)", requiredParameterLabels = { 0, 1 })
	public PNRepResult getGlobalAlignment(UIPluginContext context, XLog log, PetrinetGraph net) {
		
		Marking initMarking;
		try {
			initMarking = context.tryToFindOrConstructFirstObject(Marking.class, InitialMarkingConnection.class, InitialMarkingConnection.MARKING, net);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "No initial marking found.");
		}
		
		// replay log on model (or obtain existing replay result)
		TransEvClassMapping map;
		try {
			EvClassLogPetrinetConnection conn = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net,	log);
			map = (TransEvClassMapping) conn.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);
		} catch (ConnectionCannotBeObtained e1) {
			XEventClassifier classifier = PNLogReplayer.getDefaultClassifier(log);
			map = PNLogReplayer.getEventClassMapping(context, net, log, classifier);
		}
		

		PNRepResult result = getLoopAlignment(context, log, net, initMarking, map, true, 0);
		if (result == null) {
			return cancel(context, "Could not replay log on given net.");
		} else {
			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			String timeString = dateFormat.format(new Date());
			context.getFutureResult(0).setLabel("alignment for loop repair of "+net.getLabel() +" (@"+timeString+")");
			return result;
		}
	}
	
	public static PNRepResult getLoopAlignment(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, TransEvClassMapping map, boolean createConnection, int model_move_cost) {
		Marking m_final = (Marking)PNLogReplayer.constructFinalMarking(context, net)[1]; 
		return getLoopAlignment(context, log, net, initMarking, m_final, map, createConnection, model_move_cost);
	}
	
	public static PNRepResult getLoopAlignment(PluginContext context, XLog log, PetrinetGraph net, Marking initMarking, Marking m_final, TransEvClassMapping map, boolean createConnection, int model_move_cost) {
		PNLogReplayer.ReplayParams params = getReplayerParameters(context, net, initMarking, m_final, log, map.getEventClassifier(), model_move_cost);
		return getLoopAlignment(context, log, net, params, map, createConnection);
	}
	
	public static PNRepResult getLoopAlignment(PluginContext context, XLog log, PetrinetGraph net, PNLogReplayer.ReplayParams params, TransEvClassMapping map, boolean createConnection) {
		PNRepResult result = PNLogReplayer.callReplayer(context, net, log, map, params);
		if (result != null && createConnection) {
			context.addConnection(new PNRepResultAllRequiredParamConnection(
					"Connection between replay result, " + XConceptExtension.instance().extractName(log)
							+ ", and " + net.getLabel(), net, log, map, params.selectedAlg, params.parameters, result));
		}
		return result;
	}

	public static ReplayParams getReplayerParameters(PluginContext context, PetrinetGraph net, Marking m_initial, Marking m_final, XLog log, XEventClassifier classifier, int model_move_cost) {
		PNLogReplayer.ReplayParams params = PNLogReplayer.getReplayerParameters(context, net, m_initial, m_final, log, classifier);
		for (Transition t : params.parameters.getMapTrans2Cost().keySet()) {
			if (params.parameters.getMapTrans2Cost().get(t) != 0) params.parameters.getMapTrans2Cost().put(t, model_move_cost);
		}
		return params;
	}

	protected static PNRepResult cancel(PluginContext context, String message) {
		System.out.println("[ModelRepair/loop align]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
}
