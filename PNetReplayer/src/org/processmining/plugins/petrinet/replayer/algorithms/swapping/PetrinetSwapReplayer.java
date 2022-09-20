/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.swapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.KeepInProMCache;
import org.processmining.framework.providedobjects.ProvidedObjectDeletedException;
import org.processmining.framework.providedobjects.ProvidedObjectID;
import org.processmining.framework.providedobjects.ProvidedObjectManager;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerILPRestrictedMoveModel;
import org.processmining.plugins.astar.petrinet.manifestreplay.CostBasedCompleteManifestParam;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParamProvider;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResultImpl;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.petrinet.replayresult.SwappedMove;
import org.processmining.plugins.petrinet.replayresult.ViolatingSyncMove;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;
import org.processmining.plugins.utils.ProvidedObjectHelper;

/**
 * @author aadrians Sep 12, 2012
 * 
 */
@KeepInProMCache
@PNReplayAlgorithm
public class PetrinetSwapReplayer extends PetrinetReplayerILPRestrictedMoveModel {
	@Override
	public boolean isAllReqSatisfied(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
			IPNReplayParameter parameter) {
		if (parameter instanceof CostBasedSwapParam) {
			Marking[] finalMarking = ((CostBasedSwapParam) parameter).getFinalMarkings();
			if ((finalMarking != null) && (finalMarking.length > 0)) {
				return true;
			}
			;
		}
		return false;
	}

	@Override
	public String toString() {
		return "A* Cost-based Fitness Express with ILP (swap+replacement aware), assuming at most " + Short.MAX_VALUE
				+ " tokens in each place.";
	}

	@Override
	public String getHTMLInfo() {
		return "<html>This is an algorithm to calculate alignment between "
				+ "a log and a Petri net, taking into account possible swapped and replaced activities. <br/><br/>"
				+ "Given a trace and a Petri net, this algorithm "
				+ "return a matching between the trace and an allowed firing sequence of the net with the"
				+ "least deviation cost using the A* algorithm-based technique. The firing sequence has to reach proper "
				+ "termination (possible final markings/dead markings) of the net. <br/><br/>"
				+ "To minimize the number of explored state spaces, the algorithm prunes visited states/equally visited states. <br/><br/>"
				+ "Cost for deviations (e.g. move on model, move on log, replacement, and swapping) of "
				+ "activities can be assigned uniquely for each deviation. </html>";
	}

	@Override
	public PNRepResult replayLog(final PluginContext context, PetrinetGraph net, final XLog log,
			TransEvClassMapping mapping, final IPNReplayParameter parameters) {
		CostBasedSwapParam param = (CostBasedSwapParam) parameters;

		// mapping from constructed net to the original net
		Map<Transition, Transition> trans2Orig = new HashMap<Transition, Transition>(); // map from exploded transitions to transitions
		Map<Transition, Transition> replacementTrans = new HashMap<Transition, Transition>(); // map from replacement transition to the transition its replaced with
		Map<Transition, Pair<Transition, Transition>> swapTrans = new HashMap<Transition, Pair<Transition, Transition>>(); // map from swap transition to the transition its swapped with

		// construct another petri net for which replacement and swapping are represented
		TransEvClassMapping finalMapping = new TransEvClassMapping(mapping.getEventClassifier(),
				mapping.getDummyEventClass());
		CostBasedCompleteManifestParam finalParam = new CostBasedCompleteManifestParam(param.getMapEvClass2Cost(),
				new HashMap<Transition, Integer>(), null, null, param.getMaxNumOfStates(), null); // null values are set later
		finalParam.setGUIMode(param.isGUIMode());
		finalParam.setCreateConn(false);

		PetrinetGraph finalNet = SwapPetrinetGraphConstructor.createSwapPetrinet(net, mapping, param, finalMapping,
				finalParam, trans2Orig, replacementTrans, swapTrans);

		// replay as replay with restriction
		PetrinetReplayerILPRestrictedMoveModel replayer = new PetrinetReplayerILPRestrictedMoveModel();
		PNRepResult res = replayer.replayLog(context, finalNet, log, finalMapping, finalParam);

		// translate it back to the original petri net
		PNRepResult result = translateBack(res, trans2Orig, replacementTrans, swapTrans, finalMapping);

		// create connection with parameter if it does not exists
		if ((context != null)&&(context instanceof UIPluginContext)) {
			UIPluginContext contextUI = (UIPluginContext) context;
			ProvidedObjectHelper.publish(contextUI, "Default swap replay parameter", param, CostBasedSwapParam.class,
					false);
			try {
				// get previous parameter and delete objects etc
				CostBasedSwapParamConnection conn = contextUI.getConnectionManager().getFirstConnection(
						CostBasedSwapParamConnection.class, contextUI, net, log);
				CostBasedSwapParam oldParam = (CostBasedSwapParam) conn
						.getObjectWithRole(CostBasedSwapParamConnection.PARAM);

				// replace old param
				ProvidedObjectManager objManager = contextUI.getProvidedObjectManager();
				Iterator<ProvidedObjectID> it = objManager.getProvidedObjects().iterator();
				ProvidedObjectID id = null;
				while (it.hasNext()) {
					ProvidedObjectID tempid = it.next();
					try {
						Object obj = objManager.getProvidedObjectObject(id, false);
						if (obj.equals(oldParam)) {
							id = tempid;
							break;
						}
					} catch (ProvidedObjectDeletedException e) {
						// do nothing
					}
				}

				if (id != null) {
					try {
						objManager.deleteProvidedObject(id);
					} catch (ProvidedObjectDeletedException e) {
						e.printStackTrace();
					}
				}

				// replace the param in the connection
				conn.remove();

			} catch (ConnectionCannotBeObtained e) {
				// then the connection needs to be created
			}
			// create new connection
			contextUI.addConnection(new CostBasedSwapParamConnection(net, log, param));

		}

		// return result
		return result;
	}

	private PNRepResult translateBack(PNRepResult res, Map<Transition, Transition> trans2Orig,
			Map<Transition, Transition> replacementTrans, Map<Transition, Pair<Transition, Transition>> swapTrans,
			TransEvClassMapping finalMapping) {
		// since much memory may 
		System.gc();

		// interpret result 
		List<SyncReplayResult> finalLst = new ArrayList<SyncReplayResult>(res.size());

		Iterator<SyncReplayResult> it = res.iterator();
		while (it.hasNext()) {

			// pointer to original object
			SyncReplayResult origSRR = it.next();
			Iterator<Object> niIt = origSRR.getNodeInstance().iterator();
			Iterator<StepTypes> stIt = origSRR.getStepTypes().iterator();

			// new object
			List<Object> finalNodeInstance = new ArrayList<Object>(origSRR.getNodeInstance().size());
			List<StepTypes> finalStepTypes = new ArrayList<StepTypes>(origSRR.getNodeInstance().size());

			// start transformation
			while (stIt.hasNext()) {
				switch (stIt.next()) {
					case LMGOOD :
						// this can be sync move, replacement, or swapped
						Transition t = (Transition) niIt.next();
						Transition origT = replacementTrans.get(t);
						if (origT != null) {
							// this is a replacement
							finalNodeInstance.add(new ViolatingSyncMove(origT, finalMapping.get(t), null));
							finalStepTypes.add(StepTypes.LMREPLACED);
						} else {
							Pair<Transition, Transition> pair = swapTrans.get(t);
							if (pair != null) {
								// this is a swapped transition
								finalNodeInstance.add(new SwappedMove(pair.getFirst(), pair.getSecond()));
								finalStepTypes.add(StepTypes.LMSWAPPED);
							} else {
								// this is really a move synchronous
								finalNodeInstance.add(trans2Orig.get(t));
								finalStepTypes.add(StepTypes.LMGOOD);
							}
						}
						break;
					case MINVI :
						finalNodeInstance.add(trans2Orig.get(niIt.next()));
						finalStepTypes.add(StepTypes.MINVI);
						break;
					case MREAL :
						// copy after map back to original
						finalNodeInstance.add(trans2Orig.get(niIt.next()));
						finalStepTypes.add(StepTypes.MREAL);
						break;
					case L :
						// copy as it is
						finalNodeInstance.add(niIt.next());
						finalStepTypes.add(StepTypes.L);
						break;
					default :
						// manifest replay should not return any other step types
						break;
				}

				// remove original
				stIt.remove();
				niIt.remove();
			}

			// construct final new object
			SyncReplayResult finalSRR = new SyncReplayResult(finalNodeInstance, finalStepTypes, 0);
			finalSRR.setTraceIndex(origSRR.getTraceIndex());
			finalSRR.setInfo(origSRR.getInfo());
			finalSRR.setReliable(origSRR.isReliable());
			finalLst.add(finalSRR);

			it.remove(); // remove immediately to safe memory
		}

		PNRepResult finalRes = new PNRepResultImpl(finalLst);
		finalRes.setInfo(res.getInfo());

		return finalRes;
	}

	/**
	 * construct GUI in which the parameter for this algorithm can be obtained
	 */
	@Override
	public IPNReplayParamProvider constructParamProvider(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping) {
		return new CostBasedCompleteSwapParamProvider(context, net, log, mapping);
	}

}
