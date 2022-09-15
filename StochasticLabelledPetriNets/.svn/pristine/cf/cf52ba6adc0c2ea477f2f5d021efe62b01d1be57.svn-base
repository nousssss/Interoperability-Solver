package org.processmining.stochasticlabelledpetrinets;

public interface StochasticLabelledPetriNetEditable extends StochasticLabelledPetriNet {
	public void setTransitionLabel(int transition, String label);

	public void makeTransitionSilent(int transition);

	/**
	 * Add a labelled transition.
	 * 
	 * @param label
	 * @param weight
	 * @return the index of the transition.
	 */
	public int addTransition(String label, double weight);

	/**
	 * Add a silent transition.
	 * 
	 * @param weight
	 * @return the index of the transition.
	 */
	public int addTransition(double weight);

	/**
	 * 
	 * @return the index of the place.
	 */
	public int addPlace();

	/**
	 * 
	 * @param place
	 * @param cardinality
	 *            May be negative, however ensure the final marking contains a
	 *            positive number of tokens in each place.
	 */
	public void addPlaceToInitialMarking(int place, int cardinality);

	/**
	 * Adds a token to the given place in the final marking.
	 * 
	 * @param place
	 */
	public void addPlaceToInitialMarking(int place);

	public void addPlaceTransitionArc(int place, int transition);

	/**
	 * 
	 * @param place
	 * @param transition
	 * @param cardinality
	 *            May be negative.
	 */
	public void addPlaceTransitionArc(int place, int transition, int cardinality);

	public void addTransitionPlaceArc(int transition, int place);

	/**
	 * 
	 * @param transition
	 * @param place
	 * @param cardinality
	 *            May be negative.
	 */
	public void addTransitionPlaceArc(int transition, int place, int cardinality);
}