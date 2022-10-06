package org.processmining.plugins.inductiveminer2.loginfo;

import org.processmining.plugins.inductiveminer2.helperclasses.IntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.MultiIntSet;
import org.processmining.plugins.inductiveminer2.logs.IMLog;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class IMLogInfo {

	protected final IntDfg dfg;

	protected final TIntObjectMap<MultiIntSet> minimumSelfDistancesBetween; //index -> (index^2)
	protected final TIntIntHashMap minimumSelfDistances; //index -> minimum self distance

	protected final long numberOfEvents;
	protected final long numberOfActivityInstances;
	protected final long numberOfTraces;

	public static TIntObjectMap<MultiIntSet> createEmptyMinimumSelfDistancesBetweenMap() {
		return new TIntObjectHashMap<>(10, 0.5f, Integer.MIN_VALUE);
	}

	public static TIntIntHashMap createEmptyMinimumSelfDistancesMap() {
		return new TIntIntHashMap(10, 0.5f, Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	/**
	 * Construct a log-info object. Please use the provided "createEmpty..."
	 * functions to initialise the required hash maps.
	 * 
	 * @param directlyFollowsGraph
	 * @param minimumSelfDistancesBetween
	 * @param minimumSelfDistances
	 * @param numberOfEvents
	 * @param numberOfActivityInstances
	 * @param numberOfTraces
	 */
	public IMLogInfo(IntDfg directlyFollowsGraph, TIntObjectMap<MultiIntSet> minimumSelfDistancesBetween,
			TIntIntHashMap minimumSelfDistances, long numberOfEvents, long numberOfActivityInstances,
			long numberOfTraces) {
		this.dfg = directlyFollowsGraph;
		this.minimumSelfDistancesBetween = minimumSelfDistancesBetween;
		this.minimumSelfDistances = minimumSelfDistances;
		this.numberOfEvents = numberOfEvents;
		this.numberOfActivityInstances = numberOfActivityInstances;
		this.numberOfTraces = numberOfTraces;
	}

	public IntDfg getDfg() {
		return dfg;
	}

	public TIntObjectMap<MultiIntSet> getMinimumSelfDistancesBetween() {
		return minimumSelfDistancesBetween;
	}

	/**
	 * 
	 * @param activityIndex
	 * @return A multset of activity indices that have a minimum self-distance
	 *         relation from the given activityIndex.
	 */
	public MultiIntSet getMinimumSelfDistanceBetween(int activityIndex) {
		if (!minimumSelfDistances.containsKey(activityIndex)) {
			return new MultiIntSet();
		}
		return minimumSelfDistancesBetween.get(activityIndex);
	}

	/**
	 * 
	 * @return A map activity index to minimum self-distance
	 */
	public TIntIntHashMap getMinimumSelfDistances() {
		return minimumSelfDistances;
	}

	public long getMinimumSelfDistance(int activityIndex) {
		if (minimumSelfDistances.containsKey(activityIndex)) {
			return minimumSelfDistances.get(activityIndex);
		}
		return 0;
	}

	public long getNumberOfEvents() {
		return numberOfEvents;
	}

	public long getNumberOfActivityInstances() {
		return numberOfActivityInstances;
	}

	public long getNumberOfTraces() {
		return numberOfTraces;
	}

	public String[] getActivityNames(final IMLog log) {
		int[] set = dfg.getActivities().toSet().toArray();
		final String[] result = new String[set.length];
		for (int i = 0; i < set.length; i++) {
			result[i] = log.getActivity(set[i]);
		}
		return result;
	}

}
