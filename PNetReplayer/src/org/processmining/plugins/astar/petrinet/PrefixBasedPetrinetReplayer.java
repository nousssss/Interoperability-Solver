package org.processmining.plugins.astar.petrinet;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.KeepInProMCache;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.plugins.astar.petrinet.impl.PDelegate;
import org.processmining.plugins.astar.petrinet.impl.PHead;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayAlgorithm;

import nl.tue.astar.impl.DijkstraTail;
import nl.tue.astar.impl.memefficient.MemoryEfficientAStarAlgorithm;

@KeepInProMCache
@PNReplayAlgorithm
public class PrefixBasedPetrinetReplayer extends AbstractPetrinetReplayer<DijkstraTail, PDelegate> {

	public String toString() {
		return "Prefix based A* Cost-based Fitness, assuming at most " + Short.MAX_VALUE + " tokens in each place.";
	}

	protected PDelegate getDelegate(PetrinetGraph net, XLog log, XEventClasses classes, TransEvClassMapping mapping,
			int delta, int threads) {
		if (net instanceof ResetInhibitorNet) {
			return new PDelegate((ResetInhibitorNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta,
					true);
		} else if (net instanceof ResetNet) {
			return new PDelegate((ResetNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta, true);
		} else if (net instanceof InhibitorNet) {
			return new PDelegate((InhibitorNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta,
					true);
		} else if (net instanceof Petrinet) {
			return new PDelegate((Petrinet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta, true);
		}
		return null;
	}

	protected double getMinBoundMoveModel(final PluginContext context, PetrinetGraph net, TransEvClassMapping mapping,
			final XEventClasses classes, final double delta, final int threads,
			final MemoryEfficientAStarAlgorithm<PHead, DijkstraTail> aStar) {
		// in prefix calculation, there should not be any lower bound
		return 0;
	}
}
