package org.processmining.modelrepair.plugins.align;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.modelrepair.plugins.uma.UmaPromUtil;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import nl.tue.astar.AStarException;

@Plugin(name = "Align Log And Model for Repair (global costs)",
	parameterLabels = { "a Log", "a Petri net"}, //
	returnLabels = { "Alignment" },
	returnTypes = { PNRepResult.class }, 
	help = "Create an alignment between model and log that minimizes the number of deviating transitions and event classes (while the total number of deviations themselves, e.g., deviating events or transition occurrences may increase). Such alignments give better results in model repair when adding sub-processes.",
	userAccessible = true,
	mostSignificantResult = 1)
public class Uma_AlignForGlobalRepair_Plugin {
	
	private static boolean _debug = false;
	
	// take log and net as input and guess initial marking
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://service-technology.org/uma",
			pack="ModelRepair")
	@PluginVariant(variantLabel = "Align Log And Model for Repair (global costs)", requiredParameterLabels = { 0, 1 })
	public PNRepResult getGlobalAlignment(UIPluginContext context, XLog log, PetrinetGraph net) {

		// replay log on model (or obtain existing replay result)
		try {
			PNLogReplayer replayer = new PNLogReplayer();
			PNRepResult localResult = replayer.replayLog(context, net, log);

			IPNReplayAlgorithm selectedAlg = replayer.getUsedAlgorithm();
			IPNReplayParameter parameters = replayer.getUsedAlgParameters();
			TransEvClassMapping mapping = replayer.getUsedMapping();

			if (parameters instanceof CostBasedCompleteParam) {
				return getGlobalAlignment(context, log, net, localResult, selectedAlg, (CostBasedCompleteParam)parameters, mapping, 10);
			} else {
				return cancel(context, "Chosen replayer does not allow for changing costs.");
			}
			
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "Could not replay the log on the model. No replayer found.");
		}
	}
	
	public PNRepResult getGlobalAlignment(PluginContext context, XLog log, PetrinetGraph net,
			PNRepResult result, 
			IPNReplayAlgorithm selectedAlg, CostBasedCompleteParam parameters, TransEvClassMapping mapping,
			int maxIterations)
	{
		PNRepResult localResult = result;
		
		// replay log on model (or obtain existing replay result)
		PNLogReplayer replayer = new PNLogReplayer();

		Map<Object, Integer> frequencies = getMoveFrequencies(localResult);
		Map<Object, Integer> originalFrequencies = frequencies;
		Map<Object, Integer> lastFrequencies = new HashMap<Object, Integer>();
		if (_debug) _debug_printFrequencies(frequencies);
		
		int iterations = 0;
		do {
			iterations++;
			context.log("Adjusting costs for alignment ("+iterations+")");

			Map<XEventClass, Integer> costLogMove =  parameters.getMapEvClass2Cost();
			Map<Transition, Integer> costModelMove = parameters.getMapTrans2Cost();
			updateCosts(frequencies, costLogMove, costModelMove);
			
			lastFrequencies = frequencies;

			try {
				localResult = replayer.replayLog(context, net, log, mapping, selectedAlg, parameters);
			} catch (AStarException e) {
				UmaPromUtil.printMessage(context, "Uma", "could not replay log on model "+e);
				return null;
			}
			frequencies = getMoveFrequencies(localResult);
			_debug_printFrequencies(frequencies);
			
		} while (!lastFrequencies.equals(frequencies) && iterations < maxIterations);
		context.log("Found stable state");
		
		System.out.println("----------- original frequencies -------------");
		_debug_printFrequencies(originalFrequencies);
		System.out.println("----------- improved frequencies -------------");
		_debug_printFrequencies(frequencies);
		
		return localResult;
	}
	
	private static Map<Object, Integer> getMoveFrequencies(PNRepResult result) {
		Map<Object, Integer> frequencies = new HashMap<Object, Integer>();
		
		// create traces in the aligned log (each trace is one trace class from the replay)
		for (SyncReplayResult res : result) {
			//System.out.println("----------");
			for (int i=0; i<res.getNodeInstance().size(); i++) {
				if (   res.getStepTypes().get(i) == org.processmining.plugins.petrinet.replayresult.StepTypes.L
					|| res.getStepTypes().get(i) == org.processmining.plugins.petrinet.replayresult.StepTypes.MREAL)
				{
					Object move = res.getNodeInstance().get(i);
					if (!frequencies.containsKey(move)) frequencies.put(move, 0);
					frequencies.put(move, frequencies.get(move)+1);
					//System.out.println("set "+res.getNodeInstance().get(i)+" "+res.getStepTypes().get(i));
				} else {
					//System.out.println("ignore "+res.getNodeInstance().get(i)+" "+res.getStepTypes().get(i));
				}
				
			}
		}
		return frequencies;
	}
	
	private static void _debug_printFrequencies(Map<Object, Integer> frequencies) {
		for (Map.Entry<Object, Integer> f : frequencies.entrySet()) {
			System.out.println(f.getKey().getClass()+" "+f);
		}
	}
	
	private void updateCosts(Map<Object, Integer> frequencies, Map<XEventClass, Integer> costLogMove, Map<Transition, Integer> costModelMove) {

		int mostEfficientLogMove = 0;
		int mostEfficientModelMove = 0;
		
		for (Map.Entry<Object, Integer> f : frequencies.entrySet()) {
			Object move = f.getKey();
			if (move instanceof XEventClass) {
				if (f.getValue() > mostEfficientLogMove) mostEfficientLogMove = f.getValue();
			}
			if (move instanceof Transition) {
				if (f.getValue() > mostEfficientModelMove) mostEfficientModelMove = f.getValue();
			}
		}
		
		int mostEfficientChange = Math.max(mostEfficientLogMove, mostEfficientModelMove);
		
		for (XEventClass eventClass : costLogMove.keySet()) {
			double scaledValue;
			
			if (frequencies.containsKey(eventClass)) {
				scaledValue = costLogMove.get(eventClass) * (double)mostEfficientChange / (double)frequencies.get(eventClass);
			} else {
				scaledValue = costLogMove.get(eventClass) * (double)mostEfficientChange;
			}
			if (costLogMove.get(eventClass) > 0 && scaledValue < 1) scaledValue = 1.0;
				 
			if (_debug) System.out.println(eventClass+" "+costLogMove.get(eventClass)+" -> "+(int)scaledValue);
			costLogMove.put(eventClass, (int)scaledValue);
		}
		
		for (Transition transition : costModelMove.keySet()) {
			double scaledValue;
			if (frequencies.containsKey(transition)) {
				scaledValue = costModelMove.get(transition) * (double)mostEfficientChange / (double)frequencies.get(transition);
			} else {
				scaledValue = costModelMove.get(transition) * (double)mostEfficientChange;
			}
			if (costModelMove.get(transition) > 0 && scaledValue < 1) scaledValue = 1.0;
			
			if (_debug) System.out.println(transition+" "+costModelMove.get(transition)+" -> "+(int)scaledValue);
			costModelMove.put(transition, (int)scaledValue);
		}
	}

	protected static PNRepResult cancel(PluginContext context, String message) {
		System.out.println("[ModelRepair/global align]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
}
