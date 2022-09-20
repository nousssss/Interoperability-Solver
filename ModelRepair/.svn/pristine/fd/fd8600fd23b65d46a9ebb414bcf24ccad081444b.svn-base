package org.processmining.modelrepair.plugins.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.modelrepair.plugins.align.AlignmentUtil;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;


/**
 *  Simplified explicit representation of an alignment for model repair.
 *  
 *  
 * @author dfahland
 */
public class SimpleAlignment {
	
	/**
	 * Sequence of moves of this alignment
	 */
	private final Move[] moves; 
	
	/**
	 * How of this sequence occurs in the log
	 */
	private final int frequency;
	
	public SimpleAlignment(Move[] moves, int frequency) {
		this.moves = moves;
		this.frequency = frequency;
	}
	
	public Move[] getMoves() {
		return moves;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	/**
	 * 
	 * @param result
	 *            alignment to be translated
	 * @param nodeMap
	 *            map from Prom's Petri net used for the alignment to the Petri
	 *            net for the simple alignment
	 * @param ecl
	 *            the event classifier used for the alignment
	 * @param onlyUniqueAlignments
	 *            if true returns for each unique trace variant one alignment,
	 *            if false returns as many duplicate sequences as there are in
	 *            the original log underlying the alignment
	 * @return representation of alignment as list of {@link Move} arrays
	 */
    public static List<SimpleAlignment> getAlignment(PNRepResult result, Map<PetrinetNode, hub.top.petrinet.Node> nodeMap, XEventClassifier ecl) {
    	
    	LinkedList<SimpleAlignment> alignment = new LinkedList<>();
    	
		// create traces in the aligned log (each trace is one trace class from the replay)
		for (SyncReplayResult res : result) {
			
			// collect event order as determined by replayer
			// can contain Transition, XEventClass, String objects
			ArrayList<Object> alignedEvents = new ArrayList<Object>();
			for (Object event : res.getNodeInstance()) {
				alignedEvents.add(event);
			}
			
			LinkedList<Move> t = new LinkedList<Move>();
			LinkedList<Move> invis_postponed = new LinkedList<Move>();
			
			// add events to trace
			for (int i=0; i<alignedEvents.size(); i++) {
				
				boolean isInvisible = false;
				boolean isModelStep = false;
				
				// skip log events that cannot be replayed on the model
				if (res.getStepTypes().get(i) == org.processmining.plugins.petrinet.replayresult.StepTypes.LMNOGOOD) continue;
				
				Move to_add;
				
				if (alignedEvents.get(i) instanceof Transition) {
					to_add = new Move((hub.top.petrinet.Transition)nodeMap.get(alignedEvents.get(i)));
				} else if (alignedEvents.get(i) instanceof XEventClass) {
					to_add = new Move( ((XEventClass)alignedEvents.get(i)).getId() );
				} else {
					String qualified_eventName = alignedEvents.get(i).toString();
					String eventName = AlignmentUtil.reformatEventName_legacy(qualified_eventName, ecl);
					to_add = new Move(eventName);
				}
				
				if (res.getStepTypes().get(i) == org.processmining.plugins.petrinet.replayresult.StepTypes.MREAL) {
					isModelStep = true;
					to_add.isSkipStep = true;
				} else if (res.getStepTypes().get(i) == org.processmining.plugins.petrinet.replayresult.StepTypes.MINVI)
					isInvisible = true;
				else if (res.getStepTypes().get(i) == org.processmining.plugins.petrinet.replayresult.StepTypes.LMGOOD)
					isModelStep = true;
				
				

				
				// move invisible steps just before the start of the next visible model step 
				if (isInvisible) {
					invis_postponed.addLast(to_add);
				} else {
					if (isModelStep) {
						for (Move invis : invis_postponed)
							t.addLast(invis);
						invis_postponed.clear();
					}
					t.addLast(to_add);						
				}

			}
			
			// add all final model moves
			for (Move invis : invis_postponed) {
				t.addLast(invis);
			}
		
			Move[] trace = t.toArray(new Move[t.size()]);
			
//			for (Move m : trace) {
//				System.out.print(m);
//			}
//			System.out.println();

			// preserve frequencies of the original log
			int numCases = res.getTraceIndex().size();
			SimpleAlignment sa = new SimpleAlignment(trace, numCases);
			
			// add alignment sequence trace to collection
			alignment.add(sa);
		}
		return alignment;
    }
}
