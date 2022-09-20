/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import gnu.trove.TIntCollection;
import gnu.trove.list.TIntList;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TLongIntHashMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.tue.astar.AStarException;
import nl.tue.astar.AStarObserver;
import nl.tue.astar.Delegate;
import nl.tue.astar.Head;
import nl.tue.astar.Record;
import nl.tue.astar.Tail;
import nl.tue.astar.Trace;
import nl.tue.astar.impl.AbstractAStarThread;
import nl.tue.astar.impl.JavaCollectionStorageHandler;
import nl.tue.astar.impl.State;
import nl.tue.astar.impl.memefficient.MemoryEfficientAStarAlgorithm;
import nl.tue.astar.impl.memefficient.MemoryEfficientStorageHandler;

import org.processmining.plugins.astar.petrinet.impl.PRecord;

/**
 * @author aadrians Feb 27, 2013
 * 
 */
public abstract class AllOptAlignmentsGraphThread<H extends Head, T extends Tail> extends AbstractAStarThread<H, T> {

	protected final TLongIntMap considered2cost;

	/**
	 * CPU efficient variant of the Stubborn set implementation
	 * 
	 * @author bfvdonge
	 * 
	 * @param <H>
	 * @param <T>
	 */
	public static class CPUEfficient<H extends Head, T extends Tail> extends AllOptAlignmentsGraphThread<H, T> {

		public CPUEfficient(Delegate<H, T> delegate, TObjectIntMap<H> head2int, List<State<H, T>> stateList,
				H initialHead, Trace trace, int maxStates) throws AStarException {
			super(delegate, trace, maxStates, new JavaCollectionStorageHandler<H, T>(delegate, head2int, stateList));

			initializeQueue(initialHead);
		}
	}

	/**
	 * Memory efficient variant of the Stubborn set implementation
	 * 
	 * @author bfvdonge
	 * 
	 * @param <H>
	 * @param <T>
	 */
	public static class MemoryEfficient<H extends Head, T extends Tail> extends AllOptAlignmentsGraphThread<H, T> {

		public MemoryEfficient(MemoryEfficientAStarAlgorithm<H, T> algorithm, H initialHead, Trace trace, int maxStates)
				throws AStarException {
			super(algorithm.getDelegate(), trace, maxStates, new MemoryEfficientStorageHandler<H, T>(algorithm));
			initializeQueue(initialHead);

//			try {
//				addObserver(new DotGraphAStarObserver(File.createTempFile("AStar", ".dot",
//						new File("D:\\temp\\astar\\"))));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

		}
	}

	// mapping from states to other states that have the same suffix 
	protected Map<Record, List<Record>> mapToStatesWSameSuffix = new HashMap<Record, List<Record>>();

	public AllOptAlignmentsGraphThread(Delegate<H, T> delegate, Trace trace, int maxStates,
			StorageHandler<H, T> storageHandler) {
		super(delegate, trace, maxStates, storageHandler);
		this.sorting = ASynchronousMoveSorting.NONE;
		this.considered2cost = new TLongIntHashMap(1000, 0.5f, -2l, -2);

	}

	protected void setConsidered(Record record) {
		super.setConsidered(record);
		int cost = considered2cost.get(record.getState());
		if (cost == -2 || record.getCostSoFar() < cost) {
			considered2cost.put(record.getState(), record.getCostSoFar());
		}
	}

	public void closeObservers() {
		if (observers != null) {
			for (AStarObserver ob : observers) {
				ob.close();
			}
		}
	}

	/**
	 * This is a new method that only exists in this thread. Returns the mapping
	 * from records to other records that is pruned
	 */
	public Map<Record, List<Record>> getMapToStatesWSameSuffix() {
		return this.mapToStatesWSameSuffix;
	}

	/**
	 * This is required such that reordering between move sync and move model is
	 * computed
	 */
	@Override
	protected boolean isValidMoveOnLog(Record rec, int nextEvent, int activity, TIntList modelMoves, TIntList syncMoves) {
		return true;
	}

	/**
	 * This is required such that reordering between move sync and move model is
	 * computed
	 */
	@Override
	protected boolean isValidMoveOnModel(Record rec, TIntCollection nextEvents, int activity, TIntList modelMoves) {
		return true;
	}

	// this method needs to be overridden, because there is mapping from one state to other states with the same
	// suffix
	@SuppressWarnings("unchecked")
	protected void processMove(H head, T tail, Record rec, int modelMove, int movedEvent, int activity)
			throws AStarException {
		// First, construct the next head from the old head
		H newHead = (H) head.getNextHead(rec, delegate, modelMove, movedEvent, activity);
		long index;
		try {
			index = storageHandler.getIndexOf(newHead);
		} catch (Exception e) {
			throw new AStarException(e);
		}

		// create a record for this new head
		//		System.out.println("--------------------------------------");
		final Record newRec = rec.getNextRecord(delegate, trace, newHead, index, modelMove, movedEvent, activity);
		traversedArcCount++;
		//		System.out.println("rec :" + rec.toString() + "...");
		//		System.out.println("newRec :" + newRec.toString() + "...");
		//		System.out.println("index rec next :" + index + "...");

		int c = 0;
		if (!considered2cost.containsKey(index)) {
			//			System.out.print("NOT IN Considered...");
			//			c = queue.contains(newRec);
			Record r = queue.contains(newRec);
			// if r!=null then there is a record in the queue pointing to this
			// state.
			if (r != null) {
				// We reached the state at index before, check the costs and
				// get the estimate from the previous time. Note that we CANNOT
				// get a new estimate from the tail since this would invalidate
				// the
				// ordering relation in the priorityqueue
				c = r.getCostSoFar();
				newRec.setEstimatedRemainingCost(r.getEstimatedRemainingCost(), r.isExactEstimate());
			} else {
				c = -1;
			}

			//			System.out.println("c : " + c);
		} else {
			// The state at index was visited before, hence the estimate is
			// irrelevant
			c = 0;

			//			System.out.print("Considered...");
			newRec.setEstimatedRemainingCost(ESTIMATEIRRELEVANT, true);
			//			System.out.println("remaining for newRec : " + ESTIMATEIRRELEVANT);
		}
		//		System.out.println();
		if (c >= 0 && c <= newRec.getCostSoFar()) {

			// if c==0 then we've considered this state before, i.e. we've already made outgoing arcs
			// from this state and hence the current path must be longer (or equal)

			// Either we visited this state before,
			// or a record with the same state and lower cost exists.
			// this implies that newState was already fully present in the
			// statespace

			assert (index >= 0);

			// edge from oldRec to newRec traversed.
			for (AStarObserver observer : observers) {
				observer.edgeTraversed(rec, newRec);
			}

			//				System.out.println("Rec :" + rec.toString());
			//				System.out.println("NewRec:" + newRec.toString() + " -- cost:" + newRec.getCostSoFar());
			//				System.out.println("c = " + c);

			// check if cost is equal
			// AA:or if previous is equal, because if it is c value is not inserted

			//			if ((c == newRec.getCostSoFar()) || (rec.getCostSoFar() == newRec.getCostSoFar())) {
			if (c == 0 && considered2cost.get(index) == newRec.getCostSoFar()) {

				// We visited this state before and we reached it with the same costSoFar

				// System.out.println("insert to same suffix");
				// insert to same suffix
				List<Record> statesWSameSuffix = mapToStatesWSameSuffix.get(newRec);
				if (statesWSameSuffix == null) {
					statesWSameSuffix = new LinkedList<Record>();
					mapToStatesWSameSuffix.put(newRec, statesWSameSuffix);
				}
				// we only need predecessor states for this record,
				// so other stats may have arbitrary values
				PRecord dumRecord = new PRecord(rec.getState(), c, (PRecord) rec, movedEvent, modelMove, -1,
						((PRecord) rec).getBacktraceSize() + 1, null);
				//					System.out.println("Dummy record : " + dumRecord.toString());
				statesWSameSuffix.add(dumRecord);

			}

			return;
		}

		final T newTail;
		if (index >= 0) {
			// so far, we did the cheap stuff, now get the new tail from
			// storage, knowing that
			// a state exists at index
			int h;
			try {
				// newTail = getStoredTail(tail, index, modelMove, movedEvent,
				// logMove);
				h = storageHandler.getEstimate(newHead, index);
			} catch (Exception e) {
				throw new AStarException(e);
			}

			// newTail.getEstimatedCosts(delegate, newHead);

			newRec.setState(index);
			newRec.setEstimatedRemainingCost(h, true);
			if (rec.getTotalCost() > queue.getMaxCost()) {
				// new record has guaranteed higher cost than the queue's
				// maxcost, this state needs no further investigation.
				setConsidered(rec);
			} else if (queue.add(newRec)) {
				queuedStateCount++;
			}

			// edge from oldRec to newRec traversed.
			for (AStarObserver observer : observers) {
				observer.edgeTraversed(rec, newRec);
			}
			return;

		}

		// the statespace doesn't contain a corresponding state, hence we need
		// to compute the tail.
		newTail = (T) tail.getNextTail(delegate, newHead, modelMove, movedEvent, activity);

		if (!newTail.canComplete()) {
			return;
		}

		int h = newTail.getEstimatedCosts(delegate, newHead);

		// Check if the head is in the store and add if it isn't.
		final State<H, T> newState = new State<H, T>(newHead, newTail);

		try {
			storageHandler.storeStateForRecord(newState, newRec);
		} catch (Exception e) {
			throw new AStarException(e);
		}

		// State<H, T> ret = store.getObject(r.index);
		// if (!ret.equals(newState)) {
		// System.err.println("Retrieval error");
		// }

		// assert (r.isNew);
		newRec.setEstimatedRemainingCost(h, true);
		if (queue.add(newRec)) {
			queuedStateCount++;
		}

		// edge from oldRec to newRec traversed.
		for (AStarObserver observer : observers) {
			observer.edgeTraversed(rec, newRec);
		}

	}
}
