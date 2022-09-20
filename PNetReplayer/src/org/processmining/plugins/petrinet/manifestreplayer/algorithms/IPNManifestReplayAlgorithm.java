/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayer.algorithms;

import nl.tue.astar.AStarException;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.manifestreplayer.AbstractPNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;

/**
 * @author aadrians Feb 16, 2012
 * 
 */
public interface IPNManifestReplayAlgorithm {

	/**
	 * Main method, assuming that all required inputs are valid
	 * 
	 * @param net
	 * @param log
	 * @param mapping
	 * @param parameter
	 * @return
	 * @throws AStarException
	 */
	public Manifest replayLog(PluginContext context, PetrinetGraph net, XLog log,
			AbstractPNManifestReplayerParameter parameter) throws AStarException;

	/**
	 * Return true if all replay inputs are correct
	 */
	public boolean isAllReqSatisfied(PetrinetGraph net, XLog log, AbstractPNManifestReplayerParameter parameters);

	/**
	 * Return true if input of replay without parameters are correct
	 */
	public boolean isReqWOParameterSatisfied(PetrinetGraph net, XLog log, Marking initMarking, Marking[] finalMarkings);

	/**
	 * Return information about this algorithm
	 * 
	 * @return
	 */
	public String getHTMLInfo();
}
