/**
 * 
 */
package org.processmining.plugins.astar.petrinet.impl;

import java.util.Map;

import nl.tue.astar.AStarThread;
import nl.tue.astar.impl.DijkstraTail;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

/**
 * @author aadrians Jan 17, 2012
 * 
 */
public class DijkstraPDelegate extends PDelegate {

	public DijkstraPDelegate(Petrinet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta,
			boolean allMarkingsAreFinal, Marking[] set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, allMarkingsAreFinal, set);
	}

	public DijkstraPDelegate(ResetNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta,
			boolean allMarkingsAreFinal, Marking[] set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, allMarkingsAreFinal, set);
	}

	public DijkstraPDelegate(InhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta,
			boolean allMarkingsAreFinal, Marking[] set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, allMarkingsAreFinal, set);
	}

	public DijkstraPDelegate(ResetInhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta,
			boolean allMarkingsAreFinal, Marking[] set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, allMarkingsAreFinal, set);
	}

	@Override
	public int getCostForMoveLog(short activity) {
		if (activity == AStarThread.NOMOVE) {
			return 0;
		}
		return act2cost.get(activity);
	}

	@Override
	public int getCostForMoveModel(short transition) {
		if (transition == AStarThread.NOMOVE) {
			return 0;
		}
		return trans2cost.get(transition);
	}

	@Override
	public int getCostForMoveSync(short transition) {
		return sync2cost.get(transition);
	}

	@Override
	public int getCostFor(int modelMove, int activity) {
		if (modelMove == AStarThread.NOMOVE) {
			// move on log only
			return getCostForMoveLog((short) activity);
		}
		if (activity == AStarThread.NOMOVE) {
			return getCostForMoveModel((short) modelMove);
		}
		// synchronous move assumed here
		return getCostForMoveSync((short) modelMove);
	}

	@Override
	protected PHeadCompressor<DijkstraTail> constructHeadCompressor(short places, short activities) {
		return new PHeadUniqueDijkstraCompressor(places, activities);
	}
}
