package org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd;

import org.processmining.plugins.directlyfollowsgraph.DirectlyFollowsGraph;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntGraph;

public interface DfgMsd extends DirectlyFollowsGraph {

	public IntGraph getMinimumSelfDistanceGraph();

	public DfgMsd clone();

}