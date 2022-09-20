/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import java.util.Map;

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
import org.processmining.plugins.astar.petrinet.impl.DijkstraPDelegate;
import org.processmining.plugins.astar.petrinet.impl.PHeadCompressor;
import org.processmining.plugins.astar.petrinet.impl.PHeadUniqueDijkstraCompressor;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;


/**
 * @author aadrians
 * Mar 11, 2012
 *
 */
public class AllOptAlignmentsTreeDelegate extends DijkstraPDelegate {

	public AllOptAlignmentsTreeDelegate(Petrinet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta, boolean allMarkingAreFinal,
			Marking[] set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, allMarkingAreFinal, set);
	}
	public AllOptAlignmentsTreeDelegate(ResetNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta, boolean allMarkingAreFinal,
			Marking[] set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, allMarkingAreFinal, set);
	}
	public AllOptAlignmentsTreeDelegate(InhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta, boolean allMarkingAreFinal,
			Marking[] set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, allMarkingAreFinal, set);
	}
	public AllOptAlignmentsTreeDelegate(ResetInhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta, boolean allMarkingAreFinal,
			Marking[] set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, allMarkingAreFinal, set);
	}

	@Override
	protected PHeadCompressor<DijkstraTail> constructHeadCompressor(short places, short activities) {
		return new PHeadUniqueDijkstraCompressor(places, activities);
	}
}
