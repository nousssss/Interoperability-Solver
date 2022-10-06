package org.processmining.plugins.inductiveminer2.variants;

import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterLoopPartialTraces;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterSequenceFilteringPartialTraces;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfo;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfoPartialTraces;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMLogImplPartialTraces;
import org.processmining.plugins.inductiveminer2.mining.MinerState;
import org.processmining.plugins.inductiveminer2.mining.MiningParametersAbstract;

import gnu.trove.set.TIntSet;

public class MiningParametersIMInfrequentPartialTraces extends MiningParametersIMInfrequent {

	@Override
	public IMLog2IMLogInfo getLog2LogInfo() {
		return new IMLog2IMLogInfoPartialTraces();
	}

	@Override
	public IMLog getIMLog(XLog xLog) {
		return new IMLogImplPartialTraces(xLog, getClassifier(), getLifeCycleClassifier());
	}

	@Override
	public IMLog[] splitLogLoop(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		assert (log instanceof IMLogImplPartialTraces);
		return LogSplitterLoopPartialTraces.split((IMLogImplPartialTraces) log, partition, minerState);
	}

	@Override
	public IMLog[] splitLogSequence(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		assert (log instanceof IMLogImplPartialTraces);
		return LogSplitterSequenceFilteringPartialTraces.split((IMLogImplPartialTraces) log, partition, minerState);
	}

	@Override
	public String toString() {
		return "Inductive Miner - infrequent & partial traces   (IMfpt)";
	}

	public boolean hasFitness() {
		return false;
	}

	public boolean hasNoise() {
		return true;
	}

	public boolean noNoiseImpliesFitness() {
		return false;
	}

	public MiningParametersAbstract getMiningParameters() {
		return this;
	}

	public int getWarningThreshold() {
		return -1;
	}

	public String getDoi() {
		return null;
	}
}
