package org.processmining.stochasticlabelledpetrinets.probability;

public interface FollowerSemantics<B> {
	/**
	 * 
	 * @return The initial state.
	 */
	public abstract B getInitialState();

	/**
	 * 
	 * @param label
	 * @return The new state, or null if the step cannot be taken.
	 */
	public abstract B takeStep(B state, String label);

	public abstract boolean isFinalState(B state);
}
