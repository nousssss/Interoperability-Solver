package org.processmining.plugins.inductiveminer2.withoutlog.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParameters;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.MiningParametersWithoutLogAbstract;
import org.processmining.plugins.inductiveminer2.withoutlog.basecases.BaseCaseFinderWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.basecases.BaseCaseFinderWithoutLogEmptyLog;
import org.processmining.plugins.inductiveminer2.withoutlog.basecases.BaseCaseFinderWithoutLogEmptyTraces;
import org.processmining.plugins.inductiveminer2.withoutlog.basecases.BaseCaseFinderWithoutLogSemiFlowerModel;
import org.processmining.plugins.inductiveminer2.withoutlog.basecases.BaseCaseFinderWithoutLogSingleActivity;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLogIMConcurrent;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLogIMConcurrentWithMinimumSelfDistance;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLogIMExclusiveChoice;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLogIMLoop;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLogIMLoopWithMinimumSelfDistance;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLogIMSequence;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;
import org.processmining.plugins.inductiveminer2.withoutlog.fallthroughs.FallThroughWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.fallthroughs.FallThroughWithoutLogFlowerWithoutEpsilon;
import org.processmining.plugins.inductiveminer2.withoutlog.fallthroughs.FallThroughWithoutLogTauLoopStrict;
import org.processmining.plugins.inductiveminer2.withoutlog.graphsplitters.SimpleDfgMsdSplitter;
import org.processmining.plugins.inductiveminer2.withoutlog.postprocessors.PostProcessorWithoutLog;

import gnu.trove.set.TIntSet;

public class MiningParametersIMWithoutLog extends MiningParametersWithoutLogAbstract
		implements InductiveMinerWithoutLogVariant {

	public static final List<BaseCaseFinderWithoutLog> basicBaseCaseFinders = Collections
			.unmodifiableList(Arrays.asList(new BaseCaseFinderWithoutLog[] { //
					new BaseCaseFinderWithoutLogSingleActivity(), //
					new BaseCaseFinderWithoutLogSemiFlowerModel(), //
					new BaseCaseFinderWithoutLogEmptyLog(), //
					new BaseCaseFinderWithoutLogEmptyTraces() }));

	public static final List<CutFinderWithoutLog> basicCutFinders = Collections
			.unmodifiableList(Arrays.asList(new CutFinderWithoutLog[] { //
					new CutFinderWithoutLogIMExclusiveChoice(), //
					new CutFinderWithoutLogIMSequence(), //
					new CutFinderWithoutLogIMConcurrentWithMinimumSelfDistance(), // 
					new CutFinderWithoutLogIMLoopWithMinimumSelfDistance(), //
					new CutFinderWithoutLogIMConcurrent(), //
					new CutFinderWithoutLogIMLoop() }));

	public static final List<FallThroughWithoutLog> basicFallThroughs = Collections
			.unmodifiableList(Arrays.asList(new FallThroughWithoutLog[] { //
					new FallThroughWithoutLogTauLoopStrict(), //
					new FallThroughWithoutLogFlowerWithoutEpsilon(), //
	}));

	@Override
	public String toString() {
		return "Inductive Miner - without log (IMw)";
	}

	@Override
	public boolean hasNoise() {
		return false;
	}

	@Override
	public List<BaseCaseFinderWithoutLog> getBaseCaseFinders() {
		return basicBaseCaseFinders;
	}

	@Override
	public List<CutFinderWithoutLog> getCutFinders() {
		return basicCutFinders;
	}

	@Override
	public List<FallThroughWithoutLog> getFallThroughs() {
		return basicFallThroughs;
	}

	@Override
	public List<PostProcessorWithoutLog> getPostProcessors() {
		return new ArrayList<>();
	}

	@Override
	public EfficientTreeReduceParameters getReduceParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DfgMsd[] splitGraphConcurrent(DfgMsd graph, List<TIntSet> partition, MinerStateWithoutLog minerState) {
		return SimpleDfgMsdSplitter.split(graph, partition, Operator.concurrent);
	}

	@Override
	public DfgMsd[] splitGraphInterleaved(DfgMsd graph, List<TIntSet> partition, MinerStateWithoutLog minerState) {
		return SimpleDfgMsdSplitter.split(graph, partition, Operator.interleaved);
	}

	@Override
	public DfgMsd[] splitGraphLoop(DfgMsd graph, List<TIntSet> partition, MinerStateWithoutLog minerState) {
		return SimpleDfgMsdSplitter.split(graph, partition, Operator.loop);
	}

	@Override
	public DfgMsd[] splitGraphOr(DfgMsd graph, List<TIntSet> partition, MinerStateWithoutLog minerState) {
		return SimpleDfgMsdSplitter.split(graph, partition, Operator.or);
	}

	@Override
	public DfgMsd[] splitGraphSequence(DfgMsd graph, List<TIntSet> partition, MinerStateWithoutLog minerState) {
		return SimpleDfgMsdSplitter.split(graph, partition, Operator.sequence);
	}

	@Override
	public DfgMsd[] splitGraphXor(DfgMsd graph, List<TIntSet> partition, MinerStateWithoutLog minerState) {
		return SimpleDfgMsdSplitter.split(graph, partition, Operator.xor);
	}

	@Override
	public boolean hasFitness() {
		return false;
	}

	@Override
	public boolean noNoiseImpliesFitness() {
		return false;
	}

	@Override
	public MiningParametersWithoutLogAbstract getMiningParameters() {
		return this;
	}

	@Override
	public int getWarningThreshold() {
		return -1;
	}

	@Override
	public String getDoi() {
		return null;
	}

}
