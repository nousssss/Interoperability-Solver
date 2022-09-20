package org.processmining.plugins.astar.petrinet.impl;

import gnu.trove.iterator.TShortIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.TShortList;
import gnu.trove.list.array.TIntArrayList;
import nl.tue.astar.AStarThread;
import nl.tue.astar.Delegate;
import nl.tue.astar.Head;
import nl.tue.astar.Record;
import nl.tue.astar.Tail;
import nl.tue.astar.util.ShortShortMultiset;
import nl.tue.storage.hashing.HashCodeProvider;
import nl.tue.storage.hashing.impl.MurMur3HashCodeProvider;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * The head is basically two vectors, one for the marking and one for the parikh
 * vector.
 * 
 * @author bfvdonge
 * 
 */
public class PHead implements Head {

	public static HashCodeProvider PROVIDER = new MurMur3HashCodeProvider();

	// The maximum size of this.super:             16
	protected final ShortShortMultiset marking; //    8 + 2 * numplaces + 48 
	protected final ShortShortMultiset parikh; //     8 + 2 * numactivities + 48
	protected int hashCode; //                  4  

	public static int computeBitsForParikh(short acts, short places) {
		return Math.max(4, (int) (32.0 * acts / (acts + 2 * places)));
	}

	public PHead(AbstractPDelegate<?> delegate, Marking m, XTrace t) {
		marking = new ShortShortMultiset(delegate.numPlaces());
		parikh = new ShortShortMultiset(delegate.numEventClasses());

		for (Place p : m.baseSet()) {
			short i = delegate.getIndexOf(p);
			marking.put(i, m.occurrences(p).shortValue());
		}
		for (XEvent e : t) {
			XEventClass c = delegate.getClassOf(e);
			if (c != null) {
				short key = delegate.getIndexOf(c);
				if (key >= 0) {
					parikh.adjustValue(key, (short) 1);
				}
			}
		}
		hashCode = PROVIDER.hash(marking, parikh);
	}

	protected PHead(ShortShortMultiset marking, ShortShortMultiset parikh, int hashCode) {
		this.marking = marking;
		this.parikh = parikh;
		this.hashCode = hashCode;
	}

	protected PHead createHead(ShortShortMultiset marking, ShortShortMultiset parikh, int hashCode) {
		return new PHead(marking, parikh, hashCode);
	}

	public PHead getNextHead(Record rec, Delegate<? extends Head, ? extends Tail> d, int modelMove, int logMove,
			int activity) {
		AbstractPDelegate<?> delegate = (AbstractPDelegate<?>) d;

		final ShortShortMultiset newMarking;
		if (modelMove != AStarThread.NOMOVE) {
			newMarking = cloneAndUpdateMarking(delegate, marking, (short) modelMove);
		} else {
			newMarking = marking;
		}

		final ShortShortMultiset newParikh;
		if (logMove != AStarThread.NOMOVE) {
			newParikh = parikh.clone();
			newParikh.adjustValue((short) activity, (short) -1);
		} else {
			newParikh = parikh;
		}

		return createHead(newMarking, newParikh, PROVIDER.hash(newMarking, newParikh));
	}

	protected ShortShortMultiset cloneAndUpdateMarking(AbstractPDelegate<?> delegate, ShortShortMultiset marking,
			short modelMove) {
		ShortShortMultiset newMarking = marking.clone();
		// clone the marking
		short[] in = delegate.getInputOf(modelMove);
		short[] out = delegate.getOutputOf(modelMove);

		for (short place = delegate.numPlaces(); place-- > 0;) {
			short val = newMarking.get(place);
			short needed = in[place];
			if (needed != AbstractPDelegate.INHIBITED) {
				// only adjust the value for non-inhibitor arcs
				newMarking.adjustValue(place, (short) -needed);
			}
		}

		for (short place = delegate.numPlaces(); place-- > 0;) {
			short val = newMarking.get(place);
			short produced = out[place];
			if (produced < 0) {
				// combination or reset arc and regular arc (regular arc may be 0)
				// first get the actual produced tokens
				produced = (short) (-(produced + 1));
				// then account for removing all tokens first
				produced -= val;
			}
			newMarking.adjustValue(place, produced);
		}
		return newMarking;
	}

	public TIntList getModelMoves(Record rec, Delegate<? extends Head, ? extends Tail> d) {
		return ((AbstractPDelegate<?>) d).getEnabledTransitionsChangingMarking(marking);
	}

	public TIntList getSynchronousMoves(Record rec, Delegate<? extends Head, ? extends Tail> d, TIntList enabled,
			int activity) {
		final AbstractPDelegate<?> delegate = (AbstractPDelegate<?>) d;

		// only consider transitions mapped to activity
		final TIntList result = new TIntArrayList();
		TShortList trans = delegate.getTransitions((short) activity);
		TShortIterator it = trans.iterator();
		while (it.hasNext()) {
			int i = it.next();
			if (delegate.isEnabled(i, marking)) {
				result.add(i);
			}
		}

		return result;

	}

	public boolean isFinal(Delegate<? extends Head, ? extends Tail> d) {
		AbstractPDelegate<?> delegate = (AbstractPDelegate<?>) d;
		return parikh.isEmpty() && delegate.isFinal(marking);
	}

	protected Marking fromMultiSet(AbstractPDelegate<?> delegate) {
		Marking m = new Marking();
		for (short i = 0; i < delegate.numPlaces(); i++) {
			if (marking.get(i) > 0) {
				m.add(delegate.getPlace(i), (int) marking.get(i));
			}
		}
		return m;
	}

	public ShortShortMultiset getMarking() {
		return marking;
	}

	public ShortShortMultiset getParikhVector() {
		return parikh;
	}

	public int hashCode() {
		return hashCode;
	}

	public boolean equals(Object o) {
		return (o != null) && (o instanceof PHead) && (((PHead) o).marking.equals(marking))
				&& (((PHead) o).parikh.equals(parikh));
	}

	public String toString() {
		return "[m:" + marking + "<BR/>p:" + parikh + "]";
	}

	protected static final class HashContainer {

		public HashContainer(int hashCode) {
			hash = hashCode;
		}

		public int hash;
	}

}
