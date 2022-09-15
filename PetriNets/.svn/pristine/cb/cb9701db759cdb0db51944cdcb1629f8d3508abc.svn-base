package org.processmining.petrinets.analysis.gedsim.params;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class GraphEditDistanceSimilarityParameters extends PluginParametersImpl {

	public enum Algorithm {
		GREEDY, PROCESS_HEURISTIC, A_STAR;
	}

	public static final double DEFAULT_WEIGHT_SKIPPED_VERTICES = 1.0;
	public static final double DEFAULT_WEIGHT_SKIPPED_EDGES = 1.0;
	public static final double DEFAULT_WWEIGHT_SUBSTITUTED_NODES = 1.0;
	public static final double DEFAULT_WEIGHT_GROUPED_VERTEX = 0.0;
	//the following parameters are alogirhtm specific.
	//I didn't feel like refactoring so i put these here.
	public static final double DEFAULT_LED_CUT_OFF = 0.0;
	public static final boolean DEFAULT_USE_PURE_DISTANCE = false;
	public static final double DEFAULT_PRUNE_WHEN = 0;
	public static final double DEFAULT_PRUNE_TO = 0;
	public static final boolean DEFAULT_USE_EPSILON = false;
	public static final boolean DEFAULT_DO_GROUPING = false;
	public static final boolean DEFAULT_USE_EVENTS = false;
	public static final Algorithm DEFUALT_ALGORITHM = Algorithm.GREEDY;

	private double weightSkippedVertices;
	public void setWeightSkippedVertices(double weightSkippedVertices) {
		this.weightSkippedVertices = weightSkippedVertices;
	}

	public void setWeightSkippedEdges(double weightSkippedEdges) {
		this.weightSkippedEdges = weightSkippedEdges;
	}

	public void setWeightSubstitutedVertices(double weightSubstitutedVertices) {
		this.weightSubstitutedVertices = weightSubstitutedVertices;
	}

	public void setWeightGroupedVertex(double weightGroupedVertex) {
		this.weightGroupedVertex = weightGroupedVertex;
	}

	public void setLedCutOff(double ledCutOff) {
		this.ledCutOff = ledCutOff;
	}

	public void setUsePureDistance(boolean usePureDistance) {
		this.usePureDistance = usePureDistance;
	}

	public void setPruneWhen(double pruneWhen) {
		this.pruneWhen = pruneWhen;
	}

	public void setPruneTo(double pruneTo) {
		this.pruneTo = pruneTo;
	}

	public void setUseEpsilon(boolean useEpsilon) {
		this.useEpsilon = useEpsilon;
	}

	public void setDoGrouping(boolean doGrouping) {
		this.doGrouping = doGrouping;
	}

	public void setUsEevents(boolean usEevents) {
		this.usEevents = usEevents;
	}

	private double weightSkippedEdges;
	private double weightSubstitutedVertices;
	private double weightGroupedVertex;
	private double ledCutOff;
	private boolean usePureDistance;
	private double pruneWhen;
	private double pruneTo;
	private boolean useEpsilon;
	private boolean doGrouping;
	private boolean usEevents;

	private final Algorithm algorithm;

	public GraphEditDistanceSimilarityParameters() {
		this(DEFAULT_WEIGHT_SKIPPED_VERTICES, DEFAULT_WEIGHT_SKIPPED_EDGES, DEFAULT_WWEIGHT_SUBSTITUTED_NODES,
				DEFUALT_ALGORITHM);
	}

	public GraphEditDistanceSimilarityParameters(final double wSkipN, final double wSkipE, final double wSubN,
			final Algorithm algorithm) {
		this(wSkipN, wSkipE, wSubN, algorithm, DEFAULT_WEIGHT_GROUPED_VERTEX, DEFAULT_LED_CUT_OFF,
				DEFAULT_USE_PURE_DISTANCE, DEFAULT_PRUNE_WHEN, DEFAULT_PRUNE_TO, DEFAULT_USE_EPSILON,
				DEFAULT_DO_GROUPING, DEFAULT_USE_EVENTS);
	}

	public double getWeightSubstitutedVertices() {
		return weightSubstitutedVertices;
	}

	public double getWeightGroupedVertex() {
		return weightGroupedVertex;
	}

	public double getLedCutOff() {
		return ledCutOff;
	}

	public boolean isUsePureDistance() {
		return usePureDistance;
	}

	public double getPruneWhen() {
		return pruneWhen;
	}

	public double getPruneTo() {
		return pruneTo;
	}

	public boolean isUseEpsilon() {
		return useEpsilon;
	}

	public boolean isDoGrouping() {
		return doGrouping;
	}

	public boolean isUsEevents() {
		return usEevents;
	}

	public GraphEditDistanceSimilarityParameters(final double wSkipN, final double wSkipE, final double wSubN,
			final Algorithm algorithm, final double wgv, final double ledC, final boolean usePureDist,
			final double pruneW, final double pruneTo, final boolean useEps, final boolean doGrouping,
			final boolean useEvents) {
		this.weightSkippedVertices = wSkipN;
		this.weightSkippedEdges = wSkipE;
		this.weightSubstitutedVertices = wSubN;
		this.algorithm = algorithm;
		this.weightGroupedVertex = wgv;
		this.ledCutOff = ledC;
		this.usePureDistance = usePureDist;
		this.pruneWhen = pruneW;
		this.pruneTo = pruneTo;
		this.useEpsilon = useEps;
		this.doGrouping = doGrouping;
		this.usEevents = useEvents;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public double getWeightSkippedEdges() {
		return weightSkippedEdges;
	}

	public double getWeightSkippedVertices() {
		return weightSkippedVertices;
	}

	public double getweightSubstitutedVertices() {
		return weightSubstitutedVertices;
	}
}
