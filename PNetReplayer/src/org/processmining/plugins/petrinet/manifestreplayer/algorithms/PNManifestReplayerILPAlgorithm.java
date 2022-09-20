/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayer.algorithms;

import nl.tue.astar.AStarException;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerILPRestrictedMoveModel;
import org.processmining.plugins.astar.petrinet.manifestreplay.CostBasedCompleteManifestParam;
import org.processmining.plugins.astar.petrinet.manifestreplay.ManifestFactory;
import org.processmining.plugins.astar.petrinet.manifestreplay.PNManifestFlattener;
import org.processmining.plugins.petrinet.manifestreplayer.AbstractPNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

/**
 * @author aadrians Feb 20, 2012
 * 
 */
public class PNManifestReplayerILPAlgorithm implements IPNManifestReplayAlgorithm {
	/**
	 * Checking based on net, log, mapping between net and log, and replay
	 * parameters
	 */
	public boolean isAllReqSatisfied(PetrinetGraph net, XLog log, AbstractPNManifestReplayerParameter param) {
		if (isCorrectParameterType(param)) {
			// final marking must exist
			if ((param.getFinalMarkings() != null) && (param.getFinalMarkings().length > 0)) {
				// check if the cost of all transition class is inserted
				if ((param.getMapEvClass2Cost() != null) && (param.getTransClass2Cost() != null)) {
					return isReqWOParameterSatisfied(net, log, param.getInitMarking(), param.getFinalMarkings());
				}
				;
			}
		}
		return false;
	}

	protected boolean isCorrectParameterType(AbstractPNManifestReplayerParameter param) {
		return param instanceof PNManifestReplayerParameter;
	}

	/**
	 * Checking based on net, log, init marking, and final markings
	 */
	public boolean isReqWOParameterSatisfied(PetrinetGraph net, XLog log, Marking initMarking, Marking[] finalMarkings) {
		// for now, limit this algorithm to Petri net 
		return ((initMarking != null) && (finalMarkings != null) && (finalMarkings.length > 0));
	}

	/**
	 * replay log, assuming whatever dependencies are satisfied. context is not
	 * used unless parameters.isGUIMode() is true
	 * @throws AStarException 
	 */
	public Manifest replayLog(final PluginContext context, final PetrinetGraph net, final XLog log,
			final AbstractPNManifestReplayerParameter parametersAbs) throws AStarException {
		PNManifestReplayerParameter parameters = (PNManifestReplayerParameter) parametersAbs;
		/**
		 * Local variables
		 */
		PNManifestFlattener flattener = new PNManifestFlattener(net, parameters); // stores everything about petri net

		/**
		 * To Debug: print the flattened petri net
		 */
		//ProvidedObjectHelper.publish(context, "Flattened " + net.getLabel(), (ResetInhibitorNet) flattener.getNet(), ResetInhibitorNet.class, true);

		// create parameter
		CostBasedCompleteManifestParam parameter = new CostBasedCompleteManifestParam(flattener.getMapEvClass2Cost(),
				flattener.getMapTrans2Cost(), flattener.getMapSync2Cost(), flattener.getInitMarking(),
				flattener.getFinalMarkings(), parameters.getMaxNumOfStates(), 
				flattener.getFragmentTrans());
		parameter.setGUIMode(false);
		parameter.setCreateConn(false);

		// call petri net replayer with ILP
		PNLogReplayer replayer = new PNLogReplayer();

		// select algorithm with ILP
		PetrinetReplayerILPRestrictedMoveModel replayWithILP = new PetrinetReplayerILPRestrictedMoveModel();

		PNRepResult pnRepResult = replayer.replayLog(parameters.isGUIMode() ? context : null, flattener.getNet(), log,
				flattener.getMap(), replayWithILP, parameter);

		// translate result back to desired output
		try {
			Manifest manifestation = ManifestFactory.construct(net, parameters.getInitMarking(),
					parameters.getFinalMarkings(), log, flattener, pnRepResult, parameters.getMapping());

			if (context != null) {
				context.getFutureResult(0).setLabel(
						"Sequence pattern manifestation in " + XConceptExtension.instance().extractName(log));
			}
			return manifestation;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Short explanation about the algorithm
	 */
	public String getHTMLInfo() {
		return "<html><p>Replay to construct manifestation of event class sequence patterns. This algorithm is based on "
				+ "the A* algorithm, with ILP-based heuristic function.</p></html>";
	}

	/**
	 * the name of the algorithm
	 */
	public String toString() {
		return "A*-ILP-based manifest replay";
	}

}
