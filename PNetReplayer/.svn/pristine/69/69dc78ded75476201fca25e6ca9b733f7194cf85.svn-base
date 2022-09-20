package org.processmining.plugins.astar.petrinet;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.annotations.KeepInProMCache;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.plugins.astar.petrinet.impl.PNaiveDelegate;
import org.processmining.plugins.astar.petrinet.impl.PNaiveTail;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayAlgorithm;

@KeepInProMCache
@PNReplayAlgorithm(isBasic = true)
public class PetrinetReplayerWithoutILP extends AbstractPetrinetReplayer<PNaiveTail, PNaiveDelegate> {

	public String toString() {
		return "Dijkstra-based replayer assuming at most " + Short.MAX_VALUE + " tokens in each place.";
	}

	protected PNaiveDelegate getDelegate(PetrinetGraph net, XLog log, XEventClasses classes,
			TransEvClassMapping mapping, int delta, int threads) {
		if (net instanceof ResetInhibitorNet) {
			return new PNaiveDelegate((ResetInhibitorNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost,
					delta, false, finalMarkings);
		} else if (net instanceof ResetNet) {
			return new PNaiveDelegate((ResetNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta,
					false, finalMarkings);
		} else if (net instanceof InhibitorNet) {
			return new PNaiveDelegate((InhibitorNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta,
					false, finalMarkings);
		} else if (net instanceof Petrinet) {
			return new PNaiveDelegate((Petrinet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost, delta,
					false, finalMarkings);
		}
		return null;

	}

}
