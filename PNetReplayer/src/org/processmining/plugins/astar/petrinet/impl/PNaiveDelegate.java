/**
 * 
 */
package org.processmining.plugins.astar.petrinet.impl;

import java.util.Map;

import nl.tue.astar.impl.State;
import nl.tue.astar.impl.memefficient.TailInflater;
import nl.tue.astar.util.ShortShortMultiset;
import nl.tue.storage.CompressedHashSet;
import nl.tue.storage.Deflater;

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
 * @author aadrians Dec 22, 2011
 * 
 */
public class PNaiveDelegate extends AbstractPDelegate<PNaiveTail> {

	private final PNaiveTail compressor;
	private final boolean allMarkingsAreFinal;

	public PNaiveDelegate(ResetNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta,
			boolean allMarkingsAreFinal, Marking... set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, set);
		this.allMarkingsAreFinal = allMarkingsAreFinal;

		compressor = PNaiveTail.EMPTY;
	}

	public PNaiveDelegate(InhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta,
			boolean allMarkingsAreFinal, Marking... set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, set);
		this.allMarkingsAreFinal = allMarkingsAreFinal;

		compressor = PNaiveTail.EMPTY;
	}

	public PNaiveDelegate(ResetInhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta,
			boolean allMarkingsAreFinal, Marking... set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, set);
		this.allMarkingsAreFinal = allMarkingsAreFinal;

		compressor = PNaiveTail.EMPTY;
	}

	public PNaiveDelegate(Petrinet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta,
			boolean allMarkingsAreFinal, Marking... set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, set);
		this.allMarkingsAreFinal = allMarkingsAreFinal;

		compressor = PNaiveTail.EMPTY;
	}

	public PNaiveTail createInitialTail(PHead head) {
		return PNaiveTail.EMPTY;
	}

	public TailInflater<PNaiveTail> getTailInflater() {
		return compressor;
	}

	public Deflater<PNaiveTail> getTailDeflater() {
		return compressor;
	}

	public void setStateSpace(CompressedHashSet<State<PHead, PNaiveTail>> statespace) {

	}

	public boolean isFinal(ShortShortMultiset marking) {
		if (allMarkingsAreFinal) {
			return true;
		} else {
			if (finalMarkings.size() > 0) {
				return super.isFinal(marking);
			} else {
				return !hasEnabledTransitions(marking);
			}
		}
	}

}
