/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.annotations.KeepInProMCache;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayMultipleAlignmentAlgorithm;

/**
 * @author aadrians
 * Mar 2, 2013
 *
 */
@PNReplayMultipleAlignmentAlgorithm
@KeepInProMCache
public class AllPrefixOptAlignmentsTreeAlg extends AllOptAlignmentsTreeAlg {
	public String toString() {
		return "Tree-based state space replay to obtain all prefix optimal alignments";
	}
	
	@Override
	public String getHTMLInfo(){
		return "<html>Returns all prefix optimal alignments using tree-based state space.</html>";
	};

	protected AllOptAlignmentsTreeDelegate getDelegate(PetrinetGraph net, XLog log, XEventClasses classes,
			TransEvClassMapping map, Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			int delta, boolean allMarkingsAreFinal, Marking[] finalMarkings) {
		return super.getDelegate(net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, delta, true, finalMarkings);
	}
}
