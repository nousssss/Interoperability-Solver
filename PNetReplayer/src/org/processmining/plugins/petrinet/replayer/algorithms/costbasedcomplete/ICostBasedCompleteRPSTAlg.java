/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.plugin.Progress;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.rpstwrapper.RPSTConsultant;
import org.processmining.plugins.petrinet.replayer.util.codec.EncPNWSetFinalMarkings;

/**
 * @author aadrians
 * Oct 24, 2011
 *
 */
public interface ICostBasedCompleteRPSTAlg {
	/**
	 * Replay loop
	 * 
	 * @param lstEvtClass
	 * @param encodedPN
	 * @param encInvisTransition
	 * @param mapArc2Weight
	 * @param mapInt2Marking
	 * @param mapMarking2Int
	 * @param encInitMarking
	 * @param encFinalMarking
	 * @param numGenerator
	 * @param maxNumOfStates
	 * @param mapEvClass2EncTrans
	 * @param mapEvClass2Cost
	 * @param mapEncTrans2Cost
	 * @param mapMarking2Enabled
	 * @param mapFiringTransitions
	 * @param costConsultant
	 * @param progress
	 * 
	 * @return array of objects, contains [0] CPNCostBasedTreeNodeEncFitness
	 *         solution node, and [1] boolean is result reliable
	 */
	public Object[] replayLoop(List<XEventClass> lstEvtClass, EncPNWSetFinalMarkings encodedPN,
			Set<Integer> encInvisTransition, Map<Integer, Map<Integer, Integer>> mapArc2Weight,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking, Map<Map<Integer, Integer>, Integer> mapMarking2Int,
			Integer encInitMarking, Set<Integer> encFinalMarkings, Random numGenerator, int maxNumOfStates,
			Map<XEventClass, Set<Integer>> mapEvClass2EncTrans, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Integer, Integer> mapEncTrans2Cost, Map<Integer, SortedSet<Integer>> mapMarking2Enabled,
			Map<Integer, Map<Integer, Integer>> mapFiringTransitions, RPSTConsultant costConsultant,
			Map<Integer, XEventClass> mapEncTrans2EvClass, Progress progress);
}
