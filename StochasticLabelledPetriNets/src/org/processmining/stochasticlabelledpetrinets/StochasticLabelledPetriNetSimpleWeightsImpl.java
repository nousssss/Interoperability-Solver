package org.processmining.stochasticlabelledpetrinets;

import gnu.trove.list.array.TDoubleArrayList;

public class StochasticLabelledPetriNetSimpleWeightsImpl extends StochasticLabelledPetriNetImpl
		implements StochasticLabelledPetriNetSimpleWeightsEditable {

	private TDoubleArrayList transitionWeights;

	public StochasticLabelledPetriNetSimpleWeightsImpl() {
		transitionWeights = new TDoubleArrayList();
	}

	@Override
	public void setTransitionWeight(int transition, double weight) {
		transitionWeights.set(transition, weight);
	}

	@Override
	public int addTransition(String label, double weight) {
		super.addTransition(label, weight);
		transitionWeights.add(weight);
		return transitionWeights.size() - 1;
	}

	@Override
	public double getTransitionWeight(int transition) {
		return transitionWeights.get(transition);
	}

	@Override
	public StochasticLabelledPetriNetSemantics getDefaultSemantics() {
		return new StochasticLabelledPetriNetSemanticsSimpleWeightsImpl(this);
	}

}