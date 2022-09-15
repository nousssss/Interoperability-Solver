package org.processmining.stochasticlabelledpetrinets;

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
public class StochasticLabelledPetriNetSemanticsSimpleWeightsImpl extends StochasticLabelledPetriNetSemanticsImpl {

	private StochasticLabelledPetriNetSimpleWeights net;

	public StochasticLabelledPetriNetSemanticsSimpleWeightsImpl(StochasticLabelledPetriNetSimpleWeights net) {
		super(net);
		this.net = net;
	}

	@Override
	public double getTransitionWeight(int transition) {
		return net.getTransitionWeight(transition);
	}

	@Override
	public double getTotalWeightOfEnabledTransitions() {
		double result = 0;
		for (int transition = enabledTransitions.nextSetBit(0); transition >= 0; transition = enabledTransitions
				.nextSetBit(transition + 1)) {
			result += net.getTransitionWeight(transition);
		}
		return result;
	}

	@Override
	public StochasticLabelledPetriNetSemanticsSimpleWeightsImpl clone() {
		StochasticLabelledPetriNetSemanticsSimpleWeightsImpl result = (StochasticLabelledPetriNetSemanticsSimpleWeightsImpl) super.clone();

		result.net = net;

		return result;
	}
}