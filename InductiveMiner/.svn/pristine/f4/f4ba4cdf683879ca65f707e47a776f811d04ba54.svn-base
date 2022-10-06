package org.processmining.plugins.directlyfollowsgraph.mining;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.plugins.InductiveMiner.mining.logs.LifeCycleClassifier;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier;

public abstract class DFMMiningParametersAbstract implements DFMMiningParameters {

	public static final XEventClassifier defaultClassifier = new XEventNameClassifier();
	public static final XLifeCycleClassifier defaultLifeCycleClassifier = new LifeCycleClassifier();

	private double noiseThreshold;
	private XEventClassifier classifier = defaultClassifier;
	private XLifeCycleClassifier lifeCycleClassifier = defaultLifeCycleClassifier;

	public double getNoiseThreshold() {
		return noiseThreshold;
	}

	public void setNoiseThreshold(double noiseThreshold) {
		this.noiseThreshold = noiseThreshold;
	}

	public XEventClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(XEventClassifier classifier) {
		this.classifier = classifier;
	}

	public XLifeCycleClassifier getLifeCycleClassifier() {
		return lifeCycleClassifier;
	}

	public void setLifeCycleClassifier(XLifeCycleClassifier lifeCycleClassifier) {
		this.lifeCycleClassifier = lifeCycleClassifier;
	}
}
