package org.processmining.plugins.multietc.automaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;
import org.processmining.plugins.multietc.reflected.ReflectedLog;
import org.processmining.plugins.multietc.reflected.ReflectedTrace;
import org.processmining.plugins.multietc.res.MultiETCResult;
import org.processmining.plugins.multietc.sett.MultiETCSettings;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Precision automaton used to determine the precision between a log and a model.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class Automaton {

	/** Factory that creates the node */
	private AutomatonFactory factory;
	/** Graph representing the automaton */
	private Graph<AutomatonNode, AutomatonEdge> g;
	/** Mapping with the existing nodes in the automaton. Note that two different instances
	 *  of AutomatonNode could be considered to represent the same state, i.e., equal = true. */
	private Map<AutomatonNode, AutomatonNode> nodes;
	
	/**
	 * Create a new automaton. Note: use the AutomatonFactory instead, to create automatons
	 * @param f Factory that creates the automaton.
	 */
	public Automaton (AutomatonFactory f){
		factory = f;
		g = new DirectedSparseMultigraph<AutomatonNode, AutomatonEdge>();
		nodes = new HashMap<AutomatonNode,AutomatonNode>();
	}
	
	
	/**
	 * Check the conformance of an automaton. The automaton is build according to the provided log,
	 * extended according to the provided petri net and initial marking, and the results are
	 * set in the result object.
	 * @param log Log reflecting the behavior to be considered for this precision automaton.
	 * @param net Petri net.
	 * @param iniM Initial marking of the Petri net.
	 * @param res Oject to store the results of the conformance checking.
	 * @param etcSett 
	 */
	@SuppressWarnings("unchecked")
	public void checkConformance(ReflectedLog log, Petrinet net, Marking iniM, Marking endM, MultiETCResult res, MultiETCSettings etcSett) {
		
		Object[] toUse = getNetToUse(net, iniM, endM);
		Petrinet netToUse = (Petrinet) toUse[0];
		Marking markToUse = (Marking) toUse[1];
		Map<PetrinetNode, PetrinetNode> orig2new = (Map<PetrinetNode, PetrinetNode>) toUse[2];
		Map<PetrinetNode, PetrinetNode> new2orig = (Map<PetrinetNode, PetrinetNode>) toUse[3];		
		
		constructAutomaton(log);
		computeMarkings(netToUse, markToUse, orig2new, new2orig);
		computeAvailableTasks(netToUse, markToUse, orig2new, new2orig);
		computePrecision(res, etcSett);
	}




	/**
	 * Construct the automaton with the information reflected in the given log.
	 * @param log Log of traces of Petri net transitions.
	 */
	public void constructAutomaton(ReflectedLog log) {	
		for( ReflectedTrace trace: log){
			double traceWeight = trace.getWeight();
			
			//Deal with the initial node
			AutomatonNode ini = getOrCreateNode(trace.subList(0, 0), trace);
			ini.setWeight(ini.getWeight() + traceWeight);
			
			//Iterate for the rest of the Trace
			for(int i=0; i < trace.size(); i++){
				AutomatonNode s = getOrCreateNode(trace.subList(0, i), trace.subList(i, trace.size()));
				AutomatonNode t = getOrCreateNode(trace.subList(0, i+1), trace.subList(i+1, trace.size()));
				getOrCreateEdge(s,t,trace.get(i));
				t.setWeight(t.getWeight() + traceWeight);
			}
		}
		
	}
	
	/**
	 * Creates a node of the graph, or get if it it already exists.
	 * @param past The past sequence of transitions that define the current state of the node to create.
	 * @param future The future sequence of transitions that define the current state of the node to create.
	 * @return The created Node (if it does not exists), or the node (if it already exist).
	 */
	private AutomatonNode getOrCreateNode(List<Transition> past, List<Transition> future){
		AutomatonNode node = factory.createNode(past, future);
		if(nodes.containsKey(node)){
			//The Node already exists in the Graph
			node = nodes.get(node);
		}
		else{
			//The Node does not exists in the Graph
			g.addVertex(node);
			nodes.put(node, node);
		}
		return node;
	}
	
	/**
	 * Get the edge (or create if it does not exist) from the source to the target node, with
	 * the given transition.
	 * @param s Source node of the edge.
	 * @param t target node of the edge.
	 * @param trans Transition of the edge (label).
	 * @return Return the (existent or created) edge from s to t with label transition.
	 */
	private AutomatonEdge getOrCreateEdge(AutomatonNode s, AutomatonNode t, Transition trans){
		AutomatonEdge edge = null;
		
		//Check if the edge exists
		for(AutomatonEdge e : g.findEdgeSet(s, t)){
			if(e.getTransition() == trans){
				edge = e;
			}
		}
		//The Edge do not exists -> Create it
		if(edge == null){
			g.addEdge(new AutomatonEdge(s,t,trans), s, t, EdgeType.DIRECTED);
		}
		return edge;
	}
	
	
	private Object[] getNetToUse(Petrinet net, Marking iniM, Marking endM){
		Petrinet netToUse = null;
		Marking markingToUse = null;
		Map<PetrinetNode, PetrinetNode> orig2new = new HashMap<PetrinetNode,PetrinetNode>();
		Map<PetrinetNode, PetrinetNode> new2orig = new HashMap<PetrinetNode,PetrinetNode>();
		
		
		if(this.factory.getSett().getWindow() == MultiETCSettings.Window.BACKWARDS){
			netToUse = net;
			markingToUse = iniM;
			for(PetrinetNode node: net.getNodes()){
				orig2new.put(node, node);
				new2orig.put(node, node);
			}
			
		}
		else if (this.factory.getSett().getWindow() == MultiETCSettings.Window.FORWARDS){
			//When Future, the marking it set differently: reversing the petri net,
			//taking the final marking as initial state, and if in the node the order matters,
			//it is reproduced from the end to the start.
			
			
			//REVERSE PETRINET
			//Transitions
			netToUse = PetrinetFactory.newPetrinet("Reversed "+net.getLabel());
			for(Transition netT: net.getTransitions()){
				Transition revT = netToUse.addTransition(netT.getLabel());
				orig2new.put(netT, revT);
				new2orig.put(revT, netT);
			}
			//Places
			for(Place netP: net.getPlaces()){
				Place revP = netToUse.addPlace(netP.getLabel());
				orig2new.put(netP, revP);
				new2orig.put(revP, netP);
			}
			//Get the Arcs
			Set<Arc> arcs = new HashSet<Arc>();
			for(PetrinetNode source: net.getNodes()){
				for(PetrinetNode target: net.getNodes()){
					if(net.getArc(source, target) != null) arcs.add(net.getArc(source, target));
				}
			}
			//Arcs
			for(Arc netA: arcs){
				if(netA.getSource() instanceof Place){
					netToUse.addArc((Transition)orig2new.get(netA.getTarget()), (Place) orig2new.get(netA.getSource()), netA.getWeight());
				}
				else{
					netToUse.addArc((Place) orig2new.get(netA.getTarget()), (Transition) orig2new.get(netA.getSource()), netA.getWeight());
				}
			}
			
			//MARKING
			markingToUse = new Marking();
			for(Place p: endM){
				markingToUse.add((Place) orig2new.get(p));
			}
		}
		
		return new Object[] {netToUse, markingToUse, orig2new, new2orig};
	}
	
	/**
	 * Compute and set the marking of all nodes in the automaton according to the transitions
	 * executed to reach them.
	 * @param net Petri net.
	 * @param iniM Initial state of the net.
	 */
	private void computeMarkings(Petrinet netToUse, Marking markingToUse, Map<PetrinetNode, PetrinetNode> orig2new, Map<PetrinetNode, PetrinetNode> new2orig) {
		
		for(AutomatonNode n: g.getVertices()){
			n.computeMarking(netToUse, markingToUse, orig2new, new2orig);
		}
	
	}

	/**
	 * Compute and set the available tasks represented in each node.
	 * @param net Petri net.
	 * @param iniM Initial state of the net.
	 */
	private void computeAvailableTasks(Petrinet netToUse, Marking markToUse, Map<PetrinetNode, PetrinetNode> orig2new, Map<PetrinetNode, PetrinetNode> new2orig){
		PetrinetSemantics sem = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);
		sem.initialize(netToUse.getTransitions(), markToUse);
		
		for(AutomatonNode n: g.getVertices()){
			if(n.getMarking() != null){
				
				Marking markingOfState = new Marking();
				for(Place p: n.getMarking()){
					markingOfState.add((Place) orig2new.get(p));
				}
				
				sem.setCurrentState(markingOfState);
				
				Set<Transition> executedTasksOrig = new HashSet<Transition>();
				for(Transition t: sem.getExecutableTransitions()){
					executedTasksOrig.add((Transition) new2orig.get(t));
				}
				n.setAvailableTasks(executedTasksOrig);
			}
		}
	}
	
	/**
	 * Compute and set the Precision metric value based on computing the number of escaping points.
	 * @param res The result where to store the preicion value computed.
	 * @param etcSett 
	 */
	private void computePrecision(MultiETCResult res, MultiETCSettings etcSett) {
		double num = 0;
		double den = 0;
		
		for( AutomatonNode n: g.getVertices()){
			if(n.getMarking() != null){
				double weight = n.getWeight();
				Set<Transition> reflected = new HashSet<Transition>();
				
				if(etcSett.getWindow() == MultiETCSettings.Window.BACKWARDS){
					for(AutomatonEdge e: g.getOutEdges(n)){
						reflected.add(e.getTransition());
					}
				}
				else if (etcSett.getWindow() == MultiETCSettings.Window.FORWARDS){
					for(AutomatonEdge e: g.getInEdges(n)){
						reflected.add(e.getTransition());
					}
				}
				
				Set<Transition> escaping = new HashSet<Transition>(n.getAvailableTasks());
				escaping.removeAll(reflected);
				
				num += (weight * (n.getAvailableTasks().size() - escaping.size()));
				den += (weight *  n.getAvailableTasks().size());
				
				n.putAttribute(AutomatonNode.ESCAPING_TASKS, escaping);
				
				n.putAttribute(AutomatonNode.NUM_AVAIL_TASKS, n.getAvailableTasks().size());
				n.putAttribute(AutomatonNode.NUM_ESCAPING_TASKS, escaping.size());
				n.putAttribute(AutomatonNode.NUM_NON_ESCAPING_TASKS, n.getAvailableTasks().size() - escaping.size());
			}
			
		}
		
		res.putAttribute(MultiETCResult.PRECISION, num/den);
	}

	/**
	 * Return a JUNG Library Graph representation of the automaton.
	 * @return JUNG Graph representation of the automaton.
	 */
	public Graph<AutomatonNode, AutomatonEdge> getJUNG() {
		return g;
	}
	
	
}
