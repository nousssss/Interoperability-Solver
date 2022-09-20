package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import java.util.List;

import nl.tue.astar.AStarException;
import nl.tue.astar.AStarThread;
import nl.tue.astar.Trace;
import nl.tue.astar.impl.DijkstraTail;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.impl.PHead;
import org.processmining.plugins.astar.petrinet.impl.PRecord;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;

public class BestWithFitnessBoundAlignmentsTreeAlg extends NBestAlignmentsTreeAlg {

	private double fitnessLowerBound = 0.0d;

	public PNMatchInstancesRepResult replayLog(PluginContext context, PetrinetGraph net, Marking initMarking,
			Marking finalMarking, XLog log, TransEvClassMapping mapping, Object[] parameters) throws AStarException {		
		fitnessLowerBound = (Double) parameters[3];
		
		// Setting the original parameter (expected alignments) to make NBestAlignmentsTreeAlg happy
		parameters[3] = Integer.MAX_VALUE;
		return super.replayLog(context, net, initMarking, finalMarking, log, mapping, parameters);
	}

	protected boolean shouldConsiderResult(MatchInstancesRes result, AllOptAlignmentsTreeThread<PHead, DijkstraTail> thread, PRecord record,
			int counter, XLog log, int minCostMoveModel) {
		double fitness = calcFitness(result, (AllOptAlignmentsTreeDelegate) thread.getDelegate(), record, log, minCostMoveModel);
		return fitness > fitnessLowerBound;
	}

	protected double calcFitness(MatchInstancesRes result, AllOptAlignmentsTreeDelegate d, PRecord record, XLog log, int minCostMoveModel) {
		double mmCost = 0.0;
		double mlUpper = 0.0;
		double mlCost = 0.0;
		double mSyncCost = 0.0;

		final Trace filteredTrace = getLinearTrace(log, result.trace, d);
		XTrace trace = log.get(result.trace);
		
		int eventInTrace = -1;
		List<PRecord> history = PRecord.getHistory(record);
		for (PRecord rec : history) {
			if (rec.getMovedEvent() == AStarThread.NOMOVE) {
				// move model only
				mmCost += (d.getCostForMoveModel((short) rec.getModelMove())) / d.getDelta();
			} else {
				// a move occurred in the log. Check if class aligns with class in trace
				short a = (short) filteredTrace.get(rec.getMovedEvent()); // a is the event obtained from the replay
				eventInTrace++;
				XEventClass clsInTrace = d.getClassOf(trace.get(eventInTrace)); // this is the current event
				while (d.getIndexOf(clsInTrace) != a) {
					// The next event in the trace is not of the same class as the next event in the A-star result.
					// This is caused by the class in the trace not being mapped to any transition.
					// move log only
					mlCost += mapEvClass2Cost.get(clsInTrace);
					eventInTrace++;
					clsInTrace = d.getClassOf(trace.get(eventInTrace)); 
				}
				if (rec.getModelMove() == AStarThread.NOMOVE) {
					// move log only
					mlCost += (d.getCostForMoveLog(a)) / d.getDelta();
				} else {
					// sync move
					mSyncCost += (d.getCostForMoveSync((short) rec.getModelMove())) / d.getDelta();
				}
			}
		}
		
		// add the rest of the trace
		eventInTrace++;
		while (eventInTrace < trace.size()) {
			// move log only
			XEventClass a = d.getClassOf(trace.get(eventInTrace++));
			mlCost += mapEvClass2Cost.get(a);
		}

		// calculate mlUpper (because in cases where we have synchronous move in manifest, more than one events are aggregated
		// in one movement
		for (XEvent evt : trace) {
			mlUpper += mapEvClass2Cost.get(d.getClassOf(evt));
		}
		
		return 1 - ((mmCost + mlCost + mSyncCost) / (mlUpper + minCostMoveModel));
	}

	@Override
	public String toString() {
		return "Tree-based state space replay for all alignments down to a specified fitness lower bound (not necessarily optimal)";
	}

	@Override
	public String getHTMLInfo() {
		return "<html>Returns all alignments down to a specified fitness lower bound using tree-based state space.</html>";
	}
	
}
