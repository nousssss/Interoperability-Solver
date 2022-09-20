package org.processmining.modelrepair.parameters;

/**
 * Parameter object for {@link org.processmining.modelrepair.plugins.Uma_RepairModel_Plugin}
 * @author dfahland
 *
 */
public class RepairConfiguration {
	/**
	 * If set to 'true', the plugin will apply a few heuristics to detect
	 * whether the event log contains cyclic behavior (repetitions) of a certain
	 * number of steps where the process model contains no cycle with all these
	 * steps. If such a cycle is found, the plugin tests whether introducing a
	 * single "loop back" transitions increases fitness of the log to the model.
	 * If yes, the transition is added, if not, the model remains unchanged.
	 * This parameter is optional and reduces the number of sub-processes added
	 * due to {@link #detectSubProcesses}.
	 */
	public boolean 	detectLoops = true;
	
	/**
	 * A technical parameter used during loop detection ({@link #detectLoops}). When set
	 * to '0' (default value), loop detection will ignore that some iterations
	 * of a loop may require to skip certain process steps within the loop. If
	 * set to a value >= 1, loop detection will balance between the 'skip
	 * transitions' that have to be added if the loop is added and the
	 * sub-process that has to be added if the loop is not added. Generally, the
	 * parameter should be set to '0' to ease loop detection and preserve
	 * similarity to the original model. However, if the possible loop has a
	 * complex inner structure, the analysis for loops may incur very running
	 * times. In this case, set a value >= 1 to ensure faster completion.
	 */
	public int		loopModelMoveCosts = 0;
	
	/**
	 * If set to 'true', the plugin will extend the process model in two ways.
	 * (1) If the log requires certain process steps to be skipped (by a model
	 * move in the alignment), the plugin adds a 'skip' transition for this step
	 * that allows to proceed in the process without taking the process step.
	 * (2) If the log requires additional process steps that are currently not
	 * in the model (due to log moves in the alignment), the plugin identifies
	 * the exact locations where consecutive sequences of additional steps
	 * should be added and inserts subprocesses that fit the missing behavior.
	 * This parameter is mandatory to obtain a model that perfectly fits the
	 * given log.
	 */
	public boolean  detectSubProcesses  = true;
	
	/**
	 * If set to 'true', the plugin identifies process steps which are never or
	 * rarely executed according to the log and removes any step that is
	 * infrequent without breaking the flow in the model. Use Parameter_6 set
	 * the threshold for when a node is considered infrequent. This parameter is
	 * optional and should be used to obtain a simpler model.
	 */
	public boolean  removeInfrequentNodes = true;
	
	/**
	 * The threshold value for when a node is considered 'infrequent' in the
	 * removal of infrequent nodes ({@link #removeInfrequentNodes} ). The
	 * threshold is specified as the absolute number of occurrences of a process
	 * step in the log. Set to '1' (default) to remove only process steps which
	 * never occur in the log (this ensures a fitting model); set to > 0 to also
	 * remove parts of the model used only infrequently (gives a simpler model
	 * that does not show all behaviors of the log); set to '0' to preserve all
	 * process steps (regardless of used or not).
	 */
	public int      remove_keepIfAtLeast = 1;

	/**
	 * If set to 'true', the plugin analyzes the deviations between model and
	 * log on a global level to identify the smallest set of process steps that
	 * are missing or should be skipped. This parameter is optional. It causes
	 * higher runtime cost in the deviation analysis as several alignments are
	 * computed, but it results in simpler models with a higher similarity to
	 * the original model. In both cases, the resulting model will perfectly fit
	 * the log (if {@link #detectSubProcesses} is set to 'true')
	 */
	public boolean  globalCostAlignment = true;
	
	/**
	 * Parameter used by computation of a global cost alignment 
	 * ({@link #globalCostAlignment}). It specifies the number of analysis
	 * iterations done to identify the smallest number of process steps in the
	 * model that require a repair. Usually, the smallest number is found after
	 * one global analysis (default value '1').
	 */
	public int		globalCost_maxIterations = 1;
	
	/**
	 * Use in conjunction with subprocess detection {@link #detectSubProcesses}.
	 * If set to 'true', the identified sequences of steps that have to be added
	 * to the model as sub-processes are analyzed for similarities. Subsequences
	 * of similar events are grouped together which leads to smaller
	 * subprocesses that are inserted at more specific locations in the process.
	 * This parameter is optional and may lead to simpler models with a higher
	 * similarity to the original model. In both cases, the resulting model will
	 * perfectly fit the log (if {@link #detectSubProcesses} is set to 'true').
	 */
	public boolean  alignAlignments = true;
	
	/**
	 * Repair may modify the structure of the model in a way that invalidates
	 * the final marking of the net. If this parameter is set to 'true', the
	 * repair will try to infer a final marking whenever existing final places
	 * of the model are modified or new final places are created.
	 */
	public boolean  repairFinalMarking = true;
}
