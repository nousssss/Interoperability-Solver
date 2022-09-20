/**
 * 
 */
package org.processmining.plugins.petrinet.replayresult;

import java.util.Map;
import java.util.Set;

import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * @author aadrians
 * 
 */
public interface PNRepResult extends Set<SyncReplayResult> {

	/**
	 * reference to information in SyncReplayResult (and info, if it is cached)
	 */
	//	public static final String FITNESS = "Prefix Fitness"; // refer to TRACEFITNESS instead
	public static final String TRACEFITNESS = "Trace Fitness";
	// NOTE: TRACEFITNESS can be based on prefix, complete trace, depends on the algorithm
	
	public static final String BEHAVIORAPPROPRIATENESS = "Behavioral Appropriateness";

	public static final String UNRELIABLEALIGNMENTS = "Unreliable Alignments";
	public static final String MOVELOGFITNESS = "Move-Log Fitness";
	public static final String MOVEMODELFITNESS = "Move-Model Fitness";
	public static final String RAWFITNESSCOST = "Raw Fitness Cost";
	public static final String MAXFITNESSCOST = "Max Fitness Cost";
	public static final String MAXMOVELOGCOST = "Max Move-Log Cost";
	public static final String NUMSTATEGENERATED = "Num. States";
	public static final String QUEUEDSTATE = "Queued States";
	//	public static final String COMPLETEFITNESS = "Fitness"; // refer to TRACEFITNESS instead
	public static final String TIME = "Calculation Time (ms)";

	// additional
	public static final String ORIGTRACELENGTH = "Trace Length";

	public static final String VISTITLE = "Title of Visualization";

	public static final String TRAVERSEDARCS = "Traversed Arcs";

	/**
	 * Add information
	 * 
	 * @param property
	 * @param valString
	 */
	public void addInfo(String property, String valString);

	/**
	 * @return the info
	 */
	public Map<String, Object> getInfo();

	/**
	 * @param info
	 *            the info to set
	 */
	public void setInfo(Map<String, Object> info);

}