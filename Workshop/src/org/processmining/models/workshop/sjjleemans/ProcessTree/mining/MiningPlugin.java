package org.processmining.models.workshop.sjjleemans.ProcessTree.mining;

import static org.processmining.models.workshop.sjjleemans.Sets.findComponentWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.workshop.sjjleemans.Pair;
import org.processmining.models.workshop.sjjleemans.ProcessTreeModelConnection;
import org.processmining.models.workshop.sjjleemans.ProcessTreeModelParameters;
import org.processmining.models.workshop.sjjleemans.ProcessTree.model.Binoperator;
import org.processmining.models.workshop.sjjleemans.ProcessTree.model.EventClass;
import org.processmining.models.workshop.sjjleemans.ProcessTree.model.ExclusiveChoice;
import org.processmining.models.workshop.sjjleemans.ProcessTree.model.Loop;
import org.processmining.models.workshop.sjjleemans.ProcessTree.model.Parallel;
import org.processmining.models.workshop.sjjleemans.ProcessTree.model.ProcessTreeModel;
import org.processmining.models.workshop.sjjleemans.ProcessTree.model.ProcessTreeModel.Operator;
import org.processmining.models.workshop.sjjleemans.ProcessTree.model.Sequential;
import org.processmining.models.workshop.sjjleemans.ProcessTree.model.Tau;

@Plugin(name = "Mine a Process Tree Model", returnLabels = { "Process Tree Model" }, returnTypes = { ProcessTreeModel.class }, parameterLabels = {
		"Log", "Parameters" }, userAccessible = true)
public class MiningPlugin {
	
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree Model, default", requiredParameterLabels = { 0 })
	public ProcessTreeModel mineDefault(PluginContext context, XLog log) {
		return this.mineParameters(context, log, new ProcessTreeModelParameters());
	}
	
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree Model, parameterized", requiredParameterLabels = { 0, 1 })
	public ProcessTreeModel mineParameters(PluginContext context, XLog log, ProcessTreeModelParameters parameters) {
		Collection<ProcessTreeModelConnection> connections;
		try {
			connections = context.getConnectionManager().getConnections(ProcessTreeModelConnection.class, context, log);
			for (ProcessTreeModelConnection connection : connections) {
				if (connection.getObjectWithRole(ProcessTreeModelConnection.LOG).equals(log)
						&& connection.getParameters().equals(parameters)) {
					return connection.getObjectWithRole(ProcessTreeModelConnection.MODEL);
				}
			}
		} catch (ConnectionCannotBeObtained e) {
		}
		ProcessTreeModel model = mine(context, log, parameters);
		context.addConnection(new ProcessTreeModelConnection(log, model, parameters));
		return model;
	}
	
	private ProcessTreeModel mine(PluginContext context, XLog log, ProcessTreeModelParameters parameters) {
		//prepare the log
		Filteredlog filteredLog = new Filteredlog(log, parameters);
		debug(filteredLog.toString());
		
		//create the model
		XLogInfo info = XLogInfoFactory.createLogInfo(log, parameters.getClassifier());
		XEventClasses eventClasses = info.getEventClasses();
		ProcessTreeModel model = new ProcessTreeModel(eventClasses);
		
		//initialise the thread pool
		ThreadPool pool = new ThreadPool(0);
		
		//add a dummy node and mine
		Binoperator node = new Sequential();
		model.root = node;
		mineProcessTree(filteredLog, parameters, node, true, pool);
		model.root = node.getChildLeft();
		
		//wait for all jobs to terminate
		try {
			pool.join();
		} catch (ExecutionException e) {
			e.printStackTrace();
			model.root = null;
		}
		
		return model;
	}
	
	private void mineProcessTree(
			Filteredlog log, 
			final ProcessTreeModelParameters parameters, 
			Binoperator target, //the target where we must store our result 
			Boolean toLeft, //whether we must store our result in the left or right subtree
			final ThreadPool pool) {
		
		debug("");
		debug("==================");
		debug(log.toString());
		
		List<Possibility> possibilities = minePossibilities(log, parameters);
		
		//for now, simply take the first possibility
		if (possibilities.size() > 0) {
			
			//choose a possibility
			final Possibility possibility = possibilities.get(possibilities.size()-1);
			
			debug("chosen " + possibility.toString());
			
			final Binoperator n;
			
			switch (possibility.getOperator()) {
				case EXCLUSIVE_CHOICE :
					n = new ExclusiveChoice();
					break;
				case LOOP :
					n = new Loop();
					break;
				case LOOP_FLOWER :
					n = new Loop();
					break;
				case PARALLEL :
					n = new Parallel();
					break;
				case SEQUENTIAL :
					n = new Sequential();
					break;
				default :
					//single activity
					Iterator<XEventClass> it = possibility.getActivitiesLeft().iterator();
					if (it.hasNext()) {
						if (toLeft) {
							target.setChildLeft(new EventClass(it.next()));
						} else {
							target.setChildRight(new EventClass(it.next()));
						}
						return;
					} else {
						if (toLeft) {
							target.setChildLeft(new Tau());
						} else {
							target.setChildRight(new Tau());
						}
						return;
					}
			}
			
			if (toLeft) {
				target.setChildLeft(n);
			} else {
				target.setChildRight(n);
			}
			
			//start the threads to compute the subtrees
			pool.addJob(
					new Runnable() {
			            public void run() {
			            	mineProcessTree(possibility.getLogLeft(), parameters, n, true, pool);
			            }
			        });
			
			pool.addJob(
					new Runnable() {
			            public void run() {
			            	mineProcessTree(possibility.getLogRight(), parameters, n, false, pool);
			            }
			        });
			
		} else {
			if (toLeft) {
				target.setChildLeft(null);
			} else {
				target.setChildRight(null);
			}
		}
	}
		
	private List<Possibility> minePossibilities(Filteredlog log, ProcessTreeModelParameters parameters) {
		
		//process tree miner code
		//compute the directly-follows relation
		
		//initialise the hashmap and graph to keep track of the directly-follows relation
		Map<XEventClass, Map<XEventClass, Integer>> successionMap = new HashMap<XEventClass, Map<XEventClass, Integer>>();
		for (XEventClass fromEventClass : log.getEventClasses()) {
			Map<XEventClass, Integer> successorMap = new HashMap<XEventClass, Integer>();
			for (XEventClass toEventClass : log.getEventClasses()) {
				successorMap.put(toEventClass, 0);
			}
			successionMap.put(fromEventClass, successorMap);
		}
		DefaultDirectedGraph<XEventClass, DefaultEdge> G = new DefaultDirectedGraph<XEventClass, DefaultEdge>(DefaultEdge.class);
		for (XEventClass fromEventClass : log.getEventClasses()) {
			G.addVertex(fromEventClass);
		}
		
		//initialise variable to catch the empty and single-trace-single-event case
		int longestTrace = 0;
		
		//initialise sets to keep track of start and end activities
		Set<XEventClass> startActivities = new HashSet<XEventClass>();
		Set<XEventClass> endActivities = new HashSet<XEventClass>();
		
		//process the log
		XEventClass secondFromEventClass;
		XEventClass fromEventClass;
		XEventClass toEventClass;
		Set<XEventClass> lengthOneLoops = new HashSet<XEventClass>();
		Set<Pair<XEventClass, XEventClass>> lengthTwoLoops = new HashSet<Pair<XEventClass, XEventClass>>();
		
		boolean tauPresent = false;
		
		log.initIterator();
		while (log.hasNextTrace()) {
			log.nextTrace();
		
			toEventClass = null;
			fromEventClass = null;
			secondFromEventClass = null;
			
			int traceSize = 0;
			
			while(log.hasNextEvent()) {

				secondFromEventClass = fromEventClass;
				fromEventClass = toEventClass;
				toEventClass = log.nextEvent();
				
				traceSize += 1;
				
				if (fromEventClass != null) {
					//add connection to hashmap
					Map<XEventClass, Integer> successorMap = successionMap.get(fromEventClass);
					assert (successorMap != null);
					Integer oldCardinality = successorMap.get(toEventClass);
					successorMap.put(toEventClass, oldCardinality + 1);
					
					//add edge to directly-follows graph
					G.addEdge(fromEventClass, toEventClass);
					
					//check whether we found a witness of a length-one-loop
					if (fromEventClass == toEventClass) {
						lengthOneLoops.add(fromEventClass);
					}
					
					//check whether we found a witness of a length-two-loop
					if (secondFromEventClass != null && secondFromEventClass == toEventClass) {
						lengthTwoLoops.add(new Pair<XEventClass, XEventClass>(secondFromEventClass, fromEventClass));
					}
				} else {
					startActivities.add(toEventClass);
				}
			}
			
			//update the longest-trace-counter
			if (traceSize > longestTrace) {
				longestTrace = traceSize;
			}
			
			if (toEventClass != null) {
				endActivities.add(toEventClass);
			}
			
			if (traceSize == 0) {
				tauPresent = true;
			}
		}
		
		//catch the single event traces
		if (!tauPresent && longestTrace < 2 && log.getEventClasses().size() == 1) {
			List<Possibility> possibilities = new ArrayList<Possibility>();
			Set<XEventClass> s = new HashSet<XEventClass>(log.getEventClasses());
			possibilities.add(new Possibility(Operator.ACTIVITY, s, null, log));
			return possibilities;
		}
		
		
		//catch the empty traces
		//todo: this must never happen
		if (longestTrace == 0) {
			List<Possibility> possibilities = new ArrayList<Possibility>();
			Set<XEventClass> s = new HashSet<XEventClass>();
			possibilities.add(new Possibility(Operator.ACTIVITY, s, null, log));
			return possibilities;
		}
		
		
		//add tau activity if necessary
		if (tauPresent) {
			debug("add tau");
			//G.addVertex(new XEventClass("tau_"+UUID.randomUUID(), -1));
			G.addVertex(new XEventClass("tau", -1));
		}
		
		//compute the strongly connected components of the directly-follows graph G
		StrongConnectivityInspector<XEventClass, DefaultEdge> SCCg = new StrongConnectivityInspector<XEventClass, DefaultEdge>(G);
		List<Set<XEventClass>> SCCs = SCCg.stronglyConnectedSets();
		
		//compute the connected components of the directly-follows graph
		ConnectivityInspector<XEventClass, DefaultEdge> connectedComponentsGraph = new ConnectivityInspector<XEventClass, DefaultEdge>(G);
		List<Set<XEventClass>> connectedComponents = connectedComponentsGraph.connectedSets();
		
		/*
		//provide debug output
		debug += loopsPresent + " witnesses of loops.<br>";
		for (XEventClass c : lengthOneLoops) {
			debug += "lenth-one-loop: " + c.toString() + "<br>";
		}
		for (Pair<XEventClass, XEventClass> p : lengthTwoLoops) {
			debug += "length-two-loop: (" + p.getLeft().toString() + ", " + p.getRight().toString() + ")<br>";
		}
		debug += "strongly connected components: ";
		for (Set<XEventClass> SCC : SCCs) {
			debug += "{" + implode(SCC, ", ") + "} ";
		}
		debug += "<br>";
		debug += "start activities: " + implode(startActivities, ", ") + "<br>";
		debug += "end activities: " + implode(endActivities, ", ") + "<br>";
		debug += "<br>";*/
		
		
		List<Possibility> possibilities = new ArrayList<Possibility>();
		
		//check whether a sequence operator would suit
		if (SCCs.size() > 1) {
			//there is more than one strongly connected component in the directly-follows graph
			
			if (connectedComponents.size() == 1) {
				//the directly follows graph is connected
				
				//the sequence operator would suit.
				
				//first, merge all strongly connected components
				DefaultDirectedGraph<Set<XEventClass>, DefaultEdge> condensedGraph = new DefaultDirectedGraph<Set<XEventClass>, DefaultEdge>(DefaultEdge.class);
				//add vertices (= components)
				for (Set<XEventClass> SCC : SCCs) {
					condensedGraph.addVertex(SCC);
				}
				//add edges
				for (DefaultEdge edge : G.edgeSet()) {
					//find the connected components belonging to these nodes
					XEventClass u = G.getEdgeSource(edge);
					Set<XEventClass> SCCu = findComponentWith(SCCs, u);
					XEventClass v = G.getEdgeTarget(edge);
					Set<XEventClass> SCCv = findComponentWith(SCCs, v);
					
					//add an edge if it is not internal
					if (SCCv != SCCu) {
						condensedGraph.addEdge(SCCu, SCCv); //this returns null if the edge was already present
					}
				}
				
				//compute the list of start and end components
				Set<Set<XEventClass>> startComponents = new HashSet<Set<XEventClass>>();
				for (XEventClass startActivity : startActivities) {
					startComponents.add(findComponentWith(SCCs, startActivity));
				}
				Set<Set<XEventClass>> endComponents = new HashSet<Set<XEventClass>>();
				for (XEventClass endActivity : endActivities) {
					endComponents.add(findComponentWith(SCCs, endActivity));
				}
				
				Set<Pair<Set<XEventClass>, Set<XEventClass>>> divisions = SequenceDivision.getDivisions(condensedGraph, startComponents, endComponents);
				
				assert(divisions.iterator().hasNext());
				
				/*
				debug += "maximum flow " + maximumFlowComputation.getMaximumFlowValue() + "<br>";
				for (DefaultWeightedEdge edge : usedCapacity.keySet()) {
					Set<XEventClass> A = condensedGraph.getEdgeSource(edge);
					Set<XEventClass> B = condensedGraph.getEdgeTarget(edge);
					debug += "flow of " + usedCapacity.get(edge) + "/" + condensedGraph.getEdgeWeight(edge) + " between {" + implode(A, ", ") + "} and {" + implode(B, ", ") + "}<br>";
				}
				*/
				for (Pair<Set<XEventClass>, Set<XEventClass>> pair : divisions) {
					//compute the filtered logs and add the possibility
					possibilities.add(new Possibility(Operator.SEQUENTIAL, pair.getLeft(), pair.getRight(), log));
				}
				
				debug("A sequence operator would suit.");

			} else {
				debug("A sequence operator would not suit as the directly-follows graph is not connected.");
			}
		} else {
			debug("A sequence operator would not suit as the directly-follows graph is a single strongly connected component.");
		}
		
		//check whether an exclusive choice operator would suit
		if (connectedComponents.size() > 1) {
			String debug = "An exclusive choice operator would suit. ";
			for (Set<XEventClass> se : connectedComponents) {
				debug += "{" + implode(se, ", ") + "}";
			}
			debug(debug);
			
			//list the possibilities
			Set<Pair<Set<XEventClass>, Set<XEventClass>>> posspairs = getAllDivisions(connectedComponents);
			for (Pair<Set<XEventClass>, Set<XEventClass>> pair : posspairs) {
				//add the sets to the list of possibilities
				
				//compute the filtered logs and add the possibility
				possibilities.add(new Possibility(Operator.EXCLUSIVE_CHOICE, pair.getLeft(), pair.getRight(), log));

			}
		} else {
			debug("An exclusive choice operator would not suit as the directly-follows graph is connected.");
		}
		
		//check whether a loop operator would suit
		if (SCCs.size() == 1) {
			
			//if (loopsPresent > 0) {
				//find the body and redo part of the loop
				Pair<Set<XEventClass>, Set<XEventClass>> p = getLoopComponents(G , startActivities, endActivities);
				
				
				if (p.getLeft().size() > 0 && p.getRight().size() > 0) {
					debug("A loop operator would suit. {" + implode(p.getLeft(), ", ") + "} {" + implode(p.getRight(), ", ") + "}");
					
					//compute the filtered logs and add the possibility
					possibilities.add(new Possibility(Operator.LOOP, p.getLeft(), p.getRight(), log));
					
				} else {
					debug("A loop operator would suit but the activities cannot be divided over a non-empty body and redo part (todo).");
					
					//apply a temporary fix: apply the flower model
					possibilities.add(new Possibility(Operator.LOOP_FLOWER, p.getLeft(), p.getRight(), log));
				}
			//} else {
			//	debug("A loop operator would not suit as no activity appears twice in a trace.");
			//}
			
		} else {
			debug("A loop operator would not suit as the directly-follows graph consists of multiple strongly connected components.");
		}
		
		//check whether a parallel operator would suit
		if (SCCs.size() == 1) {
			
			//construct the adapted directly-follows graph G'
			DefaultDirectedGraph<XEventClass, DefaultEdge> Gp = new DefaultDirectedGraph<XEventClass, DefaultEdge>(DefaultEdge.class);
			for (XEventClass ec : log.getEventClasses()) {
				Gp.addVertex(ec);
			}
			//add the edges (a, b) for which there is no (b, a)
			for (DefaultEdge e : G.edgeSet()) {
				XEventClass source = G.getEdgeSource(e);
				XEventClass target = G.getEdgeTarget(e);
				if (! G.containsEdge(target, source)) {
					Gp.addEdge(target, source);
				}
			}
			//add the length-two-loops for which there is no length-one-loop
			for (Pair<XEventClass, XEventClass> p : lengthTwoLoops) {
				if (!lengthOneLoops.contains(p.getLeft())) {
					Gp.addEdge(p.getLeft(), p.getRight());
					Gp.addEdge(p.getRight(), p.getLeft());
				}
			}
			
			//compute whether the adapted directly-follows graph G' is connected
			ConnectivityInspector<XEventClass, DefaultEdge> connectedComponentsGraphGp = new ConnectivityInspector<XEventClass, DefaultEdge>(Gp);
			List<Set<XEventClass>> connectedComponentsGp = connectedComponentsGraphGp.connectedSets();
			if (connectedComponentsGp.size() > 1) {
				//a parallel operator would suit
				String debug = "A parallel operator would suit. ";
				for (Set<XEventClass> se : connectedComponentsGp) {
					debug += "{" + implode(se, ", ") + "}";
				}
				debug(debug);
				
				//list the possibilities
				Set<Pair<Set<XEventClass>, Set<XEventClass>>> posspairs = getAllDivisions(connectedComponentsGp);
				for (Pair<Set<XEventClass>, Set<XEventClass>> pair : posspairs) {
					//check whether both these sets have a start and end activity
					if (!Collections.disjoint(pair.getLeft(), startActivities) &&
							!Collections.disjoint(pair.getLeft(), endActivities) &&
							!Collections.disjoint(pair.getRight(), startActivities) &&
							!Collections.disjoint(pair.getRight(), endActivities)) {
					
						//add the sets to the list of possibilities
						
						//compute the filtered logs and add the possibility
						possibilities.add(new Possibility(Operator.PARALLEL, pair.getLeft(), pair.getRight(), log));
						
					}
				}
			} else {
				debug("A parallel operator would not suit as the adapted directly-follows graph is connected.");
			}
			
		} else {
			debug("A parallel operator would not suit as the directly-follows graph consists of multiple strongly connected components.");
		}
		
		return possibilities;
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
	
	
	//return all 2^(n-1) possibilities of combinations of a n-list of sets
	private Set<Pair<Set<XEventClass>, Set<XEventClass>>> getAllDivisions(List<Set<XEventClass>> sets) {
		Set<Pair<Set<XEventClass>, Set<XEventClass>>> returnList = new HashSet<Pair<Set<XEventClass>, Set<XEventClass>>>();
		
		//there are 2^(n-1) possibilities, iterate over all of them
		for (int i=0;i<(int)Math.pow(2, sets.size()-1)-1;i++) {
			Set<XEventClass> se1 = new HashSet<XEventClass>();
			Set<XEventClass> se2 = new HashSet<XEventClass>();
			
			//use the bits of the loop variable to determine which connected components go in which set
			int input = i;
			boolean[] bits = new boolean[sets.size()-1];
			se1.addAll(sets.get(sets.size()-1));
		    for (int j = sets.size()-2; j >= 0; j--) {
		        bits[j] = (input & (1 << j)) != 0;
		        
		        if (bits[j]) {
		        	se1.addAll(sets.get(j));
		        } else {
		        	se2.addAll(sets.get(j));
		        }
		    }
		    
		    //add the sets to the list of possibilities
		    returnList.add(new Pair<Set<XEventClass>, Set<XEventClass>>(se1, se2));
		    
		    //debug += input + " = " + Arrays.toString(bits) + "<br>";
		}
		
		return returnList;
	}
	
	//divide the activities in two sets: the body and the redo part of a loop
	private Pair<Set<XEventClass>,Set<XEventClass>> getLoopComponents(
			DefaultDirectedGraph<XEventClass, DefaultEdge> graph, 
			Set<XEventClass> startActivities, 
			Set<XEventClass> endActivities) {
		
		//initialise the start and end activities as a connected component
		HashMap<XEventClass, Integer> connectedComponents = new HashMap<XEventClass, Integer>();
		for (XEventClass startActivity : startActivities) {
			connectedComponents.put(startActivity, 0);
		}
		for (XEventClass endActivity : endActivities) {
			connectedComponents.put(endActivity, 0);
		}
		
		//find the other connected components
		Integer ccs = 1;
		for (XEventClass node : graph.vertexSet()) {
			if (!connectedComponents.containsKey(node)) {
				connectedComponents.put(node, ccs);
				labelConnectedComponents(graph, node, connectedComponents, ccs);
				ccs += 1;
			}
		}
		
		//initialise the candidates
		Boolean[] candidates = new Boolean[ccs];
		//the start and end activities are no candidates
		candidates[0] = false;
		for (int i=1;i<ccs;i++) {
			candidates[i] = true;
		}
		
		//exclude all candidates that are reachable from the start activities (that are not an end activity)
		for (XEventClass startActivity : startActivities) {
			if (!endActivities.contains(startActivity)) {
				for (DefaultEdge edge : graph.outgoingEdgesOf(startActivity)) {
					candidates[connectedComponents.get(graph.getEdgeTarget(edge))] = false;
				}
			}
		}
		
		//exclude all candidates that can reach an end activity (which is not a start activity)
		for (XEventClass endActivity : endActivities) {
			if (!startActivities.contains(endActivity)) {
				for (DefaultEdge edge : graph.incomingEdgesOf(endActivity)) {
					candidates[connectedComponents.get(graph.getEdgeSource(edge))] = false;
				}
			}
		}
		
		//return two sets of nodes: one for se1, one for se2
		Set<XEventClass> se1 = new HashSet<XEventClass>();
		Set<XEventClass> se2 = new HashSet<XEventClass>();
		//String debug = "";
		for (XEventClass node : graph.vertexSet()) {
			//debug += node.toString() + " in connected component " + connectedComponents.get(node);
			if (candidates[connectedComponents.get(node)]) {
				se2.add(node);
				//debug += ", redo part of loop";
			} else {
				se1.add(node);
				//debug += ", body part of loop";
			}
		}
		return new Pair<Set<XEventClass>, Set<XEventClass>>(se1, se2);
	}
	
	//adds all connected nodes to connectedComponents with value connectedComponent
	private void labelConnectedComponents(
			DefaultDirectedGraph<XEventClass, DefaultEdge> graph,
			XEventClass node, 
			HashMap<XEventClass, Integer> connectedComponents, 
			Integer connectedComponent) {
		if (!connectedComponents.containsKey(node)) {
			connectedComponents.put(node, connectedComponent);
			for (DefaultEdge edge : graph.edgesOf(node)) {
				labelConnectedComponents(graph, graph.getEdgeSource(edge), connectedComponents, connectedComponent);
				labelConnectedComponents(graph, graph.getEdgeTarget(edge), connectedComponents, connectedComponent);
			}
		}
	}
	
	private void debug(String x) {
		System.out.println(x);
	}
}
