/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.AbstractReplayerBasicFunctionProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParamProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.util.codec.EncPNWSetFinalMarkings;
import org.processmining.plugins.petrinet.replayer.util.statespaces.AbstractCPNCostBasedTreeNode;
import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * Abstraction for replay algorithms that require array of final markings (can
 * be empty), all transitions mapped to non-negative integer cost, and so does
 * all event classes.
 * 
 * @author aadrians Oct 23, 2011
 * 
 */
//BVD: REMOVED ANNOTATION TO CLEANUP THE AVAILABLE REPLAYERS IN THE PROM USER INTERFACE.
//@PNReplayAlgorithm
public abstract class AbstractCostBasedCompleteAlg<T extends AbstractCPNCostBasedTreeNode<T>> extends
		AbstractReplayerBasicFunctionProvider implements IPNReplayAlgorithm {
	/**
	 * Imported parameters
	 */
	// required parameters for replay
	protected Map<Transition, Integer> mapTrans2Cost;
	protected Map<XEventClass, Integer> mapEvClass2Cost;
	protected XEventClassifier classifier;
	protected int maxNumOfStates;
	protected Marking initMarking;
	protected Marking[] finalMarkings;

	/**
	 * Return true if all replay inputs are correct: parameter type is correct
	 * and non empty (no null); all transitions are mapped to cost; all event
	 * classes (including dummy event class, i.e. an event class that does not
	 * exist in log, any transitions that are NOT silent and not mapped to any
	 * event class in the log is mapped to it) are mapped to cost; all costs
	 * should be non negative; numStates is non negative
	 */
	public boolean isAllReqSatisfied(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayParameter parameter) {
		if ((net instanceof ResetInhibitorNet) || (net instanceof InhibitorNet) || (net instanceof ResetNet)
				|| (net instanceof Petrinet)) {
			if (parameter instanceof CostBasedCompleteParam) {
				CostBasedCompleteParam param = (CostBasedCompleteParam) parameter;
				if ((param.getMapTrans2Cost() != null) && (param.getMaxNumOfStates() != null)
						&& (param.getMapEvClass2Cost() != null) && (param.getInitialMarking() != null)
						&& (param.getFinalMarkings() != null)) {
					// check all transitions are indeed mapped to cost
					if ((param.getMaxNumOfStates() >= 0)
							&& (param.getMapTrans2Cost().keySet().containsAll(net.getTransitions()))) {
						Set<XEventClass> evClassWithCost = param.getMapEvClass2Cost().keySet();
						// check all event classes are mapped to cost
						XEventClassifier classifier = mapping.getEventClassifier();
						XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
						XEventClasses eventClassesName = summary.getEventClasses();

						if (evClassWithCost.containsAll(eventClassesName.getClasses())) {
							// dummy event class has to be mapped to cost
							if (mapping.getDummyEventClass() != null) {
								if (!evClassWithCost.contains(mapping.getDummyEventClass())) {
									return false;
								}
								;
							}

							// all cost should be non negative
							for (Integer costVal : param.getMapEvClass2Cost().values()) {
								if (costVal < 0) {
									return false;
								}
							}
							for (Integer costVal : param.getMapTrans2Cost().values()) {
								if (costVal < 0) {
									return false;
								}
							}
							return true;
						}
						;
					}
				}
				;
			}
		}
		return false;
	}

	/**
	 * Return true if input of replay without parameters are correct
	 */
	public boolean isReqWOParameterSatisfied(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping) {
		// no special requirement
		return ((net instanceof ResetInhibitorNet) || (net instanceof InhibitorNet) || (net instanceof ResetNet) || (net instanceof Petrinet));
	}

	/**
	 * Assign values of private attributes as given in parameters
	 * 
	 * @param parameters
	 */
	protected void importParameters(CostBasedCompleteParam parameters) {
		// replay parameters
		mapTrans2Cost = parameters.getMapTrans2Cost();
		maxNumOfStates = parameters.getMaxNumOfStates();
		mapEvClass2Cost = parameters.getMapEvClass2Cost();
		initMarking = parameters.getInitialMarking();
		finalMarkings = parameters.getFinalMarkings();
	}

	/**
	 * construct GUI in which the parameter for this algorithm can be obtained
	 */
	public IPNReplayParamProvider constructParamProvider(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping) {
		return new CostBasedCompleteParamProvider(context, net, log, mapping);
	}

	/**
	 * provide solutions
	 * 
	 * @param encPN
	 * @param currNode
	 * @param nodeInstanceLstOfLst
	 * @param stepTypesLstOfLst
	 * @param listTrace
	 */
	protected void createShortListFromTreeNode(EncPNWSetFinalMarkings encPN, T currNode, List<Object> nodeInstanceLst,
			List<StepTypes> stepTypesLst, List<XEventClass> listTrace, List<Pair<Integer, XEventClass>> listMoveOnLog) {

		// prepare to insert move on log event class
		int inverseCounterOriginalEvents = 0;
		Iterator<Pair<Integer, XEventClass>> it = listMoveOnLog.iterator();
		Pair<Integer, XEventClass> evIndex2BInserted = it.hasNext() ? it.next() : null;

		whileloop: while (currNode.getParent() != null) {
			if (evIndex2BInserted != null) {
				if (evIndex2BInserted.getFirst().equals(inverseCounterOriginalEvents)) {
					stepTypesLst.add(0, StepTypes.L);
					nodeInstanceLst.add(0, evIndex2BInserted.getSecond());
					inverseCounterOriginalEvents++;

					it.remove();
					if (it.hasNext()) {
						evIndex2BInserted = it.next();
					} else {
						evIndex2BInserted = null;
					}

					continue whileloop;
				}
			}
			if (currNode.getParent() != null) {
				stepTypesLst.add(0, currNode.getLatestStepType());
				if (currNode.getLatestStepType().equals(StepTypes.L)) {
					nodeInstanceLst.add(0, listTrace.get(currNode.getCurrIndexOnTrace() - 1));
					inverseCounterOriginalEvents++;
				} else {
					if ((currNode.getLatestStepType().equals(StepTypes.LMGOOD))
							|| (currNode.getLatestStepType().equals(StepTypes.LMNOGOOD))) {
						inverseCounterOriginalEvents++;
					}
					nodeInstanceLst.add(0, encPN.getPetrinetNodeOf(currNode.getRelatedStepTypeObj()));
				}
				currNode = currNode.getParent();
			}
		}

		// in case where not all expected activities appear in the replay result (because replay is 
		// not finished, etc. ), just put the leftovers activities in the result.
		if (evIndex2BInserted != null) {
			do {
				stepTypesLst.add(0, StepTypes.L);
				nodeInstanceLst.add(0, evIndex2BInserted.getSecond());
				evIndex2BInserted = it.hasNext() ? it.next() : null;
			} while (evIndex2BInserted != null);
		}
	}
}
