package org.processmining.stochasticlabelledpetrinets.probability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import org.apache.commons.lang.ArrayUtils;
import org.processmining.framework.plugin.ProMCanceller;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

public class CrossProductResultSolver implements CrossProductResult {

	static {
		System.loadLibrary("lpsolve55");
		System.loadLibrary("lpsolve55j");
	}

	private int initialState;
	private int deadState;
	private BitSet finalStates;
	private int maxState;
	private ArrayList<int[]> nextStates;
	private ArrayList<double[]> nextStateProbabilities;

	private BitSet deDuplicationCache = new BitSet();

	public CrossProductResultSolver() {
		initialState = -1;
		deadState = -1;
		maxState = -1;
		finalStates = new BitSet();
		nextStates = new ArrayList<>();
		nextStateProbabilities = new ArrayList<>();
	}

	public void reportInitialState(int stateIndex) {
		initialState = stateIndex;
	}

	public void reportNonFinalState(int stateIndex, TIntList nextStateIndices, TDoubleList nextStateProbabilities) {
		maxState = Math.max(maxState, stateIndex);
		deDuplicate(nextStateIndices, nextStateProbabilities);

		while (nextStates.size() <= maxState) {
			this.nextStates.add(null);
			this.nextStateProbabilities.add(null);
		}

		this.nextStates.set(stateIndex, nextStateIndices.toArray());
		this.nextStateProbabilities.set(stateIndex, nextStateProbabilities.toArray());
	}

	public void reportFinalState(int stateIndex) {
		finalStates.set(stateIndex);
		maxState = Math.max(maxState, stateIndex);
	}

	public void reportDeadState(int stateIndex) {
		this.deadState = stateIndex;
	}

	private void deDuplicate(TIntList nextStateIndexes, TDoubleList nextStateProbabilities) {
		deDuplicationCache.clear();
		for (int indexA = 0; indexA < nextStateIndexes.size(); indexA++) {
			int nodeA = nextStateIndexes.get(indexA);
			if (deDuplicationCache.get(nodeA)) {
				//look for the duplicate
				for (int indexB = 0; indexB < indexA; indexB++) {
					int nodeB = nextStateIndexes.get(indexB);
					if (nodeA == nodeB) {
						nextStateProbabilities.set(indexB,
								nextStateProbabilities.get(indexB) + nextStateProbabilities.get(indexA));
						nextStateIndexes.removeAt(indexA);
						nextStateProbabilities.removeAt(indexA);
						indexA--;
						break;
					}
				}
			}

			deDuplicationCache.set(nodeA);
		}
	}

	/**
	 * Structure of the LP model:
	 * 
	 * One row per state; one column per state.
	 * 
	 * @return
	 * @throws LpSolveException
	 */
	public double solve(ProMCanceller canceller) throws LpSolveException {
		LpSolve solver = LpSolve.makeLp(0, maxState + 1);

		solver.setDebug(false);
		solver.setVerbose(0);

		//set objective function
		{
			solver.setObj(initialState + 1, 1);
		}

		//set upper bounds
		for (int stateIndex = 0; stateIndex <= maxState; stateIndex++) {
			solver.setUpbo(stateIndex + 1, 1);
		}

		solver.setAddRowmode(true);

		for (int stateIndex = 0; stateIndex <= maxState; stateIndex++) {

			if (stateIndex == deadState) {
				//a dead state has a 0 probability to end up in a final state
				solver.setBounds(deadState + 1, 0, 0);
			} else if (finalStates.get(stateIndex)) {
				//a final state has a 1 probability to end up in a final state
				solver.setBounds(stateIndex + 1, 1, 1);
			} else {
				//any other state has a probability equal to the weighted sum of its next states, to end up in a final state
				int[] columns = ArrayUtils.add(increment(nextStates.get(stateIndex)), stateIndex + 1);
				double[] probabilities = ArrayUtils.add(nextStateProbabilities.get(stateIndex), -1);
				solver.addConstraintex(columns.length, probabilities, columns, LpSolve.EQ, 0);
			}

			if (canceller.isCancelled()) {
				return Double.NaN;
			}
		}

		solver.setAddRowmode(false);

		if (canceller.isCancelled()) {
			return Double.NaN;
		}

		solver.printLp();

		solver.solve();

		//solver.printSolution(maxState + 1);

		return solver.getObjective();
	}

	private static int[] increment(int[] array) {
		int[] result = Arrays.copyOf(array, array.length);
		for (int i = 0; i < result.length; i++) {
			result[i]++;
		}
		return result;
	}
}