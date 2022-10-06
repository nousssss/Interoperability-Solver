package org.processmining.plugins.inductiveminer2.variants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.processmining.plugins.InductiveMiner.mining.logs.LifeCycleClassifier;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThrough;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThroughActivityConcurrent;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThroughActivityOncePerTraceConcurrent;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThroughFlowerWithoutEpsilon;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThroughTauLoop;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThroughTauLoopStrict;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfo;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfoLifeCycle;

public class MiningParametersIMLifeCycle extends MiningParametersIM {

	public static final XLifeCycleClassifier lifeCycleClassifier = new LifeCycleClassifier();

	public static final List<FallThrough> lifeCycleFallthroughs = Collections
			.unmodifiableList(Arrays.asList(new FallThrough[] { //
					new FallThroughActivityOncePerTraceConcurrent(true), //
					new FallThroughActivityConcurrent(), //
					new FallThroughTauLoopStrict(), //
					new FallThroughTauLoop(), //
					new FallThroughFlowerWithoutEpsilon() }));

	@Override
	public XLifeCycleClassifier getLifeCycleClassifier() {
		return lifeCycleClassifier;
	}

	@Override
	public boolean isRepairLifeCycles() {
		return true;
	}

	@Override
	public List<FallThrough> getFallThroughs() {
		return lifeCycleFallthroughs;
	}

	@Override
	public IMLog2IMLogInfo getLog2LogInfo() {
		return new IMLog2IMLogInfoLifeCycle();
	}

	@Override
	public String toString() {
		return "Inductive Miner - life cycle  (IMlc)";
	}

	@Override
	public String getDoi() {
		return "https://doi.org/10.1007/978-3-319-42887-1_17";
	}
}
