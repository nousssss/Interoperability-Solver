/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express;

/**
 * @author aadrians
 * May 31, 2013
 *
 */
public class AllKnotSamplingOptAlignmentsGraphCountAlg extends AllKnotSamplingOptAlignmentsGraphAlg{
	public String toString() {
		return "Knot-based graph-based state space replay to obtain optimal alignment representatives of level-1 (represented alignments are counted)";
	}

	public String getHTMLInfo() {
		return "<html>Returns representative alignments using graph-based state space. <br/>"
				+ "Assuming that the model does not allow loop/infinite firing sequences of cost 0. <br/>"
				+ "Reordering of sync moves is taken into account. <br/>"
				+ "NOTE: This algorithm is computationally expensive in comparison to the one without counting represented alignments" + "</html>";
	};
	
	@Override
	protected boolean isRepresentedCounted() {
		return true;
	}
}
