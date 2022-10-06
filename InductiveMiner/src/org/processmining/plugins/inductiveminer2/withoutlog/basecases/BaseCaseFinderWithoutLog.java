package org.processmining.plugins.inductiveminer2.withoutlog.basecases;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public interface BaseCaseFinderWithoutLog {
	EfficientTree findBaseCases(DfgMsd graph, MinerStateWithoutLog minerState);
}
