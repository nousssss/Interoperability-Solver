package org.processmining.plugins.alignetc.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;
import org.processmining.plugins.alignetc.core.ReplayAutomatonArc.ArcType;
import org.processmining.plugins.alignetc.result.AlignETCResult;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

/**
 * Automaton of the behavior reflected in the replays of the petri net.
 * This structure is used to compute the "precision" dimension of the 
 * conformance checking between a log and a model.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class ReplayAutomaton {
	
	// AA: commented because codec is not needed anymore
//	private PNCodec codec; // Conection between alignment elements and Transitions
	private ReplayAutomatonNode iniNode; //Initial node of the automaton

	/**
	 * Construct an automaton reflecting the behavior of the alignments
	 * 
	 * @param alignments Aligments between the log cases and the Petri net.
	 * @throws ConnectionCannotBeObtained 
	 */
	public ReplayAutomaton(PluginContext context, PNMatchInstancesRepResult allAlignments, Petrinet net) throws ConnectionCannotBeObtained{//TODO Updated new object
		// AA: commented because codec is not needed anymore
		//Get the connection between Alignment nodes and Transitions
//		codec = (PNCodec) context.getConnectionManager()
//		.getFirstConnection(PNCodecConnection.class, context, net)
//		.getObjectWithRole(PNCodecConnection.PNCODEC);
		
		//Create the initial state
		iniNode = new ReplayAutomatonNode(0);
		
		//For each case (or set of cases with the same alignments)
		for(AllSyncReplayResult caseAlignments : allAlignments){
			
			//Compute the increment of weight assigned to each state in this set
			int nCases = caseAlignments.getCaseIDs().size();
			int nAlign = caseAlignments.getNodeInstanceLst().size();
			float incWeight = (float) nCases / (float) nAlign;
			
			//For each alignment in this set
			Iterator<List<Object>> itAlignments = caseAlignments.getNodeInstanceLst().iterator();
			Iterator<List<StepTypes>> itAlignmentsType = caseAlignments.getStepTypesLst().iterator();
			while(itAlignments.hasNext()){
				
				//Set to the beginning of the automaton
				ReplayAutomatonNode curr = iniNode;
				curr.incWeight(incWeight);
				
				//For each element of the alignment
				Iterator<Object> itTask = itAlignments.next().iterator();
				Iterator<StepTypes> itType = itAlignmentsType.next().iterator();
				while(itTask.hasNext()){
					StepTypes type = itType.next();
					
					//If it is a log move, just skip
					if(type == StepTypes.L){
						itTask.next();//Skip the task
					}
					
					else{ //It is a PetriNet Transition
						// AA: commented because no decoding is necessary anymore
//						Transition t = codec.decode((Short) itTask.next());
						Transition t = ((Transition) itTask.next());
						
						ReplayAutomatonArc arc = curr.getArc(t);
						if(arc != null){//If arc with this transition, go there
							curr = arc.target;
							curr.incWeight(incWeight);
						}
						else{//If no arc with this transition, create new node
							ReplayAutomatonNode target = new ReplayAutomatonNode(incWeight);
							curr.addArc(t,target,ArcType.NORMAL);
							curr = target;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Cut the nodes with weight under a given threshold with respect to its parent.
	 * @param threshold Threshold percentage for the cutting.
	 */
	public void cut(double threshold){
		cutRec(iniNode,threshold);
	}
	
	/**
	 * Recursive function for the automaton cutting.
	 * @param node Current node we are processing.
	 * @param threshold Threshold for the cutting.
	 */
	public void cutRec(ReplayAutomatonNode node, double threshold){
		float weightParent = node.getWeight();
		
		for(Entry<Transition, ReplayAutomatonArc> arc : node.getOutInfo()){
			float weightChild = arc.getValue().target.getWeight();
			
			//If not pass threshold -> CUT
			if((weightParent * threshold) >= weightChild){
				//arc.getValue().target = null; We need to compute Confidence and Severity
				arc.getValue().type = ArcType.CUT;
			}
			//IF pass threshold -> recursive cut
			else{
				cutRec(arc.getValue().target, threshold);
			}
		}
	}
	
	/**
	 * Extend the replay automaton with the Petri net behavior, i.e., for each
	 * of the automaton, we incorporate also the enabled transitions that
	 * has never been fired by any replay. Because the automaton is build from
	 * valid Petri net replays, the set of enabled transitions given a state is
	 * always possible to compute.
	 * 
	 * @param net Petri net used to extend the automaton (originaly the one
	 * used to compute the petri net replays).
	 * @param iniMark Initial marking of the Petri net.
	 * @throws IllegalTransitionException 
	 * 
	 */
	public void extend(Petrinet net, Marking iniMark) throws IllegalTransitionException{
		
		//Create and set a Petrinet semantics
		PetrinetSemantics sem = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);
		Collection<Transition> transAll = net.getTransitions();
		sem.initialize(transAll, iniMark);
		
		extendRec(iniNode,iniMark,sem);
		
	}
	
	public void extendRec(ReplayAutomatonNode node, Marking currMark, PetrinetSemantics sem) throws IllegalTransitionException{

		for(Entry<Transition, ReplayAutomatonArc> entry: node.getOutInfo()){
			ArcType type = entry.getValue().type;
		
			//Only explore the Normal (not CUT or ESCAPING)
			if(type == ArcType.NORMAL){
				//Execute the transition and get the reached marking
				sem.setCurrentState(currMark);
				sem.executeExecutableTransition(entry.getKey());
				Marking reachMark = sem.getCurrentState();
				
				extendRec(entry.getValue().target, reachMark, sem);
			}
		}
		
		//Extend the current node
		extendNode(node,currMark,sem);
	}
	
	public void extendNode(ReplayAutomatonNode node, Marking mark, PetrinetSemantics sem){
		
		//Get the set of enabled transitions according to the model
		sem.setCurrentState(mark);
		Collection<Transition> transModel = sem.getExecutableTransitions();
		
		//Get the set of reflected transitions according to the automaton
		Set<Transition> transAut = node.getOutTransitions();
		
		//By construction of the Automaton transAut subset of transModel
		transModel.removeAll(transAut);
		
		//For each transition enabled by the model but not reflected in the automaton
		for(Transition t : transModel){
			node.addArc(t,null,ArcType.ESCAPING);
		}
	}

	
	public void conformance(AlignETCResult res) {
		//TODO Importance of each transition by the user
		Stack<ReplayAutomatonNode> toExplore = new Stack<ReplayAutomatonNode>();
		toExplore.push(iniNode);
		double top = 0;
		double bot = 0;
		res.nStates++;
		
		while(!toExplore.isEmpty()){
			ReplayAutomatonNode node = toExplore.pop();
			float weight = node.getWeight();
			int refPoints = 0;
			int totalPoints = 0;
			
			//For each child
			for(Entry<Transition, ReplayAutomatonArc> entry: node.getOutInfo()){
				ArcType type = entry.getValue().type;
			
				//Only explore the Normal(not CUT or ESCAPING)
				if(type == ArcType.NORMAL){
					toExplore.push(entry.getValue().target);
					refPoints++;
					res.nStates++;
				}
				else if(type == ArcType.CUT){
					res.nCut++;
				}
				else if(type == ArcType.ESCAPING){
					res.nImprecisions++;
				}
				totalPoints++;
			}
			
			top += (weight * refPoints);
			bot += (weight * totalPoints);
		}
		
		res.apNumerator = top;
		res.apDenominator = bot;
		res.ap = (top/bot);	
	}
	
	
	

}
