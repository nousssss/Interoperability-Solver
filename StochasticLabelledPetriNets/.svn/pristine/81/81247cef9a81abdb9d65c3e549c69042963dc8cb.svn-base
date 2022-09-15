package org.processmining.stochasticlabelledpetrinets;

import java.util.BitSet;

import org.python.bouncycastle.util.Arrays;

/**
 * This semantics aims to avoid traversing all transitions. After construction,
 * executing a transition will only consider the transitions whose enabledness
 * may have changed. The only all-transition operation is BitSet.clear().
 * 
 * After construction, the semantics will only allocate local non-array
 * variables.
 * 
 * @author sander
 *
 */
public abstract class StochasticLabelledPetriNetSemanticsImpl implements StochasticLabelledPetriNetSemantics {

	private final StochasticLabelledPetriNet net;
	private byte[] state;
	private byte[] cacheState;
	protected BitSet enabledTransitions;
	private BitSet cacheTransition;
	protected int numberOfEnabledTransitions;

	public StochasticLabelledPetriNetSemanticsImpl(StochasticLabelledPetriNet net) {
		this.net = net;
		state = new byte[net.getNumberOfPlaces()];
		cacheState = new byte[net.getNumberOfPlaces()];
		cacheTransition = new BitSet(net.getNumberOfTransitions());
		enabledTransitions = new BitSet(net.getNumberOfTransitions());
		setInitialState();
	}

	@Override
	public int getNumberOfTransitions() {
		return net.getNumberOfTransitions();
	}

	@Override
	public void setInitialState() {
		enabledTransitions.clear();
		numberOfEnabledTransitions = 0;
		for (int place = 0; place < net.getNumberOfPlaces(); place++) {
			state[place] = (byte) net.isInInitialMarking(place);
		}
		computeEnabledTransitions();
	}

	@Override
	public void executeTransition(int transition) {
		int[] inSet = net.getInputPlaces(transition);
		for (int place : inSet) {
			if (state[place] == 0) {
				throw new RuntimeException("non-existing token consumed");
			}
			state[place]--;

			//update the enabled transitions; some transitions might be disabled by this execution
			for (int transitionT : net.getOutputTransitions(place)) {
				computeEnabledTransition(transitionT);
			}
		}

		int[] postSet = net.getOutputPlaces(transition);
		for (int place : postSet) {
			if (state[place] == Byte.MAX_VALUE) {
				throw new RuntimeException("maximum number of tokens in a place exceeded");
			}
			state[place]++;

			//update the enabled transitions; some transitions might be enabled by this execution
			for (int transitionT : net.getOutputTransitions(place)) {
				computeEnabledTransition(transitionT);
			}
		}
	}

	private boolean computeEnabledTransition(int transition) {
		//due to potential multiplicity of arcs, we have to keep track of how many tokens we would consume
		System.arraycopy(state, 0, cacheState, 0, state.length);

		int[] inSet = net.getInputPlaces(transition);
		for (int inPlace : inSet) {
			if (cacheState[inPlace] == 0) {
				if (enabledTransitions.get(transition)) {
					enabledTransitions.set(transition, false);
					numberOfEnabledTransitions--;
				}
				return false;
			} else {
				cacheState[inPlace]--;
			}
		}

		if (!enabledTransitions.get(transition)) {
			enabledTransitions.set(transition, true);
			numberOfEnabledTransitions++;
		}
		return true;
	}

	private void computeEnabledTransitions() {
		numberOfEnabledTransitions = 0;
		for (int transition = 0; transition < net.getNumberOfTransitions(); transition++) {
			computeEnabledTransition(transition);
		}
	}

	@Override
	public BitSet getEnabledTransitions() {
		return enabledTransitions;
	}

	@Override
	public boolean isFinalState() {
		return numberOfEnabledTransitions == 0;
	}

	@Override
	public byte[] getState() {
		return Arrays.clone(state);
	}

	@Override
	public void setState(byte[] newState) {
		byte[] oldState = this.state;
		this.state = Arrays.clone(newState);

		cacheTransition.clear();

		//walk through all places that have changed, and update the transition enabledness
		for (int place = 0; place < net.getNumberOfPlaces(); place++) {
			if (oldState[place] != state[place]) {
				for (int transition : net.getInputTransitions(place)) {
					if (!cacheTransition.get(transition)) {
						computeEnabledTransition(transition);
						cacheTransition.set(transition);
					}
				}
				for (int transition : net.getOutputTransitions(place)) {
					if (!cacheTransition.get(transition)) {
						computeEnabledTransition(transition);
						cacheTransition.set(transition);
					}
				}
			}
		}
	}

	@Override
	public boolean isTransitionSilent(int transition) {
		return net.isTransitionSilent(transition);
	}

	@Override
	public String getTransitionLabel(int transition) {
		return net.getTransitionLabel(transition);
	}

	@Override
	public StochasticLabelledPetriNetSemanticsImpl clone() {
		StochasticLabelledPetriNetSemanticsImpl result;
		try {
			result = (StochasticLabelledPetriNetSemanticsImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}

		result.cacheState = Arrays.clone(cacheState);
		result.cacheTransition = (BitSet) cacheTransition.clone();
		result.enabledTransitions = (BitSet) enabledTransitions.clone();
		result.numberOfEnabledTransitions = numberOfEnabledTransitions;
		result.state = Arrays.clone(state);

		return result;
	}
}