/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms;

import java.util.Map;

import nl.tue.astar.AStarThread.ASynchronousMoveSorting;
import nl.tue.astar.AStarThread.Canceller;
import nl.tue.astar.AStarThread.QueueingModel;
import nl.tue.astar.AStarThread.Type;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * @author aadrians Oct 21, 2011
 * 
 */
public interface IPNReplayParameter {
	public boolean isCreatingConn();

	public boolean isGUIMode();

	/**
	 * If true, messages are printed to context object
	 * 
	 * @param value
	 */
	public void setGUIMode(boolean value);

	/**
	 * If true, connection between replay result and its net and log is created
	 * 
	 * @param value
	 */
	public void setCreateConn(boolean value);

	public void setInitialMarking(Marking initMarking);

	public void setFinalMarkings(Marking... finalMarkings);

	/**
	 * Replace transitions in this parameter (if the parameter consider
	 * individual transitions) with another transitions.
	 * 
	 * @param configuration
	 *            map from original transitions to their replacements
	 * @param keepNonReplacedMapping
	 *            if true, non replaced transitions are kept in the new mapping
	 */
	public void replaceTransitions(Map<Transition, Transition> configuration, boolean keepNonReplacedMapping);

	public Canceller getCanceller();

	public Marking getInitialMarking();

	public int getNumThreads();

	public void setNumThreads(int threads);

	public void setQueueingModel(QueueingModel model);

	public void setAsynchronousMoveSort(ASynchronousMoveSorting sort);

	public void setType(Type type);

	public void setEpsilon(double epsilon);

	public void setExpectedAlignmentOverrun(double expectedOverrun);

	public QueueingModel getQueueingModel();

	public ASynchronousMoveSorting getAsynchronousMoveSort();

	public Type getType();

	public double getEpsilon();

	public double getExpectedAlignmentOverrun();
}
