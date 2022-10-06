package org.processmining.plugins.inductiveminer2.variants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParameters;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParametersForPetriNet;
import org.processmining.plugins.InductiveMiner.mining.cuts.IMc.probabilities.Probabilities;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinder;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinderEmptyLog;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinderEmptyTraces;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinderSemiFlowerModel;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinderSingleActivity;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.CutFinder;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.CutFinderIMConcurrent;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.CutFinderIMConcurrentWithMinimumSelfDistance;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.CutFinderIMExclusiveChoice;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.CutFinderIMLoop;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.CutFinderIMSequence;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThrough;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThroughActivityConcurrent;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThroughActivityOncePerTraceConcurrent;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThroughFlowerWithoutEpsilon;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThroughTauLoop;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThroughTauLoopStrict;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterConcurrent;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterInterleavedFiltering;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterLoop;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterOr;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterSequenceFiltering;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterXorFiltering;
import org.processmining.plugins.inductiveminer2.framework.postprocessor.PostProcessor;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfo;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfoDefault;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMLogImpl;
import org.processmining.plugins.inductiveminer2.mining.MinerState;
import org.processmining.plugins.inductiveminer2.mining.MiningParameters;
import org.processmining.plugins.inductiveminer2.mining.MiningParametersAbstract;

import gnu.trove.set.TIntSet;

public class MiningParametersIM extends MiningParametersAbstract implements InductiveMinerVariant {

	public static final List<BaseCaseFinder> basicBaseCaseFinders = Collections
			.unmodifiableList(Arrays.asList(new BaseCaseFinder[] { //
					new BaseCaseFinderSingleActivity(), //
					new BaseCaseFinderSemiFlowerModel(), //
					new BaseCaseFinderEmptyLog(), //
					new BaseCaseFinderEmptyTraces() }));

	public static final List<CutFinder> basicCutFinders = Collections.unmodifiableList(Arrays.asList(new CutFinder[] { //
			new CutFinderIMExclusiveChoice(), //
			new CutFinderIMSequence(), //
			new CutFinderIMConcurrentWithMinimumSelfDistance(), // 
			new CutFinderIMConcurrent(), //
			new CutFinderIMLoop() }));

	public static final List<FallThrough> basicFallThroughs = Collections
			.unmodifiableList(Arrays.asList(new FallThrough[] { //
					new FallThroughActivityOncePerTraceConcurrent(true), //
					new FallThroughActivityConcurrent(), //
					new FallThroughTauLoopStrict(), //
					new FallThroughTauLoop(), //
					new FallThroughFlowerWithoutEpsilon(), //
	}));

	public static final List<PostProcessor> basicPostProcessors = Collections
			.unmodifiableList(Arrays.asList(new PostProcessor[] { //
	}));

	public static final EfficientTreeReduceParameters basicReduceParameters = new EfficientTreeReduceParametersForPetriNet(
			false);

	public Probabilities getSatProbabilities() {
		return null;
	}

	public IMLog2IMLogInfo getLog2LogInfo() {
		return new IMLog2IMLogInfoDefault();
	}

	public IMLog getIMLog(XLog xLog) {
		return new IMLogImpl(xLog, getClassifier(), getLifeCycleClassifier());
	}

	public XLifeCycleClassifier getLifeCycleClassifier() {
		return MiningParameters.defaultLifeCycleClassifier;
	}

	public List<BaseCaseFinder> getBaseCaseFinders() {
		return basicBaseCaseFinders;
	}

	public List<CutFinder> getCutFinders() {
		return basicCutFinders;
	}

	public List<FallThrough> getFallThroughs() {
		return basicFallThroughs;
	}

	public List<PostProcessor> getPostProcessors() {
		return basicPostProcessors;
	}

	public IMLog[] splitLogConcurrent(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		return LogSplitterConcurrent.split(log, partition, minerState);
	}

	public IMLog[] splitLogInterleaved(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		return LogSplitterInterleavedFiltering.split(log, partition, minerState);
	}

	public IMLog[] splitLogLoop(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		return LogSplitterLoop.split(log, partition, minerState);
	}

	public IMLog[] splitLogOr(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		return LogSplitterOr.split(log, partition, minerState);
	}

	public IMLog[] splitLogSequence(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		return LogSplitterSequenceFiltering.split(log, partition, minerState);
	}

	public IMLog[] splitLogXor(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		return LogSplitterXorFiltering.split(log, partition, minerState);
	}

	@Override
	public String toString() {
		return "Inductive Miner   (IM)";
	}

	@Override
	public boolean hasNoise() {
		return false;
	}

	@Override
	public boolean noNoiseImpliesFitness() {
		return true;
	}

	@Override
	public MiningParametersAbstract getMiningParameters() {
		return this;
	}

	@Override
	public int getWarningThreshold() {
		return -1;
	}

	@Override
	public String getDoi() {
		return "http://dx.doi.org/10.1007/978-3-642-38697-8_17";
	}

	public boolean hasFitness() {
		return true;
	}

	public boolean isRepairLifeCycles() {
		return false;
	}

	public boolean isProcessStartEndComplete() {
		return false;
	}

	public EfficientTreeReduceParameters getReduceParameters() {
		return basicReduceParameters;
	}
}
