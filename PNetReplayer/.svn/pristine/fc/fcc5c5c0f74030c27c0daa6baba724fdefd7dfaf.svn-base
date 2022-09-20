/**
 * 
 */
package org.processmining.plugins.astar.petrinet.impl;

import java.io.IOException;
import java.io.InputStream;

import nl.tue.astar.impl.DijkstraTail;
import nl.tue.astar.util.ShortShortMultiset;
import nl.tue.storage.compressor.BitMask;

/**
 * @author aadrians
 * Mar 11, 2012
 *
 */
public class PHeadUniqueDijkstraCompressor extends PHeadCompressor<DijkstraTail>{

	public PHeadUniqueDijkstraCompressor(short places, short activities) {
		super(places, activities);
	}
	
	public PHead inflate(InputStream stream) throws IOException {
		// skip the hashCode
		int hashCode = readIntFromStream(stream);
		// read the marking
		BitMask mask1 = readMask(stream, places, bytesPlaces);
		ShortShortMultiset marking = inflateContent(stream, BitMask.getIndices(mask1), places);
		// read the vector
		BitMask mask2 = readMask(stream, activities, bytesActivities);
		ShortShortMultiset parikh = inflateContent(stream, BitMask.getIndices(mask2), activities);
		return new PHeadUnique(marking, parikh, hashCode);
	}

}