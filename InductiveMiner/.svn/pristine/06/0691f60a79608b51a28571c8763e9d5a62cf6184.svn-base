package org.processmining.plugins.inductiveminer2.withoutlog;

import org.processmining.plugins.inductiveminer2.mining.MiningParameters;

public abstract class MiningParametersWithoutLogAbstract implements MiningParametersWithoutLog {
	protected float noiseThreshold = MiningParameters.defaultNoiseThreshold;
	protected boolean isDebug = MiningParameters.defaultIsDebug;
	protected boolean isUseMultithreading = MiningParameters.defaultIsUseMultiThreading;

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
