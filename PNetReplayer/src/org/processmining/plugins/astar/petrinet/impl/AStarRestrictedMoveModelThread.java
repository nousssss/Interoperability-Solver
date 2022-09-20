/**
 * 
 */
package org.processmining.plugins.astar.petrinet.impl;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.set.TShortSet;

import java.util.List;

import nl.tue.astar.AStarException;
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
 * @author aadrians Sep 14, 2012
 * 
 */
public abstract class AStarRestrictedMoveModelThread<H extends Head, T extends Tail> extends AbstractAStarThread<H, T> {

	/**
	 * CPU efficient variant of the Stubborn set implementation
	 * 
	 * @author bfvdonge
	 * 
	 * @param <H>
	 * @param <T>
	 */
	public static class CPUEfficient<H extends Head, T extends Tail> extends AStarRestrictedMoveModelThread<H, T> {

		public CPUEfficient(Delegate<H, T> delegate, TObjectIntMap<H> head2int, List<State<H, T>> stateList,
				H initialHead, Trace trace, int maxStates, TShortSet nonSkippedTransitions) throws AStarException {
			super(delegate, trace, maxStates, nonSkippedTransitions, new JavaCollectionStorageHandler<H, T>(delegate,
					head2int, stateList));
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
	public static class MemoryEfficient<H extends Head, T extends Tail> extends AStarRestrictedMoveModelThread<H, T> {

		public MemoryEfficient(MemoryEfficientAStarAlgorithm<H, T> algorithm, H initialHead, Trace trace,
				int maxStates, TShortSet nonSkippedTransitions) throws AStarException {
			super(algorithm.getDelegate(), trace, maxStates, nonSkippedTransitions,
					new MemoryEfficientStorageHandler<H, T>(algorithm));
			initializeQueue(initialHead);
		}
	}

	protected TShortSet nonSkippedTransitions;

	public AStarRestrictedMoveModelThread(Delegate<H, T> delegate, Trace trace, int maxStates,
			TShortSet nonSkippedTransitions, StorageHandler<H, T> storageHandler) {
		super(delegate, trace, maxStates, storageHandler);
		this.nonSkippedTransitions = nonSkippedTransitions;
	}

	@Override
	protected boolean isValidMoveOnModel(Record rec, TIntCollection nextEvents, int activity, TIntList modelMoves) {
		// remove all restricted moves from modelMoves
		TIntIterator it = modelMoves.iterator();
		while (it.hasNext()) {
			if (nonSkippedTransitions.contains((short) it.next())) {
				it.remove();
			}
		}
		return sorting != ASynchronousMoveSorting.MODELMOVEFIRST
				|| (rec.getPredecessor() == null || rec.getModelMove() != NOMOVE);
	}

}
