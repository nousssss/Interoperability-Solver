package org.processmining.plugins.inductiveminer2.framework.fallthroughs;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * Try to exclude a single activity, given that it occurs precisely once per
 * trace.
 * 
 * @author sleemans
 *
 */
public class FallThroughActivityOncePerTraceConcurrent implements FallThrough {

	private final boolean strict;

	/**
	 * 
	 * @param strict
	 *            Denotes whether this case is applied strictly, i.e. true =
	 *            only apply if each trace contains precisely one activity;
	 *            false = apply it also if it's close enough.
	 */
	public FallThroughActivityOncePerTraceConcurrent(boolean strict) {
		this.strict = strict;
	}

	public EfficientTree fallThrough(IMLog log, IMLogInfo logInfo, MinerState minerState) {
		if (logInfo.getDfg().getNumberOfActivities() > 1) {

			int[] activities = logInfo.getDfg().getActivities().sortByCardinality();
			for (int activity : activities) {

				/*
				 * An arbitrary parallel cut is always possible. However, to
				 * save precision we only want to split here if this activity
				 * occurs precisely once in each trace.
				 */

				long cardinality = logInfo.getDfg().getActivities().getCardinalityOf(activity);
				long epsilon = logInfo.getDfg().getNumberOfEmptyTraces();
				boolean x = epsilon == 0 && cardinality == logInfo.getNumberOfTraces();

				double noise = minerState.parameters.getNoiseThreshold();
				double avg = cardinality / logInfo.getNumberOfTraces();
				double reverseNoise = noise == 1 ? Double.MAX_VALUE : 1 / (1 - noise);
				boolean y = epsilon < logInfo.getNumberOfTraces() * noise && avg > 1 - noise && avg < reverseNoise;

				if (x || (!strict && y)) {

					InductiveMiner.debug(" fall through: leave out one-per-trace activity", minerState);

					//create cut
					TIntSet sigma0 = new TIntHashSet(10, 0.5f, Integer.MIN_VALUE);
					sigma0.add(activity);
					TIntSet sigma1 = new TIntHashSet(10, 0.5f, Integer.MIN_VALUE);
					sigma1.addAll(activities);
					sigma1.remove(activity);
					List<TIntSet> partition = new ArrayList<>();
					partition.add(sigma0);
					partition.add(sigma1);

					//split log
					IMLog[] logSplitResult = minerState.parameters.splitLogConcurrent(log, logInfo, partition,
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
				}
			}
		}
		return null;
	}
}
