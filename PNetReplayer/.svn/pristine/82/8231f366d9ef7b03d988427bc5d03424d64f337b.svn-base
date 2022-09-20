package org.processmining.plugins.astar.petrinet.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.tue.astar.Delegate;
import nl.tue.astar.Head;
import nl.tue.astar.Tail;
import nl.tue.astar.impl.memefficient.StorageAwareDelegate;
import nl.tue.astar.impl.memefficient.TailInflater;
import nl.tue.storage.CompressedStore;
import nl.tue.storage.Deflater;

/**
 * Implementation of the tail that implements the estimate equal to the cost of
 * all synchronous moves
 * 
 * @author aadrians Dec 22, 2011
 * 
 */
public class PNaiveTail implements Tail, Deflater<PNaiveTail>, TailInflater<PNaiveTail> {

	public static final PNaiveTail EMPTY = new PNaiveTail();

	private PNaiveTail() {

	}

	public Tail getNextTail(Delegate<? extends Head, ? extends Tail> d, Head oldHead, int modelMove, int logMove,
			int activity) {
		return EMPTY;
	}

	public <S> Tail getNextTailFromStorage(Delegate<? extends Head, ? extends Tail> d, CompressedStore<S> store,
			long index, int modelMove, int logMove, int activity) throws IOException {
		return EMPTY;
	}

	public int getEstimatedCosts(Delegate<? extends Head, ? extends Tail> d, Head head) {
		return ((PHead) head).getParikhVector().getNumElts();
	}

	public boolean canComplete() {
		return true;
	}

	public void deflate(PNaiveTail object, OutputStream stream) throws IOException {
	}

	public PNaiveTail inflate(InputStream stream) throws IOException {
		return EMPTY;
	}

	public int getMaxByteCount() {
		return 0;
	}

	public <H extends Head> int inflateEstimate(StorageAwareDelegate<H, PNaiveTail> delegate, H head, InputStream stream)
			throws IOException {
		return getEstimatedCosts(delegate, head);
	}

}
