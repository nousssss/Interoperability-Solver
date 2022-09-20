/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.syncproduct;

import java.util.Map;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayer.algorithms.AbstractDefaultPNReplayParam;

/**
 * @author aadrians
 * Oct 21, 2011
 *
 */
public class SyncProductParam extends AbstractDefaultPNReplayParam {

	private Integer moveOnLogOnly = null;
	private Integer moveOnModelOnlyInvi = null;
	private Integer moveOnModelOnlyReal = null;
	private Marking initialMarking = null;

	// negative if not permitted
	private Integer moveSynchronizedViolating = null;
	private Boolean moveSynchronizedViolatingPartially = null;	
	
	public SyncProductParam(){
		moveOnLogOnly = 5;
		moveOnModelOnlyInvi = 0;
		moveOnModelOnlyReal = 2;
		moveSynchronizedViolating = -1; // not permitted
		moveSynchronizedViolatingPartially = false;
		
		initialMarking = new Marking();
	}
	
	/**
	 * @return the initialMarking
	 */
	public Marking getInitialMarking() {
		return initialMarking;
	}

	/**
	 * @param initialMarking
	 *            the initialMarking to set
	 */
	public void setInitialMarking(Marking initialMarking) {
		this.initialMarking = initialMarking;
	}

	/**
	 * @return the moveOnLogOnly
	 */
	public Integer getMoveOnLogOnly() {
		return moveOnLogOnly;
	}

	/**
	 * @param moveOnLogOnly
	 *            the moveOnLogOnly to set
	 */
	public void setMoveOnLogOnly(Integer moveOnLogOnly) {
		this.moveOnLogOnly = moveOnLogOnly;
	}

	/**
	 * @return the moveOnModelOnlyInvi
	 */
	public Integer getMoveOnModelOnlyInvi() {
		return moveOnModelOnlyInvi;
	}

	/**
	 * @param moveOnModelOnlyInvi
	 *            the moveOnModelOnlyInvi to set
	 */
	public void setMoveOnModelOnlyInvi(Integer moveOnModelOnlyInvi) {
		this.moveOnModelOnlyInvi = moveOnModelOnlyInvi;
	}

	/**
	 * @return the moveOnModelOnlyReal
	 */
	public Integer getMoveOnModelOnlyReal() {
		return moveOnModelOnlyReal;
	}

	/**
	 * @param moveOnModelOnlyReal
	 *            the moveOnModelOnlyReal to set
	 */
	public void setMoveOnModelOnlyReal(Integer moveOnModelOnlyReal) {
		this.moveOnModelOnlyReal = moveOnModelOnlyReal;
	}

	/**
	 * @return the moveSynchronizedViolating
	 */
	public Integer getMoveSynchronizedViolating() {
		return moveSynchronizedViolating;
	}

	/**
	 * @param moveSynchronizedViolating
	 *            the moveSynchronizedViolating to set
	 */
	public void setMoveSynchronizedViolating(Integer moveSynchronizedViolating) {
		this.moveSynchronizedViolating = moveSynchronizedViolating;
	}

	/**
	 * @return the moveSynchronizedViolatingPartially
	 */
	public Boolean getMoveSynchronizedViolatingPartially() {
		return moveSynchronizedViolatingPartially;
	}

	/**
	 * @param moveSynchronizedViolatingPartially
	 *            the moveSynchronizedViolatingPartially to set
	 */
	public void setMoveSynchronizedViolatingPartially(Boolean moveSynchronizedViolatingPartially) {
		this.moveSynchronizedViolatingPartially = moveSynchronizedViolatingPartially;
	}

	public void replaceTransitions(Map<Transition, Transition> configuration, boolean keepNonReplacedMapping) {
		// nothing needs to be done here
	}

	public void setFinalMarkings(Marking[] finalMarkings) {
		// nothing needs to be done
	}

}
