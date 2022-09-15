package org.processmining.stochasticlabelledpetrinets;

public interface StochasticLabelledPetriNet {
	/**
	 * 
	 * @return the number of transitions. All transitions have indices starting
	 *         at 0 and ending at the returned value (exclusive).
	 */
	public int getNumberOfTransitions();

	/**
	 * 
	 * @return the number of places. All places have indices starting at 0 and
	 *         ending at the returned value (exclusive).
	 */
	public int getNumberOfPlaces();

	/**
	 * Only call when it is certain that the transition is not a silent
	 * transition.
	 * 
	 * @param transition
	 * @return the label of the transition.
	 */
	public String getTransitionLabel(int transition);

	/**
	 * 
	 * @param transition
	 * @return whether the transition is a silent transition
	 */
	public boolean isTransitionSilent(int transition);

	/**
	 * 
	 * @param place
	 * @return the number of tokens on this place in the initial marking.
	 */
	public int isInInitialMarking(int place);

	/**
	 * 
	 * @param transition
	 * @return a list of places that have arcs to this transition. Transitions
	 *         may appear multiple times. The caller must not change the
	 *         returned array.
	 */
	public int[] getInputPlaces(int transition);

	/**
	 * 
	 * @param transition
	 * @return a list of places that have arcs from this transition. Transitions
	 *         may appear multiple times. The caller must not change the
	 *         returned array.
	 */
	public int[] getOutputPlaces(int transition);

	/**
	 * 
	 * @param place
	 * @return a list of transitions that have arcs to this place. Places may
	 *         appear multiple times. The caller must not change the returned
	 *         array.
	 */
	public int[] getInputTransitions(int place);

	/**
	 * 
	 * @param place
	 * @return a list of transitions that have arcs from this place. Places may
	 *         appear multiple times. The caller must not change the returned
	 *         array.
	 */
	public int[] getOutputTransitions(int place);

	/**
	 * 
	 * @return an object that allows for a standardised interpretation of the
	 *         language of the net. The returned object might not be thread safe
	 *         and the implementer must ensure a new, fresh, object is returned
	 *         for each call.
	 */
	public StochasticLabelledPetriNetSemantics getDefaultSemantics();
}
