/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.behavapp;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.plugin.Progress;
import org.processmining.plugins.petrinet.replayer.util.LogAutomatonNode;
import org.processmining.plugins.petrinet.replayer.util.codec.EncPNWSetFinalMarkings;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * @author aadrians Oct 24, 2011
 * 
 */
public interface IBehavAppAlg {
	public SyncReplayResult replayLoop(List<XEventClass> lstEvtClass, EncPNWSetFinalMarkings encodedPN,
			Map<Integer, Map<Integer, Integer>> mapArc2Weight, Map<Integer, Map<Integer, Integer>> mapInt2Marking,
			Map<Map<Integer, Integer>, Integer> mapMarking2Int, Map<Integer, LogAutomatonNode> mapInt2Automaton,
			Map<Integer, XEventClass> mapEncTrans2EvClass, boolean useLogWeight,
			Map<XEventClass, Integer> encActivityWeight, Random numGenerator, Integer maxNumStates,
			Map<List<XEventClass>, Set<Integer>> traceClass, Set<Integer> encInvisTransition,
			Set<Integer> encFinalMarkings, Map<Integer, Integer> mapMarking2EqMarkingClass,
			Map<Integer, Set<XEventClass>> mapMarking2EnabledEvClass,
			Map<Integer, SortedSet<Integer>> mapMarking2Enabled,
			Map<Integer, Map<Integer, Integer>> mapFiringTransitions, Progress progress);
}
