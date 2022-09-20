/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.markeq;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;
import net.sf.javailp.VarType;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.petrinet.replayer.util.codec.EncPNWSetFinalMarkings;

/**
 * @author aadrians
 * 
 */
public class MarkingEqConsultant {
	private static String MOVEONLOG = "L";
	private static String MOVEONMODEL = "M";
	private static String TRANS = "t";

	// is the required library loaded?
	private static boolean loadedLibrary = false;

	/**
	 * important attributes
	 */
	// problem template of linear programming problem to be solved
	private Problem problem;
	private static SolverFactory factory = new SolverFactoryLpSolve();

	// naming of event class and transition
	private XEventClass[] evClassOptArr = null; // move on log
	private Integer[] transOptArr = null; // move on model
	
	// constraint templates
	private Linear[] evClassOptConstraint = null; // constraint for each mapped event class
	
	// constraints derived from places, mapping from encoded place
	// map between encoded place to constraint for every possible final marking 
	private Map<Integer, Linear> mapEncodedPlace2Constraint = new HashMap<Integer, Linear>();

	// final marking
	private List<Map<Integer, Integer>> finalMarkings;

	// cached results. Mapping from current distribution of event class -> curr marking -> cost
	private Map<Map<XEventClass, Integer>, Map<Integer, Integer>> mapDist2CurrM2Cost = new HashMap<Map<XEventClass, Integer>, Map<Integer, Integer>>();

	/**
	 * Static method to load lpsolve
	 * 
	 * @throws IOException
	 */
	public static void loadLPSolveLibrary() throws IOException {
		try {
			System.loadLibrary("lpsolve55");
			System.loadLibrary("lpsolve55j");
		} catch (Exception e) {
			throw new IOException("Unable to load required libraries.", e);
		}
	}

	public MarkingEqConsultant(Map<XEventClass, Integer> mapEvClass2Cost,
			Map<XEventClass, Set<Integer>> mapEvClass2EncTrans, Map<Integer, XEventClass> mapEncTrans2EvClass,
			Map<Integer, Integer> mapEncTrans2Cost, EncPNWSetFinalMarkings encodedPN,
			HashSet<Integer> encFinalMarkings, Map<Integer, Map<Integer, Integer>> mapInt2Marking) throws IOException {

		// import final markings
		finalMarkings = new LinkedList<Map<Integer, Integer>>();
		for (Integer encFinalMarking : encFinalMarkings) {
			finalMarkings.add(mapInt2Marking.get(encFinalMarking));
		}

		if (!loadedLibrary) {
			loadLPSolveLibrary();
			loadedLibrary = true;

			factory.setParameter(Solver.VERBOSE, 0);
			factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds
		}

		// construct problem
		problem = new Problem();
		Linear optimization = new Linear(); // temp variable
		Linear templinear = new Linear();

		// utilities
		// adjacency matrix. String variable for transitions that takes away tokens 
		Map<Integer, Map<String, Integer>> adjacencyMatrix = new HashMap<Integer, Map<String, Integer>>();

		// weighting
		Map<Integer, Map<Integer, Integer>> mapArc2Weight = encodedPN.getMapArc2Weight();

		// add all move on model as minimization problem
		// also add constraint that move on model >= 0
		Set<Integer> transSet = mapEncTrans2Cost.keySet();
		if ((transSet != null) && (transSet.size() > 0)) {
			transOptArr = transSet.toArray(new Integer[transSet.size()]);
			
			for (int i = 0; i < transOptArr.length; i++) {
				String moveOnModelVar = MOVEONMODEL + i;
				optimization.add(mapEncTrans2Cost.get(transOptArr[i]), moveOnModelVar);
				
				String transOccurrenceVar = TRANS + i;
				
				// add constraint that the optimized variable should be less or equal to what has to happen
				templinear = new Linear();
				templinear.add(1, moveOnModelVar);
				templinear.add(-1, transOccurrenceVar);
				problem.add(templinear, "<=", 0);
				
				// setup adjacency matrix based on places
				Set<Integer> predecessors = encodedPN.getPredecessorsOf(transOptArr[i]);
				if (predecessors != null) {
					for (Integer encPlace : predecessors) {
						Map<String, Integer> mapTrans2Val = adjacencyMatrix.get(encPlace);
						if (mapTrans2Val == null) {
							mapTrans2Val = new HashMap<String, Integer>();
							adjacencyMatrix.put(encPlace, mapTrans2Val);
						}

						Integer oldValue = mapTrans2Val.get(transOccurrenceVar);
						if (oldValue == null) {
							mapTrans2Val.put(transOccurrenceVar, -1 * mapArc2Weight.get(encPlace).get(transOptArr[i]));
						} else {
							mapTrans2Val.put(transOccurrenceVar,
									oldValue - mapArc2Weight.get(encPlace).get(transOptArr[i]));
						}
					}
				}
				Map<Integer, Integer> successors = mapArc2Weight.get(transOptArr[i]);
				if (successors != null) {
					for (Integer encPlace : successors.keySet()) {
						Map<String, Integer> mapTrans2Val = adjacencyMatrix.get(encPlace);
						if (mapTrans2Val == null) {
							mapTrans2Val = new HashMap<String, Integer>();
							adjacencyMatrix.put(encPlace, mapTrans2Val);
						}

						Integer oldValue = mapTrans2Val.get(transOccurrenceVar);
						if (oldValue == null) {
							mapTrans2Val.put(transOccurrenceVar, mapArc2Weight.get(transOptArr[i]).get(encPlace));
						} else {
							mapTrans2Val.put(transOccurrenceVar,
									oldValue + mapArc2Weight.get(transOptArr[i]).get(encPlace));
						}
					}
				}
			}
		}

		// for each place, setup a constraint template
		for (Integer encPlace : adjacencyMatrix.keySet()) {
			// create constraints
			Linear linear = new Linear();
			Map<String, Integer> mapTransVar2Value = adjacencyMatrix.get(encPlace);
			for (String nameTransVar : mapTransVar2Value.keySet()) {
				Integer value = mapTransVar2Value.get(nameTransVar);
				if (value != 0) {
					linear.add(value, nameTransVar);
				}
			}
			mapEncodedPlace2Constraint.put(encPlace, linear);
		}

		// add all move on log as minimization problem
		// also add constraint that move on log >= 0
		Set<XEventClass> evClassSet = mapEvClass2Cost.keySet();
		Set<Integer> transMapped2RealEvClass = new HashSet<Integer>(transOptArr.length);
		if ((evClassSet != null) && (evClassSet.size() > 0)) {
			evClassOptArr = evClassSet.toArray(new XEventClass[evClassSet.size()]);
			evClassOptConstraint = new Linear[evClassOptArr.length]; // one extra constraint per event class
			for (int i = 0; i < evClassOptArr.length; i++) {
				String moveOnLogVar = MOVEONLOG + i;
				optimization.add(mapEvClass2Cost.get(evClassOptArr[i]), moveOnLogVar);

				// the number of move on log must be >= 0
				problem.setVarType(moveOnLogVar, Integer.class);
				problem.setVarLowerBound(moveOnLogVar, 0);

				Linear lCons = new Linear();
				lCons.add(1, moveOnLogVar);
				
				// get all transitions that mapped to the same events
				Set<Integer> transMappedToEC = mapEvClass2EncTrans.get(evClassOptArr[i]);
				if ((transMappedToEC != null) && (transMappedToEC.size() > 0)) {
					for (int j = 0; j < transOptArr.length; j++) {
						if (transMappedToEC.contains(transOptArr[j])) {
							lCons.add(1, TRANS + j);
							lCons.add(-1, MOVEONMODEL + j);
							transMapped2RealEvClass.add(j);
						}
					}
				}
				
				// add constraint
				evClassOptConstraint[i] = lCons;
			}
		}
		
		// all transitions that mapped to dummy activities
		for (int i=0; i < transOptArr.length; i++){
			if (!transMapped2RealEvClass.contains(i)){
				Linear linear = new Linear();
				linear.add(1, TRANS + i);
				linear.add(-1, MOVEONMODEL + i);
				Constraint c = new Constraint(linear, "=", 0);
				problem.add(c);
			}
		}

		// set objective
		problem.setObjective(optimization, OptType.MIN);
	}

	/**
	 * Estimate cost
	 * 
	 * @param lstEvtClass
	 * @param currMarking
	 * @param mapInt2Marking
	 * @param mapEncTrans2EvClass
	 * @return
	 */
	public int estimateCost(List<XEventClass> lstEvtClass, Integer currMarking,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking, Map<Integer, XEventClass> mapEncTrans2EvClass) {
		// check cache
		// construct Map<XeventClass, frequency of occurrence>
		Map<XEventClass, Integer> mapEC2Freq = new HashMap<XEventClass, Integer>(lstEvtClass.size());
		for (XEventClass evClass : lstEvtClass) {
			Integer occurrence = mapEC2Freq.get(evClass);
			if (occurrence == null) {
				mapEC2Freq.put(evClass, 1);
			} else {
				mapEC2Freq.put(evClass, occurrence + 1);
			}
		}

		return getOrCalculateCost(mapEC2Freq, currMarking, mapInt2Marking, mapEncTrans2EvClass);
	}

	/**
	 * Method to calculate cost from scratch
	 * 
	 * @param mapEC2Freq
	 * @param currMarking
	 * @param mapInt2Marking
	 * @param mapEncTrans2EvClass
	 * @return
	 */
	private synchronized int getOrCalculateCost(Map<XEventClass, Integer> mapEC2Freq, Integer currMarking,
			Map<Integer, Map<Integer, Integer>> mapInt2Marking, Map<Integer, XEventClass> mapEncTrans2EvClass) {
		// try to find if it has been calculated before
		Map<Integer, Integer> mapCurrM2Cost = mapDist2CurrM2Cost.get(mapEC2Freq);
		if (mapCurrM2Cost == null) {
			mapCurrM2Cost = new HashMap<Integer, Integer>();
			mapDist2CurrM2Cost.put(mapEC2Freq, mapCurrM2Cost);
		}
		// get the real marking
		Map<Integer, Integer> realCurrMarking = mapInt2Marking.get(currMarking);

		// get the cost
		Integer cost = mapCurrM2Cost.get(currMarking);
		if (cost == null) {
			// calculate the cost using marking equation for all final markings
			for (Map<Integer, Integer> finalMarking : finalMarkings) {
				Problem instProblem = new Problem();

				// copy all problem constraints from template
				for (Constraint cons : problem.getConstraints()) {
					instProblem.add(cons);
				}
				instProblem.setObjective(problem.getObjective());

				// optimization constraint for each activity
				for (int i = 0; i < evClassOptArr.length; i++) {
					// calculate the number of event class
					Integer count = mapEC2Freq.get(evClassOptArr[i]);
					if (count == null) {
						count = 0;
					}

					instProblem.add(evClassOptConstraint[i], "=", count);
				}
				
				// constraint to the non-optimized-variables
				for (Integer encPlace : mapEncodedPlace2Constraint.keySet()) {
					// calculate the number of event class
					Integer count = finalMarking.get(encPlace);
					if (count == null) {
						count = 0;
					}
					count = realCurrMarking.get(encPlace) == null ? count : count - realCurrMarking.get(encPlace);

					instProblem.add(mapEncodedPlace2Constraint.get(encPlace), "=", count);
				}
				

				// set all variables to integer >= 0
				for (Object var : instProblem.getVariables()){
					instProblem.setVarType(var, VarType.INT);
					instProblem.setVarLowerBound(var, 0);
				}
				
				// ask the solver
				Solver solver = factory.get();
				Result result = solver.solve(instProblem);
				if (result != null) {
					if (cost == null) {
						cost = result.getObjective().intValue();
					} else {
						// compare
						if (result.getObjective().intValue() < cost) {
							cost = result.getObjective().intValue();
						}
					}
				}
			}

			if (cost == null) {
				cost = 0;
			}
			mapCurrM2Cost.put(currMarking, cost);
		}

		return cost;
	}
}
