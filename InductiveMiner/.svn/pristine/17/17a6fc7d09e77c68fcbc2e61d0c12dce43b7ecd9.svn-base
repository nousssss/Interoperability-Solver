package org.processmining.plugins.inductiveminer2.withoutlog;

import java.util.List;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParameters;
import org.processmining.plugins.inductiveminer2.withoutlog.basecases.BaseCaseFinderWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;
import org.processmining.plugins.inductiveminer2.withoutlog.fallthroughs.FallThroughWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.postprocessors.PostProcessorWithoutLog;

import gnu.trove.set.TIntSet;

public interface MiningParametersWithoutLog {
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

	public boolean isUseMultithreading();

	public List<BaseCaseFinderWithoutLog> getBaseCaseFinders();

	public List<CutFinderWithoutLog> getCutFinders();

	public List<FallThroughWithoutLog> getFallThroughs();

	public List<PostProcessorWithoutLog> getPostProcessors();

	public EfficientTreeReduceParameters getReduceParameters();

	public DfgMsd[] splitGraphConcurrent(DfgMsd graph, List<TIntSet> partition, MinerStateWithoutLog minerState);

	public DfgMsd[] splitGraphInterleaved(DfgMsd graph, List<TIntSet> partition, MinerStateWithoutLog minerState);

	public DfgMsd[] splitGraphLoop(DfgMsd graph, List<TIntSet> partition, MinerStateWithoutLog minerState);

	public DfgMsd[] splitGraphOr(DfgMsd graph, List<TIntSet> partition, MinerStateWithoutLog minerState);

	public DfgMsd[] splitGraphSequence(DfgMsd graph, List<TIntSet> partition, MinerStateWithoutLog minerState);

	public DfgMsd[] splitGraphXor(DfgMsd graph, List<TIntSet> partition, MinerStateWithoutLog minerState);
}
