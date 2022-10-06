package org.processmining.plugins.inductiveminer2.withoutlog.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.withoutlog.InductiveMinerWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.basecases.BaseCaseFinderWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.basecases.BaseCaseFinderWithoutLogEmptyLog;
import org.processmining.plugins.inductiveminer2.withoutlog.basecases.BaseCaseFinderWithoutLogEmptyTraces;
import org.processmining.plugins.inductiveminer2.withoutlog.basecases.BaseCaseFinderWithoutLogEmptyTracesFiltering;
import org.processmining.plugins.inductiveminer2.withoutlog.basecases.BaseCaseFinderWithoutLogSingleActivityFiltering;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.FilterWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public class MiningParametersIMInfrequentWithoutLog extends MiningParametersIMWithoutLog {

	public static final List<BaseCaseFinderWithoutLog> filteringBaseCases = Collections
			.unmodifiableList(Arrays.asList(new BaseCaseFinderWithoutLog[] { //
					new BaseCaseFinderWithoutLogEmptyLog(), //
					new BaseCaseFinderWithoutLogEmptyTracesFiltering(), //
					new BaseCaseFinderWithoutLogEmptyTraces(), //
					new BaseCaseFinderWithoutLogSingleActivityFiltering() }));

	public static final CutFinderWithoutLog filteringCutFinders = new CutFinderWithoutLog() {
		public Cut findCut(DfgMsd graph, MinerStateWithoutLog minerState) {
			DfgMsd logInfoFiltered = FilterWithoutLog.filterNoise(graph, minerState.parameters.getNoiseThreshold());

			//call IM cut detection
			return InductiveMinerWithoutLog.findCut(logInfoFiltered, MiningParametersIMWithoutLog.basicCutFinders,
					minerState);
		}
	};

	private static final List<BaseCaseFinderWithoutLog> baseCaseFinders = new ArrayList<>();
	static {
		baseCaseFinders.addAll(filteringBaseCases);
		baseCaseFinders.addAll(basicBaseCaseFinders);
	}

	private static final List<CutFinderWithoutLog> cutFinders = new ArrayList<>();
	static {
		cutFinders.addAll(basicCutFinders);
		cutFinders.add(filteringCutFinders);
	}

	@Override
	public String toString() {
		return "Inductive Miner - infrequent - without log (IMfw)";
	}

	@Override
	public boolean hasNoise() {
		return true;
	}

	@Override
	public List<BaseCaseFinderWithoutLog> getBaseCaseFinders() {
		return baseCaseFinders;
	}

	@Override
	public List<CutFinderWithoutLog> getCutFinders() {
		return cutFinders;
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
	public int getWarningThreshold() {
		return -1;
	}

	@Override
	public String getDoi() {
		return null;
	}

}
