package org.processmining.plugins.inductiveminer2.withoutlog.graphsplitters;

import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.withoutlog.MinerStateWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;

public interface DfgMsdSplitter {
	DfgMsd[] split(DfgMsd graph, Cut cut, MinerStateWithoutLog minerState);
}
