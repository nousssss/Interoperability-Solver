package org.processmining.plugins.inductiveminer2.mining;

import org.deckfour.xes.classification.XEventClassifier;

/**
 * This class provides basic getters and setters for user-settable mining
 * parameters, and sets defaults. Setting any parameter should not change the
 * algorithm.
 * 
 * @author sander
 *
 */
public abstract class MiningParametersAbstract implements MiningParameters {

	protected XEventClassifier classifier = MiningParameters.defaultClassifier;
	protected float noiseThreshold = MiningParameters.defaultNoiseThreshold;
	protected boolean isDebug = MiningParameters.defaultIsDebug;
	protected boolean isUseMultithreading = MiningParameters.defaultIsUseMultiThreading;

	@Override
	public XEventClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(XEventClassifier classifier) {
		this.classifier = classifier;
	}

	@Override
	public float getNoiseThreshold() {
		return hasNoise() ? noiseThreshold : 0;
	}

	public void setNoiseThreshold(float noiseTreshold) {
		this.noiseThreshold = noiseTreshold;
	}

	@Override
	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	@Override
	public boolean isUseMultithreading() {
		return isUseMultithreading;
	}

	public void setUseMultithreading(boolean isUseMultiThreading) {
		this.isUseMultithreading = isUseMultiThreading;
	}

}
