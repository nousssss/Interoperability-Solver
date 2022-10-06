package org.processmining.plugins.inductiveminer2.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinder;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinderEmptyLog;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinderEmptyTraces;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinderEmptyTracesFiltering;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinderSingleActivityFiltering;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.CutFinder;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Filter;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThrough;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfo;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfoLifeCycle;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

public class MiningParametersIMInfrequentLifeCycle extends MiningParametersIM {

	public static final List<BaseCaseFinder> filteringBaseCases = Collections
			.unmodifiableList(Arrays.asList(new BaseCaseFinder[] { //
					new BaseCaseFinderEmptyLog(), //
					new BaseCaseFinderEmptyTracesFiltering(), //
					new BaseCaseFinderEmptyTraces(), //
					new BaseCaseFinderSingleActivityFiltering() }));

	public static final CutFinder filteringCutFinders = new CutFinder() {
		public Cut findCut(IMLog log, IMLogInfo logInfo, MinerState minerState) {
			IMLogInfo logInfoFiltered = Filter.filterNoise(logInfo, minerState.parameters.getNoiseThreshold());

			//call IM cut detection
			return InductiveMiner.findCut(null, logInfoFiltered, MiningParametersIM.basicCutFinders, minerState);
		}
	};

	protected static final List<BaseCaseFinder> baseCaseFinders = new ArrayList<>();
	static {
		baseCaseFinders.addAll(filteringBaseCases);
		baseCaseFinders.addAll(basicBaseCaseFinders);
	}

	protected static final List<CutFinder> cutFinders = new ArrayList<>();
	static {
		cutFinders.addAll(basicCutFinders);
		cutFinders.add(filteringCutFinders);
	}

	@Override
	public List<BaseCaseFinder> getBaseCaseFinders() {
		return baseCaseFinders;
	}

	@Override
	public List<CutFinder> getCutFinders() {
		return cutFinders;
	}
	
	@Override
	public XLifeCycleClassifier getLifeCycleClassifier() {
		return MiningParametersIMLifeCycle.lifeCycleClassifier;
	}

	@Override
	public boolean isRepairLifeCycles() {
		return true;
	}

	@Override
	public List<FallThrough> getFallThroughs() {
		return MiningParametersIMLifeCycle.lifeCycleFallthroughs;
	}

	@Override
	public IMLog2IMLogInfo getLog2LogInfo() {
		return new IMLog2IMLogInfoLifeCycle();
	}

	@Override
	public String toString() {
		return "Inductive Miner - infrequent & life cycle   (IMflc)";
	}

	@Override
	public boolean hasFitness() {
		return false;
	}

	@Override
	public boolean hasNoise() {
		return true;
	}

	@Override
	public boolean noNoiseImpliesFitness() {
		return true;
	}

	@Override
	public String getDoi() {
		return "https://doi.org/10.1007/978-3-319-42887-1_17";
	}
}
