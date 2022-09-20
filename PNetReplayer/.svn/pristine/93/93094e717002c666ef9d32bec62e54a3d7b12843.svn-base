package org.processmining.plugins.astar.petrinet.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nl.tue.astar.impl.memefficient.CachedStorageAwareDelegate;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

public class PILPDelegate extends AbstractPILPDelegate<PILPTail> implements CachedStorageAwareDelegate<PHead, PILPTail> {

	protected PILPTailCompressor tailCompressor;

	public PILPDelegate(Petrinet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta, int threads,
			Marking... set) {
		this((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost,
				new HashMap<Transition, Integer>(0), delta, threads, true, true, set);
	}

	public PILPDelegate(ResetNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta, int threads,
			Marking... set) {
		this((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost,
				new HashMap<Transition, Integer>(0), delta, threads, true, true, set);
	}

	public PILPDelegate(InhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta, int threads,
			Marking... set) {
		this((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost,
				new HashMap<Transition, Integer>(0), delta, threads, true, true, set);
	}

	public PILPDelegate(ResetInhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta, int threads,
			Marking... set) {
		this((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost,
				new HashMap<Transition, Integer>(0), delta, threads, true, true, set);
	}

	/**
	 * The following constructors accept mapping from sync moves to cost
	 */

	public PILPDelegate(Petrinet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, Marking... set) {
		this((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads,
				true, true, set);
	}

	public PILPDelegate(ResetNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, Marking... set) {
		this((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads,
				true, true, set);
	}

	public PILPDelegate(InhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, Marking... set) {
		this((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads,
				true, true, set);
	}

	public PILPDelegate(ResetInhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, Marking... set) {
		this((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads,
				true, true, set);
	}

	/**
	 * The following constructors accept mapping from sync moves to cost
	 */

	public PILPDelegate(Petrinet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, boolean useInts, boolean useFastLowerBounds,
			Marking... set) {
		this((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads,
				useInts, useFastLowerBounds, set);
	}

	public PILPDelegate(ResetNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, boolean useInts, boolean useFastLowerBounds,
			Marking... set) {
		this((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads,
				useInts, useFastLowerBounds, set);
	}

	public PILPDelegate(InhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, boolean useInts, boolean useFastLowerBounds,
			Marking... set) {
		this((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads,
				useInts, useFastLowerBounds, set);
	}

	public PILPDelegate(ResetInhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, boolean useInts, boolean useFastLowerBounds,
			Marking... set) {
		this((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads,
				useInts, useFastLowerBounds, set);
	}

	/**
	 * For backwards compatibility
	 */
	@Deprecated
	protected PILPDelegate(PetrinetGraph net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, Marking... set) {
		this(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads, true, true, set);
	}

	/**
	 * The main constructor that in the end is called by other constructors
	 */
	protected PILPDelegate(PetrinetGraph net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, boolean useInts, boolean useFastLowerBounds,
			Marking... set) {
		super(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads, useInts,
				useFastLowerBounds, set);

		this.tailCompressor = new PILPTailCompressor(2 * transitions + activities + resetArcs + set.length, places,
				activities);

	}

	/**
	 * Loads the required jar and dll files (from the location) provided by the
	 * user via the settings if not loaded already and creates a solverfactory
	 * 
	 * @return solverfactory
	 * @throws IOException
	 */
	public PILPTail createInitialTail(PHead head) {
		return new PILPTail(this, head, 0);
	}

	public PILPTailCompressor getTailInflater() {
		return tailCompressor;
	}

	public PILPTailCompressor getTailDeflater() {
		return tailCompressor;
	}

	public PHeadCompressor<PILPTail> getHeadInflater() {
		return headCompressor;
	}

	public PHeadCompressor<PILPTail> getHeadDeflater() {
		return headCompressor;
	}

}
