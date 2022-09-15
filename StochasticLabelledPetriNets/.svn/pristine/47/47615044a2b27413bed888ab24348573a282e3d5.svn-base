package org.processmining.stochasticlabelledpetrinets.probability;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSemantics;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class CrossProduct {

	protected static class ABState<B> {

		public ABState(byte[] stateA, B stateB) {
			this.stateA = stateA;
			this.stateB = stateB;
		}

		final byte[] stateA;
		final B stateB;

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(stateA);
			result = prime * result + Objects.hash(stateB);
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			ABState<B> other = (ABState<B>) obj;
			return Arrays.equals(stateA, other.stateA) && Objects.equals(stateB, other.stateB);
		}
	}

	private static class Z<B> {
		int stateCounter = 0;
		StochasticLabelledPetriNetSemantics semantics;
		TObjectIntMap<ABState<B>> seen = new TObjectIntHashMap<>(10, 0.5f, -1);
		ArrayDeque<ABState<B>> worklist = new ArrayDeque<>();
	}

	private static class Y {
		TIntList outgoingStates = new TIntArrayList();
		TDoubleList outgoingStateProbabilities = new TDoubleArrayList();
	}

	public static <B> void traverse(StochasticLabelledPetriNetSemantics semanticsA, FollowerSemantics<B> systemB,
			CrossProductResult result, ProMCanceller canceller) {
		Z<B> z = new Z<>();
		Y y = new Y();
		z.semantics = semanticsA;
		semanticsA.setInitialState();

		//initialise
		{
			ABState<B> state = new ABState<>(z.semantics.getState(), systemB.getInitialState());
			z.worklist.add(state);
			z.seen.put(state, z.stateCounter);
			result.reportInitialState(z.stateCounter);
			z.stateCounter++;
		}

		int deadStateA = z.stateCounter;
		z.stateCounter++;
		result.reportDeadState(deadStateA);

		BitSet enabledTransitions = new BitSet();

		while (!z.worklist.isEmpty()) {
			ABState<B> stateAB = z.worklist.pop();
			int stateABindex = z.seen.get(stateAB);

			z.semantics.setState(stateAB.stateA);

			if (z.semantics.isFinalState()) {
				if (systemB.isFinalState(stateAB.stateB)) {
					result.reportFinalState(stateABindex);
				}
			} else {
				enabledTransitions.clear();
				enabledTransitions.or(z.semantics.getEnabledTransitions());
				double totalWeight = z.semantics.getTotalWeightOfEnabledTransitions();

				y.outgoingStates.clear();
				y.outgoingStateProbabilities.clear();

				for (int transition = enabledTransitions.nextSetBit(0); transition >= 0; transition = enabledTransitions
						.nextSetBit(transition + 1)) {

					z.semantics.setState(stateAB.stateA);
					z.semantics.executeTransition(transition);

					byte[] newStateA = z.semantics.getState();
					if (z.semantics.isTransitionSilent(transition)) {
						//silent transition; only A takes a step
						B newStateB = stateAB.stateB;

						processNewState(z, y, totalWeight, transition, newStateA, newStateB);
					} else {
						//labelled transition; both A and B take steps
						B newStateB = systemB.takeStep(stateAB.stateB, z.semantics.getTransitionLabel(transition));
						if (newStateB != null) {
							processNewState(z, y, totalWeight, transition, newStateA, newStateB);
						} else {
							//dead state
							y.outgoingStates.add(deadStateA);
							y.outgoingStateProbabilities.add(z.semantics.getTransitionWeight(transition) / totalWeight);
						}
					}
				}

				result.reportNonFinalState(stateABindex, y.outgoingStates, y.outgoingStateProbabilities);
			}

			if (canceller.isCancelled()) {
				return;
			}
		}
	}

	private static <B> void processNewState(Z<B> z, Y y, double totalWeight, int transition, byte[] newStateA,
			B newStateB) {
		ABState<B> newStateAB = new ABState<B>(newStateA, newStateB);
		int newStateIndex = z.seen.adjustOrPutValue(newStateAB, 0, z.stateCounter);
		if (newStateIndex == z.stateCounter) {
			//newStateAB was not encountered before
			z.stateCounter++;
			z.worklist.add(newStateAB);
		}

		y.outgoingStates.add(newStateIndex);
		y.outgoingStateProbabilities.add(z.semantics.getTransitionWeight(transition) / totalWeight);
	}
}