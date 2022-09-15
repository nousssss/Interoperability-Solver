/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/
package org.processmining.plugins.etconformance.data;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.packages.PackageManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.CoverabilityGraph;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.CTMarking;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;
import org.processmining.plugins.connectionfactories.logpetrinet.EvClassLogPetrinetConnectionFactoryUI;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.etconformance.ETCException;
import org.processmining.plugins.etconformance.ETCResults;
import org.processmining.plugins.etconformance.util.InvisiblePetrinetSemantics;
import org.processmining.plugins.etconformance.util.MDTUtils;
import org.processmining.plugins.petrinet.behavioralanalysis.CGGenerator;

import edu.uci.ics.jung.graph.DelegateTree;

/**
 * Class representing a prefix automaton of a log.
 * Eventually, this prefix could be enriched with 
 * model information.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class PrefixAutomaton {
	
	/** Attribute's name of the Number of Similar Instances of Aggregated Logs */
	private static final String NUM_SIMILAR_INSTANCES = "numSimilarInstances";
	
	/** Tree of the prefix automaton */
	DelegateTree<PrefixAutomatonNode,PrefixAutomatonEdge> tree;
	/** Imprecisions of the system */
	Imprecisions imprecisions;
	
	
	/**
	 * Constructor of a prefix automaton of the given log.
	 * @param log Log used to construct the prefix automaton.
	 * @param mapping 
	 */
	public PrefixAutomaton(XLog log, ETCResults res, TransEvClassMapping mapping){
		//1- Create a new empty tree and log info
		tree = new DelegateTree<PrefixAutomatonNode,PrefixAutomatonEdge>();
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, mapping.getEventClassifier());
		
		//2- Create a root node and a current and next pointer to walk through the tree
		PrefixAutomatonNode root = new PrefixAutomatonNode(0);
		tree.addVertex(root);
		PrefixAutomatonNode current, next;
		
		//3-Create counter for statistics about the log
		int nTraces = 0;
		long nSizeTraces = 0;
		res.setNTasks(logInfo.getEventClasses().size());
		
		
		//4 - Walk through the log and fill the prefix tree
		for(XTrace trace : log){
			
			//Get the number of instances of that trace (1 if it is not set)
			XAttribute numInstAt = trace.getAttributes().get(NUM_SIMILAR_INSTANCES);
			int numInst = 1;
			if (numInstAt != null) {
				numInst = Integer.parseInt(numInstAt.toString());
			}
			nTraces += numInst;
			nSizeTraces += (trace.size()*numInst);
			
			//For each new trace, set current node to root and increment 
			//root instances
			root.incIntances(numInst);
			current = root;

			// Walk through the trace
			for(XEvent event : trace){			
				//Get the event label of the log
				XEventClass label = logInfo.getEventClasses().getClassOf(event);
				 			
				//Check if there is a edge with that label.
				next = null;
				Collection<PrefixAutomatonEdge> edges = tree.getChildEdges(current);
				Iterator<PrefixAutomatonEdge> itEdge = edges.iterator();
				while(itEdge.hasNext() && next == null){
					PrefixAutomatonEdge edge = itEdge.next();
					if(edge.getEvent() == label){
						//If there is the edge, take it
						next = tree.getOpposite(current, edge);
					}
				}
				
				//If there is no node with this task, we create the node and the edge
				if(next == null){ 
					next = new PrefixAutomatonNode(0);
					tree.addChild(new PrefixAutomatonEdge(label), current, next);
				}
				
				//Increment the number of instances of the next node
				next.incIntances(numInst);
				current = next;
			}
			
		}
		
		//Compute the number of traces and the average length of the traces
		res.setNTraces(nTraces);
		res.setAveSizeTraces(Math.round((double)nSizeTraces/(double)nTraces));
	}
	
	
	/**
	 * Enrich/Extend the Prefix Automaton with the behavior of the model (a Petrinet).
	 * @param net The model used to enrich the automaton.
	 * @param iniMark Initial marking of the Petrinet.
	 * @param mapping Connection with the relation between transitions in the 
	 * Petrinet and events in the log.
	 * @throws IllegalTransitionException 
	 */
	public void enrich(PluginContext context, Petrinet net, Marking iniMark, TransEvClassMapping mapping, ETCResults res) throws Exception{
		//Create and set a Petrinet semantics
		PetrinetSemantics sem = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);
		Collection<Transition> transAll = net.getTransitions();
		sem.initialize(transAll, iniMark);
		
		//Start enriching
		enrichRec(context, net,iniMark,mapping,tree.getRoot(),sem, res);
		
	}
	
	/**
	 * Enrich/Extend the subtree from the given node with the behavior of the model. 
	 * @param net Model (Petri Net).
	 * @param marking Petri net marking corresponding with the node.
	 * @param mapping Conection between Petri net transitions an Log events.
	 * @param node Node root of the subtree to enrich/extend.
	 * @param sem Semantincs of a Petri net to obtain the enable transitions.
	 * @throws Exception
	 */
	public void enrichRec(PluginContext context, Petrinet net, Marking marking, 
			TransEvClassMapping mapping, PrefixAutomatonNode node, 
			PetrinetSemantics sem, ETCResults res) throws Exception {
		
		//Get the enable transitions for the marking of the given node.
		MarksTasks enableTasks = computeEnableTasks(context, net, marking, mapping, sem, res);

		
		//For each child edge of Node 
		for (PrefixAutomatonEdge edge :tree.getChildEdges(node)){
			XEventClass task = edge.getEvent();
			PrefixAutomatonNode childNode = tree.getOpposite(node, edge);
			int ix = enableTasks.tasks.indexOf(task);
			
			//Check if the task is in the available tasks
			if(ix == -1){
				//NO FITNESS
				childNode.setType(PrefixAutomatonNodeType.NON_FIT);
				res.setNonFitStates(res.getNonFitStates()+1);
				setNonFitSubtree(childNode, res);
				res.setnNonFitTraces(res.getnNonFitTraces()+childNode.getInstances());
				
				//throw new InvalidParameterException("NO FITNESS: The net must cover the log" +
				//		"Marking: "+marking.toString()+"  Task: "+task.toString());
			}
			else if(ix != -1 && enableTasks.marks.get(ix) == null){
				//INDETERMINISM
				childNode.setType(PrefixAutomatonNodeType.NON_DET);
				res.setNonDetStates(res.getNonDetStates()+1);
				setNonDetSubtree(childNode, res);
				res.setnNonDetTraces(res.getnNonDetTraces()+childNode.getInstances());
			}
			else{
				//Follow the extension of the automaton for the child
				enrichRec(context, net,enableTasks.marks.get(ix),mapping,childNode,sem, res);
				
				//Remove this tasks from the available tasks
				enableTasks.marks.remove(ix);
				enableTasks.tasks.remove(ix);
			}
			

		}
		
		//For the remaining available tasks, create the extended nodes
		for(XEventClass enrichedTask : enableTasks.tasks){
			PrefixAutomatonNode enrichedNode = new PrefixAutomatonNode(0);
			tree.addChild(new PrefixAutomatonEdge(enrichedTask), node, enrichedNode);
		}
	}
	
	/**
	 * Set all the subtree of the node as NON_FIT nodes
	 * @param node Initial node of the subtree that falls in the NON fitting part.
	 */
	public void setNonFitSubtree(PrefixAutomatonNode node, ETCResults res){
		for(PrefixAutomatonNode child : tree.getChildren(node)){
			child.setType(PrefixAutomatonNodeType.NON_FIT);
			setNonFitSubtree(child, res);
			res.setNonFitStates(res.getNonFitStates()+1);
		}
	}
	
	/**
	 * Set all the subtree of the node as NON_DET nodes
	 * @param node Initial node of the subtree that falls in the NON deterministic part.
	 */
	public void setNonDetSubtree(PrefixAutomatonNode node, ETCResults res){
		for(PrefixAutomatonNode child : tree.getChildren(node)){
			child.setType(PrefixAutomatonNodeType.NON_DET);
			setNonDetSubtree(child, res);
			res.setNonDetStates(res.getNonDetStates()+1);
		}
	}
	
	/**
	 * Compute the available tasks for the given marking according to the model.
	 * @param net Model.
	 * @param marking Marking.
	 * @param mapping Connection between log events and Petri net transitions.
	 * @param sem Petri net semantics to compute the available transitions.
	 * @return Pairs of enable tasks and the marking reached after firing it..
	 * @throws Exception
	 */
	public MarksTasks computeEnableTasks(PluginContext context, Petrinet net, Marking marking, 
			TransEvClassMapping mapping, PetrinetSemantics sem, ETCResults res) throws Exception{
		
		//Create the result object
		MarksTasks mt = new MarksTasks();
		
		//Set the current marking in the semantic
		sem.setCurrentState(marking);
		
		//Get the executable transitions
		Collection<Transition> enableTrans = sem.getExecutableTransitions();
		
		//EventClass of invisible/skip/dummy tasks
		XEventClass dummy = EvClassLogPetrinetConnectionFactoryUI.DUMMY;
		
		//First the Visible ones
		for(Transition trans : enableTrans){
			//Get the task of the transition
			XEventClass task = mapping.get(trans);
			if(task != dummy){ //Visible task
				computeEnableTasksVisible(trans, task, mt, marking, sem, res);
			}
		}
		
		//Second the invisible ones
		for(Transition trans : enableTrans){
			//Get the task of the transition
			XEventClass task = mapping.get(trans);
			if(task == dummy){ //Invisible task
				computeEnableTasksInvisible(context, trans, marking, net, sem, mapping, mt, res);
			}
		}
	
		return mt;
	}
	
	/**
	 * Compute the tasks associated with a given visible tasks.
	 * @param trans Visible transition.
	 * @param taskSet Set of tasks associated to this transition.
	 * @param mt Tasks and Markings seen since now, where the function adds the
	 *  task associated with the given transition.
	 * @param marking Marking that enables the given transition.
	 * @param sem Petrinet semantic used analysis the petri net.
	 * @throws IllegalTransitionException
	 * @throws ETCException
	 */
	private void computeEnableTasksVisible(Transition trans, 
			XEventClass task, MarksTasks mt, Marking marking, 
			PetrinetSemantics sem, ETCResults res) throws IllegalTransitionException, ETCException{	
		
		//Check if it exists another enable transitions with same task
		if(mt.tasks.contains(task)){
			//INDETERMINISM
			//Because the visible are the first, if there is another task
			//with the same name, means indeterminism.
			if(res.isRandomIndet()){
				//Not do anything (we choose the first marking)
			}
			else{
				//throw new ETCException(ETCException.DETERMINISM_TXT+"Task: "+
				//		task.toString()+"Markings: "+marking.toString()+" - "+
				//		mt.marks.get(mt.tasks.indexOf(task)));
				mt.tasks.add(task);
				mt.marks.add(null);
				mt.direct.add(true);
			}
			
		}
		
		//Compute the reached marking after firing the transition
		sem.setCurrentState(marking);
		sem.executeExecutableTransition(trans);
		Marking mark = sem.getCurrentState();
		
		//Add the task and the mark to the result
		mt.tasks.add(task);
		mt.marks.add(mark);
		mt.direct.add(true);
	}
	
	/**
	 * Compute the visible tasks after firing the given invisible task.
	 * @param context Plugin context.
	 * @param invTran The invisible transition.
	 * @param mark The marking that enables the invisible transition.
	 * @param net The Petri net.
	 * @param sem A Petri net semantics to perform Petri net analysis.
	 * @param mapping Conection between Petri net transitions and log events.
	 * @param mt Tasks and Markings seen since now, where the function adds the
	 *  tasks associated with the given transition.
	 * @throws ETCException
	 * @throws IllegalTransitionException
	 */
	private void computeEnableTasksInvisible(PluginContext context, 
			Transition invTran, Marking mark, Petrinet net, PetrinetSemantics 
			sem, TransEvClassMapping mapping, MarksTasks mt, ETCResults res) throws ETCException, 
			IllegalTransitionException{
		
		//Get the marking reached after firing the invisible transition
		sem.setCurrentState(mark);
		sem.executeExecutableTransition(invTran);
		Marking sourceMark = sem.getCurrentState();
		
		//Build the Invisible Coverability Graph for that marking
		CoverabilityGraph icg = buildICG(context, sourceMark, net, mapping);
		
		//TODO Foo to test a breath first search over the ICG
		//@SuppressWarnings("unused")
		//State rootnodefoo = icg.getNode(sourceMark);

		//For all the markings in the ICG, get their Visible Tasks
		Iterator<?> markICGIt = icg.getStates().iterator();
		while (markICGIt.hasNext()) {
			Marking m = (Marking) markICGIt.next();
			sem.setCurrentState(m);
			
			for (Transition t : sem.getExecutableTransitions()) {
				XEventClass task = mapping.get(t);
				
				//Check only the visible tasks enabled
				XEventClass dummy = EvClassLogPetrinetConnectionFactoryUI.DUMMY;
				if (task != dummy) {//Visible
					
					//Check Indeterminism
					if(mt.tasks.contains(task)){
						//IF NOT (Lazy AND previous tasks is a direct one= = Indeterminism
						if(!(res.isLazyInv() && mt.direct.get(mt.tasks.indexOf(task)))){
							//INDETERMINISM
							if(res.isRandomIndet()){
								//Not do anything (we choose the first marking)
							}
							else{
								//throw new ETCException(ETCException.DETERMINISM_TXT+"Task: "+
								//		task.toString()+"Markings: "+m.toString()+" - "+
								//		mt.marks.get(mt.tasks.indexOf(task)));
								mt.tasks.add(task);
								mt.marks.add(null);
								mt.direct.add(false);
							}
							
						}	
					}
					else{
						//Compute the reached marking after firing the transition
						sem.setCurrentState(m);
						sem.executeExecutableTransition(t);
						Marking markToSet = sem.getCurrentState();
						
						//Add the task and the mark to the result
						mt.tasks.add(task);
						mt.marks.add(markToSet);
						mt.direct.add(false);
					}					
				}
			}

		}
	}
	
	/**
	 * Build the Invisible Coverability Graph (ICG) from the given Marking.
	 * 
	 * @param mark Initial Marking for the ICG.
	 * @return Coverability Graph (ICG) from the given Marking.
	 */
	private CoverabilityGraph buildICG(PluginContext context, Marking sourceMark, Petrinet net, TransEvClassMapping mapping) {

		//Create and set a Invisible Petri net semantics for the ICG
		InvisiblePetrinetSemantics semInv = new InvisiblePetrinetSemantics
		(PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class), mapping);
		semInv.initialize(net.getTransitions(), sourceMark);
		
		//Create a Coverability Generator and create the CG with an invisible semantic
		CGGenerator genCG = new CGGenerator();
		return genCG.doBreadthFirst(context, sourceMark.toString(), new CTMarking(sourceMark), semInv);
	}
	

	/**
	 * Set and compute the escaping states of an extended prefix automaton.
	 * It also set the type of the other kind of nodes.
	 * @param res Result object to store some statistical information.
	 */
	public void detectEscaping(ETCResults res) {
		imprecisions = new Imprecisions();
		tree.getRoot().setType(PrefixAutomatonNodeType.IN);//Root always IN node
		res.setInStates(res.getInStates()+1); // The root
		detectEscapingRec(tree.getRoot(),res);
		res.setTotalStates(res.getInStates()+res.getEsc0States()+res.getEscGStates()+res.getOuterStates()+res.getNonFitStates()+res.getNonDetStates());
	}
	
	/**
	 * Set and compute the escaping states of the subtree rooted by the given node.
	 * It also set the type of the other kind of nodes.
	 * @param parent Root of the subtree to be analyzed.
	 * @param res Result object to store some statistical information.
	 */
	public void detectEscapingRec(PrefixAutomatonNode parent, ETCResults res) {
		//TODO Different strategies for defining escaping edges
		//Current strategy: Percentage respect to the parent node
		
		//Compute the minimum occurrence value to not be escaping
		int parentOccu = parent.getInstances();
		double gamma = res.getEscTh(); 
		double limit = parentOccu * gamma;
		
		//Set children node type
		int numEsc = 0;
		int numChildren = 0;
		for(PrefixAutomatonNode child: tree.getChildren(parent)){
			//Only the states in the Fitting Part and Deterministic Part
			if(child.getType() != PrefixAutomatonNodeType.NON_FIT && child.getType() != PrefixAutomatonNodeType.NON_DET){
				//If child is escaping edge
				int childOccu = child.getInstances();
				if( childOccu <= limit){			
					child.setType(PrefixAutomatonNodeType.ESCAPING);
					numEsc++;
					if(childOccu == 0) res.setEsc0States(res.getEsc0States()+1);
					else res.setEscGStates(res.getEscGStates()+1);
					
					//Set the subtree as OUT nodes
					setOutSubtree(child, res);
					
					//Collect Imprecision / Escaping State
					imprecisions.addImp(child);
					imprecisions.addGain(parentOccu);
					//TODO Not defined Gamma = 1
					int cost = (int) Math.ceil((childOccu - (parentOccu*gamma))/(gamma -1));
					imprecisions.addCost(cost);
				}
				else{ //No escaping edge -> continuing detection on child
					child.setType(PrefixAutomatonNodeType.IN);
					res.setInStates(res.getInStates()+1);
					detectEscapingRec(child,res);
				}
				
				numChildren++;
			}
			
		}
		
		//Set parent statistics about his children
		parent.setNumChildren(numChildren);
		parent.setNumEscaping(numEsc);	
	}
	
	/**
	 * Set all the subtree of the node as OUT nodes
	 * @param node Initial node of the subtree that falls OUT
	 */
	public void setOutSubtree(PrefixAutomatonNode node, ETCResults res){
		for(PrefixAutomatonNode child : tree.getChildren(node)){
			child.setType(PrefixAutomatonNodeType.OUT);
			setOutSubtree(child, res);
			res.setOuterStates(res.getOuterStates()+1);
		}
	}
	
	
	/**
	 * Compute the metrics over the extended prefix automaton.
	 * @param res Result object to store the results.
	 */
	public void computeMetrics(ETCResults res) {
		computeETCP(res);
	}
	
	/**
	 * Compute the ETCPrecision (ETCp) metric.
	 * @param res Result object to store the results.
	 */
	private void computeETCP(ETCResults res){
		long top = 0;
		long bot = 0;
		
		for(PrefixAutomatonNode node : tree.getVertices()){
			//Only for the IN Nodes
			if(node.getType() == PrefixAutomatonNodeType.IN){
				top += node.getInstances() * node.getNumEscaping();
				bot += node.getInstances() * node.getNumChildren();
			}
		}
		
		double sol = 1 - ((double)top/(double)bot);
		res.setEtcp(sol);
		res.setEtcpNumerator(top);
		res.setEtcpDenominator(bot);
	}	

	/**
	 * Compute the Confidence Interval of the ETCP metric.
	 * @param res Result object to store the results.
	 * @throws IOException
	 */
	public void computeConfidence(ETCResults res) throws IOException {
		computeConfidenceUpperBound(res);
		computeConfidenceLowerBound(res);
	}

	/**
	 * Compute the Upper Bound of the Confidence Interval.
	 * @param res Result object to store the results.
	 * @throws IOException
	 */
	private void computeConfidenceUpperBound(ETCResults res) throws IOException {
		//Load the ILP Solver
		SolverFactory factory;
		try {
			PackageManager.getInstance().findOrInstallPackages("LpSolve");
			System.loadLibrary("lpsolve55");
			System.loadLibrary("lpsolve55j");
			factory = new SolverFactoryLpSolve();
		} catch (Exception e) {
			throw new IOException("Unable to load required libraries: LPSolve");
		}
		
		//Create the Problem
		Problem problem = new Problem();
		Linear linearGain = new Linear();
		Linear linearCost = new Linear();
		for(int i=0; i<imprecisions.numImp(); i++){
			linearGain.add(imprecisions.getGain(i),"x"+i);
			linearCost.add(imprecisions.getCost(i),"x"+i);
			problem.setVarType("x"+i, Boolean.class);
		}
		problem.add(linearCost, "<=", res.getkConfidence());
		problem.setObjective(linearGain, OptType.MAX);
		
		//Solve the Problem
		Solver solver = factory.get();
		Result result = solver.solve(problem);
		
		//Compute the UpperBound
		long objValue =  (Long) result.getObjective();
		double num = res.getEtcpNumerator();
		num -= objValue;
		double den = res.getEtcpDenominator();
		res.setUpperBound(1-(num/den));	
	}

	
	/**
	 * Compute the Lower Bound of the Confidence Interval.
	 * @param res Result object to store the results.
	 * @throws IOException
	 */
	private void computeConfidenceLowerBound(ETCResults res) {
		double num = res.getEtcpNumerator();
		num += (res.getAveSizeTraces() * res.getkConfidence() * (res.getNTasks()-1));
		double den = res.getEtcpDenominator();
		den += (res.getAveSizeTraces() * res.getkConfidence() * res.getNTasks());
		res.setLowerBound(1-(num/den));
		
	}

	/**
	 * Build the Minimal Disconformant Traces log for the given extended automaton.
	 * @param res Result object to store the results.
	 * @return Minimal Disconformant Traces log.
	 */
	public XLog createMDT(ETCResults res) {	
		XLog MDT = MDTUtils.createMDT(
				"MDT:"+res.getLogName()+"+"+res.getModelName(),
				"Minimal Disconformant Traces from the log "+res.getLogName()
				+" and the model "+res.getModelName(),
				"Created by ETConformance plug-in (Jorge Munoz-Gama)");
		
		//For each imprecision
		for(int i=0; i<imprecisions.numImp(); i++){
			XTrace trace = MDTUtils.createTrace("MDT_"+i);
			PrefixAutomatonNode imp = imprecisions.getImp(i);
			createMDTTraceRec(imp, trace);
			if(res.isSeverity()) computeSeverity(res, trace, imp);
			MDT.add(trace);
		}
		
		//Return
		return MDT;
	}
	
	/**
	 * Recursive function to fill the trace with the path from the root to the
	 * given node.
	 * @param node End node of the trace path.
	 * @param trace Trace to add the events according to the path.
	 */
	public void createMDTTraceRec(PrefixAutomatonNode node, XTrace trace){
		//Base Case
		if(tree.isRoot(node)){
			//Do nothing
		}
		//Recursive Case
		else{
			PrefixAutomatonNode parent = tree.getParent(node);
			createMDTTraceRec(parent,trace);
			
			String edgeName = tree.getParentEdge(node).getEvent().getId();
			XEvent event = MDTUtils.createEvent(edgeName);
			trace.add(event);
		}
	}
	/**
	 * Compute and set the severity of an imprecision.
	 * @param res Results object with statistical information.
	 * @param trace Trace leading to the imprecision.
	 * @param imp Imprecision to be analyzed.
	 */
	public void computeSeverity(ETCResults res, XTrace trace, PrefixAutomatonNode imp){
		PrefixAutomatonNode parent = tree.getParent(imp);
		Double freq = computeFreqSeverity(parent);
		Double alt = computeAltSeverity(parent);
		Double stab = computeStabSeverity(res,parent,imp);
		MDTUtils.addSeverity(trace,freq,alt,stab);
	}
	
	/**
	 * Compute the frequency factor of the severity.
	 * @param parent Parent of the imprecision node.
	 * @return The frequency factor of the imprecision.
	 */
	private Double computeFreqSeverity(PrefixAutomatonNode parent){
		int max = tree.getRoot().getInstances(); //Root always has the maximum
		int parOccu = parent.getInstances();
		return (double)parOccu/(double)max;
	}
	
	/**
	 * Compute the alternation factor of the severity.
	 * @param parent Parent of the imprecision node.
	 * @return The alternation factor of the imprecision.
	 */
	private Double computeAltSeverity(PrefixAutomatonNode parent){
		return (double)parent.getNumEscaping()/(double)parent.getNumChildren();
	}
	
	/**
	 * Compute the stability factor of the severity.
	 * @param res Result object with some statistical information.
	 * @param parent Parent of the imprecision node.
	 * @param imp Imprecision node.
	 * @return The stability factor of the imprecison.
	 */
	private Double computeStabSeverity(ETCResults res, PrefixAutomatonNode parent,
			PrefixAutomatonNode imp){
		int z = (int) Math.ceil(parent.getInstances() * res.getSeverityTh());
		int l = (int) Math.ceil(((z + parent.getInstances())*res.getEscTh())-imp.getInstances());
		BigDecimal invC = new BigDecimal(1.0/parent.getNumChildren());
		BigDecimal oneMinInvC = new BigDecimal(1.0 - (1.0/parent.getNumChildren()));
		
		BigDecimal stab = new BigDecimal(0.0);
		for(int i=0; i<(l-1); i++){
			BigInteger bin = binomial(z,i);
			BigDecimal mul1 = new BigDecimal(bin);
			BigDecimal mul2 = invC.pow(i);
			BigDecimal mul3 = oneMinInvC.pow(z-i);
			
			stab = stab.add(mul1.multiply(mul2.multiply(mul3)));
		}
		
		return stab.doubleValue();
	}
	
	
	/**
	 * Computes the binomial coefficient (n chosen k).
	 * @param N
	 * @param K
	 * @return The binomial coefficient (n chosen k).
	 */
	static BigInteger binomial(final int N, final int K) {
	    BigInteger ret = BigInteger.ONE;
	    for (int k = 0; k < K; k++) {
	        ret = ret.multiply(BigInteger.valueOf(N-k))
	                 .divide(BigInteger.valueOf(k+1));
	    }
	    return ret;
	}



	//GETTERS and SETTERS

	public DelegateTree<PrefixAutomatonNode, PrefixAutomatonEdge> getTree() {
		return tree;
	}
	
	
	/**
	 * Inner class containing Task, and the marking reached after firing them
	 * 
	 * @author Jorge Munoz-Gama (jmunoz)
	 */
	public class MarksTasks{
		
		public List<Marking> marks;
		public List<XEventClass> tasks;
		public List<Boolean> direct;
		
		public MarksTasks(){
			marks = new LinkedList<Marking>();
			tasks = new LinkedList<XEventClass>();
			direct = new LinkedList<Boolean>();
		}
	}
	
	/**
	 * Inner class containing Task, and the marking reached after firing them
	 * 
	 * @author Jorge Munoz-Gama (jmunoz)
	 */
	public class IndetMark extends Marking{

	}

}
