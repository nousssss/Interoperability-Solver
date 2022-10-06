package org.processmining.plugins.inductiveminer2.framework.fallthroughs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.processmining.plugins.InductiveMiner.Sets;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.InductiveMiner.jobList.JobList;
import org.processmining.plugins.InductiveMiner.jobList.JobListConcurrent;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.DfgCutFinderSimple;
import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.IntDfgImpl;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MinerState;
import org.processmining.plugins.inductiveminer2.withoutlog.graphsplitters.SimpleDfgMsdSplitter;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class FallThroughActivityConcurrent implements FallThrough {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.plugins.InductiveMiner.mining.fallthrough.FallThrough
	 * #fallThrough(org.processmining.plugins.InductiveMiner.mining.IMLog,
	 * org.processmining.plugins.InductiveMiner.mining.IMLogInfo,
	 * org.processmining.processtree.ProcessTree,
	 * org.processmining.plugins.InductiveMiner.mining.MiningParameters)
	 * 
	 * Try to leave out an activity and recurse If this works, then putting the
	 * left out activity in parallel is fitness-preserving
	 */

	public FallThroughActivityConcurrent() {

	}

	private class CutWrapper {
		List<TIntSet> partition = null;
	}

	public EfficientTree fallThrough(final IMLog log, final IMLogInfo logInfo, final MinerState minerState) {

		if (logInfo.getDfg().getActivities().setSize() < 3) {
			return null;
		}

		//leave out an activity
		final DfgCutFinderSimple dfgCutFinder = new DfgCutFinderSimple();
		final AtomicBoolean found = new AtomicBoolean(false);
		final CutWrapper cutWrapper = new CutWrapper();

		JobList jobList = new JobListConcurrent(minerState.getMinerPool());

		for (int leaveOutActivity : logInfo.getDfg().getActivities()) {
			//leave out a single activity and try whether that gives a valid cut

			final int leaveOutActivity2 = leaveOutActivity;
			jobList.addJob(new Runnable() {
				public void run() {

					if (minerState.isCancelled()) {
						return;
					}

					if (!found.get()) {

						//in a typical overcomplicated java-way, create a cut (parallel, [{a}, Sigma\{a}])
						TIntSet leaveOutSet = new TIntHashSet(10, 0.5f, Integer.MIN_VALUE);
						leaveOutSet.add(leaveOutActivity2);
						List<TIntSet> partition = new ArrayList<>();
						partition.add(leaveOutSet);
						partition.add(Sets.complement(leaveOutSet, logInfo.getDfg().getActivities().toSet().toArray()));

						InductiveMiner.debug("  try cut " + partition, minerState);

						//split the dfg
						IntDfg subDfg = new IntDfgImpl();
						SimpleDfgMsdSplitter.filterDfg(logInfo.getDfg(), subDfg, partition.get(1), Operator.concurrent,
								partition, 1);

						//see if a cut applies
						//for performance reasons, only on the directly follows graph

						Cut cut2 = dfgCutFinder.findCut(subDfg, minerState);

						if (minerState.isCancelled()) {
							return;
						}

						if (cut2 != null && cut2.isValid()) {
							//see if we are first
							boolean oldFound = found.getAndSet(true);
							if (!oldFound) {
								//we were first
								cutWrapper.partition = partition;
							}
						}
					}
				}
			});
		}

		try {
			jobList.join();
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}

		if (found.get() && !minerState.isCancelled()) {
			//the cut we made is a valid one; split the log, construct the parallel construction and recurse

			InductiveMiner.debug(" fall through: leave out activity", minerState);

			IMLog[] logSplitResult = minerState.parameters.splitLogConcurrent(log, logInfo, cutWrapper.partition,
					minerState);

			if (minerState.isCancelled()) {
				return null;
			}

			//recurse
			EfficientTree child1 = InductiveMiner.mineNode(logSplitResult[0], minerState);

			if (minerState.isCancelled() || child1 == null) {
				return null;
			}

			EfficientTree child2 = InductiveMiner.mineNode(logSplitResult[1], minerState);

			if (minerState.isCancelled() || child2 == null) {
				return null;
			}

			return InlineTree.concurrent(child1, child2);
		} else {
			return null;
		}
	}
}
