/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms;

import nl.tue.astar.AStarThread.ASynchronousMoveSorting;
import nl.tue.astar.AStarThread.Canceller;
import nl.tue.astar.AStarThread.QueueingModel;
import nl.tue.astar.AStarThread.Type;

/**
 * @author aadrians Oct 21, 2011
 * 
 */
public abstract class AbstractDefaultPNReplayParam implements IPNReplayParameter {

	protected boolean createConn = false;
	protected boolean guiMode = false;
	protected Canceller canceller;
	private int numThreads = 1;
	private QueueingModel model = QueueingModel.DEPTHFIRSTWITHCERTAINTYPRIORITY;
	private ASynchronousMoveSorting sort = ASynchronousMoveSorting.MODELMOVEFIRST;
	private Type type = Type.PLAIN;
	private double epsilon = 0.0;
	private double expectedAlignmentOverrun = 1.0;

	/**
	 * Return true if connections need to be made after replay is finished
	 */
	public boolean isCreatingConn() {
		return createConn;
	}

	/**
	 * Return true if GUI is used
	 */
	public boolean isGUIMode() {
		return guiMode;
	}

	/**
	 * value is true if later the algorithm is expected to give GUI notification
	 */
	public void setGUIMode(boolean value) {
		this.guiMode = value;
	}

	/**
	 * value is true if the replay result of the algorithm is expected to be
	 * visualized
	 */
	public void setCreateConn(boolean value) {
		this.createConn = value;
	}

	public Canceller getCanceller() {
		return canceller;
	}

	public void setCanceller(Canceller canceller) {
		this.canceller = canceller;
	}

	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}

	public void setQueueingModel(QueueingModel model) {
		this.model = model;
	}

	public void setAsynchronousMoveSort(ASynchronousMoveSorting sort) {
		this.sort = sort;
	}

	public void setType(nl.tue.astar.AStarThread.Type type) {
		this.type = type;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public void setExpectedAlignmentOverrun(double expectedAlignmentOverrun) {
		this.expectedAlignmentOverrun = expectedAlignmentOverrun;
	}

	public QueueingModel getQueueingModel() {
		return this.model;
	}

	public ASynchronousMoveSorting getAsynchronousMoveSort() {
		return this.sort;
	}

	public nl.tue.astar.AStarThread.Type getType() {
		return this.type;
	}

	public double getEpsilon() {
		return this.epsilon;
	}

	public double getExpectedAlignmentOverrun() {
		return this.expectedAlignmentOverrun;
	}
}
