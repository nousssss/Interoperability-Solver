package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import java.util.Arrays;

import nl.tue.astar.AStarException;
import nl.tue.astar.impl.DijkstraTail;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.impl.PHead;
import org.processmining.plugins.astar.petrinet.impl.PRecord;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;

public class BestWithFitnessBoundPerTraceAlignmentTreeAlg extends BestWithFitnessBoundAlignmentsTreeAlg {
	
	private float[] fitnessLowerBounds;

	public PNMatchInstancesRepResult replayLog(PluginContext context, PetrinetGraph net, Marking initMarking,
			Marking finalMarking, XLog log, TransEvClassMapping mapping, Object[] parameters) throws AStarException {
		
		if (parameters[3] instanceof Double) {
			fitnessLowerBounds = new float[log.size()];
			Arrays.fill(fitnessLowerBounds, ((Double) parameters[3]).floatValue());
		} else if (parameters[3] instanceof float[]) {
			fitnessLowerBounds = (float[]) parameters[3];
		} else {
			throw new UnsupportedOperationException("Missing parameter[3]: Lower fitness bound!");
		}

		// Setting the original parameter (single lower bound) to make BestWithFitnessBoundAlignmentsTreeAlg happy
		parameters[3] = 0.0d;
		
		return super.replayLog(context, net, initMarking, finalMarking, log, mapping, parameters);
	}

	protected boolean shouldConsiderResult(MatchInstancesRes result,
			AllOptAlignmentsTreeThread<PHead, DijkstraTail> thread, PRecord record, int counter, XLog log,
			int minCostMoveModel) {
		double fitness = calcFitness(result, (AllOptAlignmentsTreeDelegate) thread.getDelegate(), record, log, minCostMoveModel);
		return fitness > fitnessLowerBounds[result.trace];
	}

	@Override
	public String toString() {
		return "Tree-based state space replay for all alignments (not necessarily optimal) down to a different fitness lower bound for each trace";
	}

	@Override
	public String getHTMLInfo() {
		return "<html>Returns all alignments (not necessarily optimal) down to a different fitness lower bound for each trace using tree-based state space.</html>";
	}
	
}
