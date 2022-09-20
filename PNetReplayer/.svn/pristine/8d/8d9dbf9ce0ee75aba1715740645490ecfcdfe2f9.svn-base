/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.map.TObjectIntMap;

import java.util.List;

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

/**
 * @author aadrians Dec 22, 2011
 * 
 */
public abstract class AllOptAlignmentsTreeThread<H extends Head, T extends Tail> extends AbstractAStarThread<H, T> {

	/**
	 * CPU efficient variant of the Stubborn set implementation
	 * 
	 * @author bfvdonge
	 * 
	 * @param <H>
	 * @param <T>
	 */
	public static class CPUEfficient<H extends Head, T extends Tail> extends AllOptAlignmentsTreeThread<H, T> {

		public CPUEfficient(Delegate<H, T> delegate, TObjectIntMap<H> head2int, List<State<H, T>> stateList,
				H initialHead, Trace trace, int maxStates) throws AStarException {
			super(delegate, trace, maxStates, new JavaCollectionStorageHandler<H, T>(delegate, head2int, stateList) {
				@Override
				public long getIndexOf(H head) {
					return -3;
				}
			});

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
	public static class MemoryEfficient<H extends Head, T extends Tail> extends AllOptAlignmentsTreeThread<H, T> {

		public MemoryEfficient(MemoryEfficientAStarAlgorithm<H, T> algorithm, H initialHead, Trace trace, int maxStates)
				throws AStarException {
			super(algorithm.getDelegate(), trace, maxStates, new MemoryEfficientStorageHandler<H, T>(algorithm) {
				@Override
				public long getIndexOf(H head) {
					return -3;
				}
			});
			initializeQueue(initialHead);
		}
	}

	public AllOptAlignmentsTreeThread(Delegate<H, T> delegate, Trace trace, int maxStates,
			StorageHandler<H, T> storageHandler) {
		super(delegate, trace, maxStates, storageHandler);
		this.sorting = ASynchronousMoveSorting.NONE;
	}

	public void closeObservers() {
		if (observers != null) {
			for (AStarObserver ob : observers) {
				ob.close();
			}
		}
	}

	/**
	 * This method needs to be overridden because the tree state space should
	 * investigate successor states naively, i.e. It does not look at history or
	 * another branch
	 */
	@Override
	public Record getOptimalRecord(Canceller c, int stopAt) throws AStarException {
		State<H, T> state;
		Record rec = null;
		H head = null;
		T tail = null;

		while (!queue.isEmpty() && !c.isCancelled() && queue.peek().getTotalCost() <= stopAt) {
			rec = poll();
			poll++;
			try {
				state = storageHandler.getStoredState(rec);
			} catch (Exception e) {
				throw new AStarException(e);
			}
			head = state.getHead();
			tail = state.getTail();

			// System.out.println(rec.getCostSoFar() + "  " + rec.getTotalCost()
			// + "  " + head + "  "
			// + tail.getEstimatedCosts(delegate, head));
			// System.out.println(states);
			if (poll >= maxStates || rec.getTotalCost() > stopAt) {
				// unreliable, best guess:
				this.reliable = false;
				for (AStarObserver observer : observers) {
					observer.stoppedUnreliablyAt(rec);
				}
				return rec;
			}

			if (head.isFinal(delegate)) {
				this.reliable = true;
				for (AStarObserver observer : observers) {
					observer.finalNodeFound(rec);
				}
				return rec;
			}

			// move model only
			TIntList enabled = head.getModelMoves(rec, delegate);

			TIntCollection nextEvents = rec.getNextEvents(delegate, trace);
			TIntIterator evtIt = nextEvents.iterator();
			int activity = NOMOVE;

			while (evtIt.hasNext()) {
				int nextEvent = evtIt.next();

				TIntList ml = null;

				// move both log and model synchronously;
				activity = trace.get(nextEvent);
				ml = head.getSynchronousMoves(rec, delegate, enabled, activity);
				TIntIterator it = ml.iterator();
				while (it.hasNext()) {
					processMove(head, tail, rec, it.next(), nextEvent, activity);
				}

				// sorting == ASynchronousMoveSorting.LOGMOVEFIRST implies
				// logMove only after initial move, sync move or log move.
				//				if (isValidMoveOnLog(rec, nextEvent, activity, enabled, ml)) {
				// allow move on log only if the previous move was
				// 1) the initial move (rec.getPredecessor() == null
				// 2) a synchronous move
				// 3) a log move.
				processMove(head, tail, rec, NOMOVE, nextEvent, activity);
				//				}
			}

			// sorting == ASynchronousMoveSorting.MODELMOVEFIRST implies
			// modelMove only after initial move, sync move or model move.
			//			if (isValidMoveOnModel(rec, nextEvents, activity, enabled)) {
			// allow move on model only if previous move was:
			// 1) the initial move (rec.getPredecessor() == null
			// 2) a synchronous move
			// 3) a move on model.
			TIntIterator it = enabled.iterator();
			while (it.hasNext()) {
				// move model
				processMove(head, tail, rec, it.next(), NOMOVE, NOMOVE);
			}
			//			}
		}

		this.reliable = false;
		for (AStarObserver observer : observers) {
			observer.stoppedUnreliablyAt(rec);
		}
		return rec;
	}

}
