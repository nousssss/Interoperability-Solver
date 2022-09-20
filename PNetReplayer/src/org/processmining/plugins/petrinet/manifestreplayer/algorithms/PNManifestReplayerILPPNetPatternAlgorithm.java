/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayer.algorithms;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.manifestreplayer.AbstractPNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerNetPatternParameter;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;

/**
 * @author aadrians May 17, 2012
 * 
 */
public class PNManifestReplayerILPPNetPatternAlgorithm implements IPNManifestReplayAlgorithm {

	@Override
	public String getHTMLInfo() {
		return "<html><p>Replay to construct manifestation of petri net patterns. This algorithm is based on "
				+ "the A* algorithm, with ILP-based heuristic function.</p></html>";
	}

	/**
	 * the name of the algorithm
	 */
	public String toString() {
		return "A*-ILP-based manifest replay with petri net pattern";
	}

	/*
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

	/**
	 * Checking based on net, log, init marking, and final markings
	 */
	public boolean isReqWOParameterSatisfied(PetrinetGraph net, XLog log, Marking initMarking, Marking[] finalMarkings) {
		// for now, limit this algorithm to Petri net 
		return ((initMarking != null) && (finalMarkings != null) && (finalMarkings.length > 0));
	}

	protected boolean isCorrectParameterType(AbstractPNManifestReplayerParameter param) {
		return param instanceof PNManifestReplayerNetPatternParameter;
	}

	public Manifest replayLog(PluginContext context, PetrinetGraph net, XLog log,
			AbstractPNManifestReplayerParameter parametersAbs) {
		PNManifestReplayerNetPatternParameter parameters = (PNManifestReplayerNetPatternParameter) parametersAbs;
		
		/**
		 * Local variables
		 */
		
		return null;
	}

}
