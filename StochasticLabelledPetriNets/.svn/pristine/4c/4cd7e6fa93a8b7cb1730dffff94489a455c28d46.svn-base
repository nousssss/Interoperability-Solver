package org.processmining.stochasticlabelledpetrinets.probability;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;

public interface CrossProductResult {

	/**
	 * The initial state will be reported twice: once as initial state, and
	 * again as a final or non-final state.
	 * 
	 * @param stateIndex
	 */
	public void reportInitialState(int stateIndex);

	/**
	 * A state will be reported as either final, non-final, or dead.
	 * 
	 * @param stateIndex
	 * @param nextStateIndices
	 *            may contain duplicated values. List might be reused and
	 *            changed after this call returns, and changes by the
	 *            implementer will be overwritten.
	 * @param nextStateProbabilities
	 *            list might be reused and changed after this call returns, and
	 *            changes by the implementer will be overwritten.
	 */
	public void reportNonFinalState(int stateIndex, TIntList nextStateIndices, TDoubleList nextStateProbabilities);

	/**
	 * A state will be reported as either final, non-final, or dead. Multiple
	 * states might be reported as final.
	 * 
	 * @param stateIndex
	 */
	public void reportFinalState(int stateIndex);

	/**
	 * A state will be reported as either final, non-final, or dead.
	 * 
	 * A dead state is a state in the cross product that indicates that A made a
	 * move that was not supported by B. At most one state will be reported as
	 * dead.
	 * 
	 * 
	 * @param stateIndex
	 */
	public void reportDeadState(int stateIndex);
}