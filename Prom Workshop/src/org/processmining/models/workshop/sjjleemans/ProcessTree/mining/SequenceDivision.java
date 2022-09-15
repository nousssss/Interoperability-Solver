package org.processmining.models.workshop.sjjleemans.ProcessTree.mining;

import static org.processmining.models.workshop.sjjleemans.Sets.complement;
import static org.processmining.models.workshop.sjjleemans.Sets.difference;
import static org.processmining.models.workshop.sjjleemans.Sets.extend;
import static org.processmining.models.workshop.sjjleemans.Sets.findComponentWith;
import static org.processmining.models.workshop.sjjleemans.Sets.flatten;
import static org.processmining.models.workshop.sjjleemans.Sets.intersection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.deckfour.xes.classification.XEventClass;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.models.workshop.sjjleemans.Pair;

public class SequenceDivision {
	
	//return a set with possible divisions of the activities
	public static Set<Pair<Set<XEventClass>, Set<XEventClass>>> getDivisions(
			DefaultDirectedGraph<Set<XEventClass>, DefaultEdge> condensedGraph,
			Set<Set<XEventClass>> startNodes,
			Set<Set<XEventClass>> endNodes) {
		
		Set<Set<XEventClass>> realNodes = new HashSet<Set<XEventClass>>(condensedGraph.vertexSet());

		//add taus
		Set<Set<XEventClass>> taus = new HashSet<Set<XEventClass>>();
		Set<Set<XEventClass>> startEndNodes = intersection(startNodes, endNodes);
		for (Set<XEventClass> startEndNode : startEndNodes) {
			
			//add a tau before this node
			Set<XEventClass> beforeTau = new HashSet<XEventClass>();
			beforeTau.add(new XEventClass("beforeTau_"+UUID.randomUUID(), -1));
			condensedGraph.addVertex(beforeTau);
			condensedGraph.addEdge(beforeTau, startEndNode);
			startNodes.remove(startEndNode);
			startNodes.add(beforeTau);
			taus.add(beforeTau);
			
			//add a tau after this node
			Set<XEventClass> afterTau = new HashSet<XEventClass>();
			afterTau.add(new XEventClass("afterTau_"+UUID.randomUUID(), -1));
			condensedGraph.addVertex(afterTau);
			condensedGraph.addEdge(startEndNode, afterTau);
			endNodes.remove(startEndNode);
			endNodes.add(afterTau);
			taus.add(afterTau);
		}
		
		
		//find out which nodes are reachable from/to
		//put the result in a graph and compute the connected components
		DirectedGraph<Set<XEventClass>, DefaultEdge> xorGraph = new DefaultDirectedGraph<Set<XEventClass>, DefaultEdge>(DefaultEdge.class);
		for (Set<XEventClass> node : condensedGraph.vertexSet()) {
			//add to the xor graph
			xorGraph.addVertex(node);
		}
		
		for (Set<XEventClass> node : condensedGraph.vertexSet()) {
			Set<Set<XEventClass>> reachableFromTo = walkBack(condensedGraph, node);
			reachableFromTo.addAll(walkForward(condensedGraph, node));
			//debug("reachable from/to {" + implode(node, ",") + "}: " + implode2(reachableFromTo, ", "));
			
			Set<Set<XEventClass>> notReachable = difference(condensedGraph.vertexSet(), reachableFromTo);
			
			//remove the node itself
			notReachable.remove(node);
			debug("not reachable from/to {" + implode(node, ",") + "}: " + implode2(notReachable, ", "));
			
			//add edges to the xor graph
			for (Set<XEventClass> node2 : notReachable) {
				xorGraph.addEdge(node, node2);
			}
		}
		//find the connected components to find the condensed xor nodes
		List<Set<Set<XEventClass>>> xorCondensedNodes = new ConnectivityInspector<Set<XEventClass>, DefaultEdge>(xorGraph).connectedSets();
		for (Set<Set<XEventClass>> se : xorCondensedNodes) {
			debug("xor-free node: " + implode2(se, ", "));
		}
		
		//create the dual condensed graph
		DefaultDirectedGraph<Set<Set<XEventClass>>, DefaultEdge> conconGraph = new DefaultDirectedGraph<Set<Set<XEventClass>>, DefaultEdge>(DefaultEdge.class);
		for (Set<Set<XEventClass>> conconNode : xorCondensedNodes) {
			conconGraph.addVertex(conconNode);
		}
		//add the edges to the dual condensed graph
		for (DefaultEdge edge : condensedGraph.edgeSet()) {
			Set<XEventClass> u = condensedGraph.getEdgeSource(edge);
			Set<Set<XEventClass>> SCCu = findComponentWith(conconGraph.vertexSet(), u);
			Set<XEventClass> v = condensedGraph.getEdgeTarget(edge);
			Set<Set<XEventClass>> SCCv = findComponentWith(conconGraph.vertexSet(), v);
			
			//add an edge if it is not internal
			if (SCCv != SCCu) {
				conconGraph.addEdge(SCCu, SCCv); //this returns null if the edge was already present
			}
		}
		
		//transform the startNodes to the dual condensed graph
		Set<Set<Set<XEventClass>>> condensedStartNodes = new HashSet<Set<Set<XEventClass>>>();
		for (Set<XEventClass> node : startNodes) {
			condensedStartNodes.add(findComponentWith(conconGraph.vertexSet(), node));
		}
		
		
		//add all start nodes
		Set<Set<Set<XEventClass>>> baseCut = new HashSet<Set<Set<XEventClass>>>(condensedStartNodes);
		
		//walk back and add nodes
		Set<Set<Set<XEventClass>>> queue = new HashSet<Set<Set<XEventClass>>>(baseCut);
		for (Set<Set<XEventClass>> node : queue) {
			baseCut.addAll(walkBack(conconGraph, node));
		}
		
		Set<Pair<Set<XEventClass>, Set<XEventClass>>> result = new HashSet<Pair<Set<XEventClass>,Set<XEventClass>>>();
		
		//if this is a valid cut, add it to the result
		Set<XEventClass> baseCutClasses = flatten(difference(flatten(baseCut), taus));
		if (baseCutClasses.size() > 0 && baseCutClasses.size() < flatten(realNodes).size()) {
			result.add(new Pair<Set<XEventClass>, Set<XEventClass>>(
					baseCutClasses, 
					complement(baseCutClasses, flatten(realNodes))));	
		}
		
		//add more cuts by extending the base cut
		for (Set<Set<XEventClass>> node : conconGraph.vertexSet()) {
			if (!baseCut.contains(node)) {
				//this node is not yet in the base cut, we could maybe add it
				Set<Set<Set<XEventClass>>> cut = extend(baseCut, node);
				
				//add all nodes from which there is a path to node
				cut.addAll(walkBack(conconGraph, node));
				
				//check whether it is a valid cut and add it to the result
				Set<XEventClass> cutClasses = flatten(difference(flatten(cut), taus));
				if (cutClasses.size() > 0 && cutClasses.size() < flatten(realNodes).size()) {
					result.add(new Pair<Set<XEventClass>, Set<XEventClass>>(
							cutClasses, 
							complement(cutClasses, flatten(realNodes))));	
				}
			}
		}
		
		//dirty heuristics trick: add all nodes except the outgoing nodes
		Set<Set<XEventClass>> heuristicCut = difference(realNodes, endNodes);
		Set<XEventClass> cutClasses = flatten(difference(heuristicCut, taus));
		if (cutClasses.size() > 0 && cutClasses.size() < flatten(realNodes).size()) {
			result.add(new Pair<Set<XEventClass>, Set<XEventClass>>(
					cutClasses, 
					complement(cutClasses, flatten(realNodes))));	
		}
		
		return result;
	}

	private static <X> Set<X> walkBack(
			DefaultDirectedGraph<X, DefaultEdge> condensedGraph,
			X node) {
		
		Set<X> result = new HashSet<X>();
		
		List<DefaultEdge> queue = new ArrayList<DefaultEdge>();
		queue.addAll(condensedGraph.incomingEdgesOf(node));
		while (queue.size() > 0) {
			DefaultEdge edge = queue.remove(0);
			X source = condensedGraph.getEdgeSource(edge);
			
			result.add(source);
			queue.addAll(condensedGraph.incomingEdgesOf(source));
		}
		
		return result;
	}
	
	private static <X> Set<X> walkForward(
			DefaultDirectedGraph<X, DefaultEdge> condensedGraph,
			X node) {
		
		Set<X> result = new HashSet<X>();
		
		List<DefaultEdge> queue = new ArrayList<DefaultEdge>();
		queue.addAll(condensedGraph.outgoingEdgesOf(node));
		while (queue.size() > 0) {
			DefaultEdge edge = queue.remove(0);
			X source = condensedGraph.getEdgeTarget(edge);
			
			result.add(source);
			queue.addAll(condensedGraph.outgoingEdgesOf(source));
		}
		
		return result;
	}
	
	
	public static void debug(String s) {
		System.out.println(s);
	}
	
	public static String implode(Set<XEventClass> input, String glueString) {
		String output = "";
		boolean first = true;
		if (input.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (XEventClass e : input) {
				if (first) {
					first = false;
				} else {
					sb.append(glueString);
				}
				sb.append(e.toString());
			}
			output = sb.toString();
		}
		return output;
	}
	
	public static String implode2(Set<Set<XEventClass>> input, String glueString) {
		String output = "";
		if (input.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (Set<XEventClass> e : input) {
				sb.append("{");
				sb.append(implode(e, glueString));
				sb.append("}");
			}
			output = sb.toString();
		}
		return output;
	}
}
