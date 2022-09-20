/**
 * 
 */
package org.processmining.plugins.astar.petrinet.impl;

import org.deckfour.xes.model.XTrace;
import org.processmining.models.semantics.petrinet.Marking;

import nl.tue.astar.util.ShortShortMultiset;


/**
 * @author aadrians
 * Mar 10, 2012
 *
 */
public class PHeadUnique extends PHead{
	public static int hashStat = 0;

	public PHeadUnique(ShortShortMultiset marking, ShortShortMultiset parikh, int hashCode) {
		super(marking, parikh, hashStat++);
	}

	public PHeadUnique(AbstractPDelegate<?> delegate, Marking m, XTrace t) {
		super(delegate, m, t);
	}
	
	@Override
	public boolean equals(Object o) {
		return false;
	}
	
	@Override
	protected PHead createHead(ShortShortMultiset marking, ShortShortMultiset parikh, int hashCode) {
		return new PHeadUnique(marking, parikh, hashStat++);
	}

//	@Override
//	public PHeadUnique getNextHead(Record rec, Delegate<? extends Head, ? extends Tail> d, int modelMove, int logMove,
//			int activity) {
//		AbstractPDelegate<?> delegate = (AbstractPDelegate<?>) d;
//
//		final ShortShortMultiset newMarking;
//		if (modelMove != AStarThread.NOMOVE) {
//			newMarking = cloneAndUpdateMarking(delegate, marking, (short) modelMove);
//		} else {
//			newMarking = marking;
//		}
//
//		final ShortShortMultiset newParikh;
//		if (logMove != AStarThread.NOMOVE) {
//			newParikh = parikh.clone();
//			newParikh.adjustValue((short) activity, (short) -1);
//		} else {
//			newParikh = parikh;
//		}
//
//		return new PHeadUnique(newMarking, newParikh, hashStat++);//, hash.hash, bitsForParikh);
//	}
}
