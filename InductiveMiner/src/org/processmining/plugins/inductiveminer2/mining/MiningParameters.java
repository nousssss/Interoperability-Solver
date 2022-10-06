package org.processmining.plugins.inductiveminer2.mining;

import java.util.List;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParameters;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParametersForPetriNet;
import org.processmining.plugins.InductiveMiner.mining.cuts.IMc.probabilities.Probabilities;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinder;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.CutFinder;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThrough;
import org.processmining.plugins.inductiveminer2.framework.postprocessor.PostProcessor;
import org.processmining.plugins.inductiveminer2.helperclasses.XLifeCycleClassifierIgnore;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfo;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;

import gnu.trove.set.TIntSet;

public interface MiningParameters {

	public static final XEventClassifier defaultClassifier = new XEventNameClassifier();
	/**
	 * The default life cycle classifier ignores life cycles completely and maps
	 * every event to 'complete'.
	 */
	public static final XLifeCycleClassifier defaultLifeCycleClassifier = new XLifeCycleClassifierIgnore();
	public static final float defaultNoiseThreshold = 0.2f;
	public static final boolean defaultIsDebug = false;
	public static final boolean defaultIsUseMultiThreading = true;
	public static final boolean defaultIsRepairLifeCycles = false;
	//public static final EfficientTreeReduceParameters defaultReduceParameters = new EfficientTreeReduceParameters(false,
	//		false);
	public static final EfficientTreeReduceParameters defaultReduceParameters = new EfficientTreeReduceParametersForPetriNet(
			false);

	public IMLog getIMLog(XLog xLog);

	public XEventClassifier getClassifier();

	public XLifeCycleClassifier getLifeCycleClassifier();

	/**
	 * 
	 * @return The noise threshold, or 0 if hasNoise() returns false.
	 */
	public float getNoiseThreshold();

	/**
	 * 
	 * @return Whether the noise threshold is relevant.
	 */
	public boolean hasNoise();

	public boolean isDebug();

	public Probabilities getSatProbabilities();

	public boolean isUseMultithreading();

	public IMLog2IMLogInfo getLog2LogInfo();

	public List<BaseCaseFinder> getBaseCaseFinders();

	public List<CutFinder> getCutFinders();

	public List<FallThrough> getFallThroughs();

	public boolean isRepairLifeCycles();

	List<PostProcessor> getPostProcessors();

	public EfficientTreeReduceParameters getReduceParameters();

	public IMLog[] splitLogConcurrent(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState);

	public IMLog[] splitLogInterleaved(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState);

	public IMLog[] splitLogLoop(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState);

	public IMLog[] splitLogOr(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState);

	public IMLog[] splitLogSequence(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState);

	public IMLog[] splitLogXor(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState);

}
