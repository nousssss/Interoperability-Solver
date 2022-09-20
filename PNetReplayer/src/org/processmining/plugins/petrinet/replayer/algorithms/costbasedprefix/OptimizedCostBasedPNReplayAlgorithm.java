/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedprefix;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.util.Pair;
import org.processmining.plugins.petrinet.replayer.util.codec.PNCodec;
import org.processmining.plugins.petrinet.replayer.util.statespaces.CPNCostBasedTreeNode;
import org.processmining.plugins.petrinet.replayresult.StepTypes;

/**
 * @author aadrians
 * 
 */
public class OptimizedCostBasedPNReplayAlgorithm {
	// accessor result
	public static int LISTOFPAIR = 0;
	public static int ISRELIABLE = 1;
	public static int TIME = 2;

	// result of loopReplay
	private static int ID = 0;
	private static int NODE = 1;

	public static Object[] replayTraceInEncodedForm(Progress progress, List<XEventClass> listTrace,
			Map<XEventClass, List<Short>> transitionMapping, Set<Short> setInviTrans, PNCodec codec,
			Bag<Short> encInitMarking, int maxNumOfStates, int inappropriateTransFireCost, int replayedEventCost,
			int skippedEventCost, int heuristicDistanceCost, int selfExecInviTaskCost, int selfExecRealTaskCost,
			boolean allowInviTaskMove, boolean allowRealTaskMove, boolean allowEventSkip, boolean allowExecWOTokens,
			boolean allowExecViolating, PriorityQueue<CPNCostBasedTreeNode> pq) {

		pq.clear();

		CPNCostBasedTreeNode root = new CPNCostBasedTreeNode();
		root.setCurrMarking(encInitMarking);
		root.setCurrIndexOnTrace(0);
		root.setCost(heuristicDistanceCost * listTrace.size());
		pq.add(root);

		CPNCostBasedTreeNode currNode;

		// start replay
		int traceSize = listTrace.size();
		int id = 1;
		boolean reliable = true; // false if only the best so far is returned

		try {
			// only replay once, no reset
			Object[] loopResult = replayLoop(progress, listTrace, transitionMapping, setInviTrans, codec, encInitMarking,
					maxNumOfStates, inappropriateTransFireCost, replayedEventCost, skippedEventCost, heuristicDistanceCost,
					selfExecInviTaskCost, selfExecRealTaskCost, pq, traceSize, id, allowInviTaskMove, allowRealTaskMove,
					allowEventSkip, allowExecWOTokens, allowExecViolating);
			id = Integer.parseInt(loopResult[ID].toString());
			currNode = (CPNCostBasedTreeNode) loopResult[NODE];
	
			if (id >= maxNumOfStates) {
				reliable = false;
			}
			if (progress.isCancelled()) {
				return null;
			}
	
			return new Object[] {
					createShortListFromTreeNode(encInitMarking, setInviTrans, currNode, codec, listTrace, transitionMapping),
					reliable, loopResult[TIME] };
		} catch (OutOfMemoryError exc){
			// out of memory, return empty trace with unreliable result
			return new Object[] { new LinkedList<Pair<StepTypes, Object>>(), false, 0 };
		}
	}

	/**
	 * 
	 * @param progress
	 * @param listTrace
	 * @param transitionMapping
	 * @param setInviTrans
	 * @param codec
	 * @param encInitMarking
	 * @param maxNumOfStates
	 * @param inappropriateTransFireCost
	 * @param replayedEventCost
	 * @param skippedEventCost
	 * @param heuristicDistanceCost
	 * @param selfExecInviTaskCost
	 * @param selfExecRealTaskCost
	 * @param pq
	 * @param traceSize
	 * @param id
	 * @param allowInviTaskMove
	 * @param allowRealTaskMove
	 * @param allowEventSkip
	 * @param allowExecWOTokens
	 * @param allowExecViolating
	 * @return id of states, solution node, time spent to compute (in milliseconds)
	 */
	private static Object[] replayLoop(Progress progress, List<XEventClass> listTrace,
			Map<XEventClass, List<Short>> transitionMapping, Set<Short> setInviTrans, PNCodec codec,
			Bag<Short> encInitMarking, int maxNumOfStates, int inappropriateTransFireCost, int replayedEventCost,
			int skippedEventCost, int heuristicDistanceCost, int selfExecInviTaskCost, int selfExecRealTaskCost,
			PriorityQueue<CPNCostBasedTreeNode> pq, int traceSize, int id, boolean allowInviTaskMove,
			boolean allowRealTaskMove, boolean allowEventSkip, boolean allowExecWOTokens, boolean allowExecViolating) {
		CPNCostBasedTreeNode currNode = pq.poll();
		long temp = System.nanoTime();

		while ((currNode.getCurrIndexOnTrace() < traceSize) && (id < maxNumOfStates) && (!progress.isCancelled())) {
			if (allowEventSkip) {
				// skip current event
				CPNCostBasedTreeNode nodeT = createNodeByMoveTrace(traceSize, currNode, replayedEventCost,
						heuristicDistanceCost, skippedEventCost);
				id++;
				if (!pq.contains(nodeT)) {
					pq.add(nodeT);
				}
			}

			// execute next event
			List<Short> candidateTrans = transitionMapping.get(listTrace.get(currNode.getCurrIndexOnTrace()));
			if (candidateTrans.size() > 1) {
				for (Short candidate : candidateTrans) {
					if (progress.isCancelled()) {
						return null;
					}
					// create new pnCostBasedTreeNode
					CPNCostBasedTreeNode nodeE = createNodeByExecuteEvent(currNode, candidate, codec, traceSize,
							inappropriateTransFireCost, replayedEventCost, heuristicDistanceCost, true,
							allowExecViolating);

					if (nodeE != null) {
						id++;
						if (!pq.contains(nodeE)) {
							pq.add(nodeE);
						}

						if (allowExecWOTokens) {
							// execute without taking any tokens
							CPNCostBasedTreeNode nodeNew = createNodeByExecuteEventWithoutTokens(currNode, candidate,
									codec, traceSize, inappropriateTransFireCost, replayedEventCost,
									heuristicDistanceCost, true);
							id++;
							if (!pq.contains(nodeNew)) {
								pq.add(nodeNew);
							}
						}
					}
				}
			} else {
				Short candidate = candidateTrans.get(0);
				CPNCostBasedTreeNode nodeE = createNodeByExecuteEvent(currNode, candidate, codec, traceSize,
						inappropriateTransFireCost, replayedEventCost, heuristicDistanceCost, false, allowExecViolating);
				if (nodeE != null) {
					id++;
					if (!pq.contains(nodeE)) {
						pq.add(nodeE);
					}

					if (allowExecWOTokens) {
						// execute without taking any tokens
						CPNCostBasedTreeNode nodeNew = createNodeByExecuteEventWithoutTokens(currNode, candidate,
								codec, traceSize, inappropriateTransFireCost, replayedEventCost, heuristicDistanceCost,
								false);
						id++;
						if (!pq.contains(nodeNew)) {
							pq.add(nodeNew);
						}
					}
				}
			}

			if (allowInviTaskMove || allowRealTaskMove) {
				// execute any possible transitions (including both invisible and
				// real transitions)
				Set<CPNCostBasedTreeNode> additional = createNodeByExecutingPossibleTransitions(currNode, codec,
						setInviTrans, selfExecInviTaskCost, selfExecRealTaskCost, allowInviTaskMove, allowRealTaskMove);
				for (CPNCostBasedTreeNode nodeM : additional) {
					id++;
					if (!pq.contains(nodeM)) {
						pq.add(nodeM);
					}
				}
			}

			// continue to the next node
			currNode = pq.poll();

		}
		
		return new Object[] { id, currNode, (double)(System.nanoTime() - temp) / (double) 1000000 };
	}

	private static CPNCostBasedTreeNode createNodeByExecuteEventWithoutTokens(CPNCostBasedTreeNode currNode,
			Short selectedNode, PNCodec codec, int traceSize, int inappropriateTransFireCost, int replayedEventCost,
			int heuristicDistanceCost, boolean isDuplicate) {
		CPNCostBasedTreeNode res = new CPNCostBasedTreeNode(currNode);

		Set<Short> successors = codec.getSuccessors(selectedNode);

		Bag<Short> newMarking = new HashBag<Short>(currNode.getCurrMarking());
		newMarking.addAll(successors);
		for (Short reset : codec.getResets(selectedNode)) {
			newMarking.remove(reset, newMarking.getCount(reset));
		}

		res.setCurrMarking(newMarking);
		res.setCurrIndexOnTrace(currNode.getCurrIndexOnTrace() + 1);

		res.getTraceModelViolatingStep().add(
				new Pair<Integer, Short>(currNode.getCurrIndexOnTrace() + currNode.getModelOnlyStep().size(),
						selectedNode));

		// cost
		int cost = currNode.getCost() + inappropriateTransFireCost + replayedEventCost - heuristicDistanceCost;
		res.setCost(cost);

		return res;
	}

	private static Set<CPNCostBasedTreeNode> createNodeByExecutingPossibleTransitions(CPNCostBasedTreeNode currNode,
			PNCodec codec, Set<Short> setInviTrans, int selfExecInviTaskCost, int selfExecRealTaskCost,
			boolean allowInviTaskMove, boolean allowRealTaskMove) {
		Set<Short> possiblyExecuted = getPossiblyExecutedTrans(currNode, codec);
		Set<CPNCostBasedTreeNode> res = new HashSet<CPNCostBasedTreeNode>(possiblyExecuted.size());
		for (Short possblyExec : possiblyExecuted) {
			if (setInviTrans.contains(possblyExec)) {
				if (!allowInviTaskMove) {
					continue;
				}
			} else {
				if (!allowRealTaskMove) {
					continue;
				}
			}

			CPNCostBasedTreeNode newNode = new CPNCostBasedTreeNode(currNode);

			// marking
			Bag<Short> newMarking = newNode.getCurrMarking();
			newMarking.removeAll(codec.getPredecessors(possblyExec));
			for (Short reset : codec.getResets(possblyExec)) {
				newMarking.remove(reset, newMarking.getCount(reset));
			}
			newMarking.addAll(codec.getSuccessors(possblyExec));
			newNode.setCurrMarking(newMarking);

			// cost
			int cost = newNode.getCost();
			if (setInviTrans.contains(possblyExec)) {
				cost += selfExecInviTaskCost;
			} else {
				cost += selfExecRealTaskCost;
			}

			newNode.setCost(cost);

			// move only
			List<Pair<Integer, Short>> listPair = newNode.getModelOnlyStep();
			listPair.add(new Pair<Integer, Short>(listPair.size() + currNode.getCurrIndexOnTrace(), possblyExec));
			newNode.setModelOnlyStep(listPair);

			res.add(newNode);
		}
		return res;
	}

	private static Set<Short> getPossiblyExecutedTrans(CPNCostBasedTreeNode currNode, PNCodec codec) {
		Set<Short> res = new HashSet<Short>();
		Bag<Short> currMarking = currNode.getCurrMarking();
		for (Short codedTrans : codec.getMapShortTrans().keySet()) {
			boolean isAppropriate = currMarking.containsAll(codec.getPredecessors(codedTrans));
			for (Short inh : codec.getInhibitors(codedTrans)) {
				if (currMarking.contains(inh)) {
					isAppropriate = false;
					break;
				}
			}

			if (isAppropriate) {
				res.add(codedTrans);
			}
		}
		return res;
	}

	private static CPNCostBasedTreeNode createNodeByExecuteEvent(CPNCostBasedTreeNode currNode, Short selectedNode,
			PNCodec codec, int traceSize, int inappropriateTransFireCost, int replayedEventCost,
			int heuristicDistanceCost, boolean isDuplicate, boolean allowExecViolating) {
		Set<Short> predecessors = codec.getPredecessors(selectedNode);
		Set<Short> successors = codec.getSuccessors(selectedNode);
		CPNCostBasedTreeNode res = new CPNCostBasedTreeNode(currNode);

		boolean isAppropriate = computeNewMarking(currNode, selectedNode, codec, predecessors, successors, res);

		if (!isAppropriate && !allowExecViolating) {
			return null;
		}

		res.setCurrIndexOnTrace(currNode.getCurrIndexOnTrace() + 1);

		if (isDuplicate) {
			res.getDuplicatesOnlyStep().add(
					new Pair<Integer, Short>(currNode.getCurrIndexOnTrace() + currNode.getModelOnlyStep().size(),
							selectedNode));
		}

		// cost
		int cost = currNode.getCost();
		if (!isAppropriate) {
			cost += inappropriateTransFireCost;
		}
		cost = cost + replayedEventCost - heuristicDistanceCost;
		res.setCost(cost);

		return res;
	}

	private static boolean computeNewMarking(CPNCostBasedTreeNode currNode, Short selectedNode, PNCodec codec,
			Set<Short> predecessors, Set<Short> successors, CPNCostBasedTreeNode res) {
		Bag<Short> newMarking = new HashBag<Short>(currNode.getCurrMarking());
		boolean isAppropriate = newMarking.containsAll(predecessors);
		for (Short inh : codec.getInhibitors(selectedNode)) {
			if (newMarking.contains(inh)) {
				isAppropriate = false;
				break;
			}
		}

		newMarking.removeAll(predecessors);
		for (Short reset : codec.getResets(selectedNode)) {
			newMarking.remove(reset, newMarking.getCount(reset));
		}

		newMarking.addAll(successors);
		res.setCurrMarking(newMarking);
		return isAppropriate;
	}

	private static CPNCostBasedTreeNode createNodeByMoveTrace(int traceSize, CPNCostBasedTreeNode currNode,
			int replayedEventCost, int heuristicDistanceCost, int skippedEventCost) {
		CPNCostBasedTreeNode res = new CPNCostBasedTreeNode(currNode);
		res.setCurrIndexOnTrace(currNode.getCurrIndexOnTrace() + 1);
		res.getMoveTraceOnlyStep().add(currNode.getCurrIndexOnTrace() + currNode.getModelOnlyStep().size());

		// cost
		int cost = currNode.getCost() + skippedEventCost - heuristicDistanceCost + replayedEventCost;
		res.setCost(cost);

		return res;
	}

	/**
	 * @param encInitMarking
	 * @param setInviTrans
	 * @param currNode
	 * @param codec
	 * @param listTrace
	 * @param transitionMapping
	 * @return
	 */
	private static List<Pair<StepTypes, Object>> createShortListFromTreeNode(Bag<Short> encInitMarking,
			Set<Short> setInviTrans, CPNCostBasedTreeNode currNode, PNCodec codec, List<XEventClass> listTrace,
			Map<XEventClass, List<Short>> transitionMapping) {
		ListIterator<Pair<Integer, Short>> itDup = currNode.getDuplicatesOnlyStep().listIterator();
		ListIterator<Pair<Integer, Short>> itModOnly = currNode.getModelOnlyStep().listIterator();
		ListIterator<Pair<Integer, Short>> itModLogViolates = currNode.getTraceModelViolatingStep().listIterator();
		ListIterator<Integer> itTraceOnly = currNode.getMoveTraceOnlyStep().listIterator();

		List<Pair<StepTypes, Object>> res = new LinkedList<Pair<StepTypes, Object>>();

		int currIdx = 0;
		Pair<Integer, Short> currDup = itDup.hasNext() ? itDup.next() : null;
		Pair<Integer, Short> currModOnly = itModOnly.hasNext() ? itModOnly.next() : null;
		Pair<Integer, Short> currModLogViolates = itModLogViolates.hasNext() ? itModLogViolates.next() : null;
		int currTraceOnly = itTraceOnly.hasNext() ? itTraceOnly.next() : Integer.MAX_VALUE;

		// replay token games
		Bag<Short> currMarking = new HashBag<Short>(encInitMarking);
		int currTraceIndex = 0;

		int resTraceSize = listTrace.size() + currNode.getModelOnlyStep().size();
		boolean loopFinish = false;

		while (currIdx < resTraceSize) {
			loopFinish = false;
			if (currDup != null) {
				if (currDup.getFirst() == currIdx) { // currently executing
					// duplicate
					Short executedTransition = currDup.getSecond();

					boolean isAppropriate = currMarking.containsAll(codec.getPredecessors(executedTransition));
					for (Short inh : codec.getInhibitors(executedTransition)) {
						if (currMarking.contains(inh)) {
							isAppropriate = false;
							break;
						}
					}

					if (isAppropriate) {
						// valid execution
						res.add(new Pair<StepTypes, Object>(StepTypes.LMGOOD, codec.decode(executedTransition)));
					} else { // invalid execution
						res.add(new Pair<StepTypes, Object>(StepTypes.LMNOGOOD, codec.decode(executedTransition)));
					}
					currMarking.removeAll(codec.getPredecessors(executedTransition));
					for (Short reset : codec.getResets(executedTransition)) {
						currMarking.remove(reset, currMarking.getCount(reset));
					}
					currMarking.addAll(codec.getSuccessors(executedTransition));
					loopFinish = true;
					currTraceIndex++;
					currDup = itDup.hasNext() ? itDup.next() : null;
				}
			}

			if ((currModOnly != null) && (!loopFinish)) {
				if (currModOnly.getFirst() == currIdx) {
					Short executedTransition = currModOnly.getSecond();
					if (setInviTrans.contains(executedTransition)) {
						res.add(new Pair<StepTypes, Object>(StepTypes.MINVI, codec.decode(executedTransition)));
					} else {
						res.add(new Pair<StepTypes, Object>(StepTypes.MREAL, codec.decode(executedTransition)));
					}
					currMarking.removeAll(codec.getPredecessors(executedTransition));
					for (Short reset : codec.getResets(executedTransition)) {
						currMarking.remove(reset, currMarking.getCount(reset));
					}
					currMarking.addAll(codec.getSuccessors(executedTransition));
					loopFinish = true;
					currModOnly = itModOnly.hasNext() ? itModOnly.next() : null;
				}
			}

			if ((currModLogViolates != null) && (!loopFinish)) {
				if (currModLogViolates.getFirst() == currIdx) {
					Short executedTransition = currModLogViolates.getSecond();
					// invalid execution
					res.add(new Pair<StepTypes, Object>(StepTypes.LMNOGOOD, codec.decode(executedTransition)));
					for (Short reset : codec.getResets(executedTransition)) {
						currMarking.remove(reset, currMarking.getCount(reset));
					}
					currMarking.addAll(codec.getSuccessors(executedTransition));
					loopFinish = true;
					currModLogViolates = itModLogViolates.hasNext() ? itModLogViolates.next() : null;
					currTraceIndex++;
				}
			}

			if (currTraceOnly == currIdx) {
				res.add(new Pair<StepTypes, Object>(StepTypes.L, listTrace.get(currTraceIndex)));
				loopFinish = true;
				currTraceIndex++;
				currTraceOnly = itTraceOnly.hasNext() ? itTraceOnly.next() : Integer.MAX_VALUE;
			}

			if (!loopFinish) {
				Short executedTransition = transitionMapping.get(listTrace.get(currTraceIndex)).get(0);

				boolean isAppropriate = currMarking.containsAll(codec.getPredecessors(executedTransition));
				for (Short inh : codec.getInhibitors(executedTransition)) {
					if (currMarking.contains(inh)) {
						isAppropriate = false;
						break;
					}
				}

				if (isAppropriate) {
					// valid execution
					res.add(new Pair<StepTypes, Object>(StepTypes.LMGOOD, codec.decode(executedTransition)));
				} else { // invalid execution
					res.add(new Pair<StepTypes, Object>(StepTypes.LMNOGOOD, codec.decode(executedTransition)));
				}
				currMarking.removeAll(codec.getPredecessors(executedTransition));
				for (Short reset : codec.getResets(executedTransition)) {
					currMarking.remove(reset, currMarking.getCount(reset));
				}
				currMarking.addAll(codec.getSuccessors(executedTransition));
				currTraceIndex++;
			}

			currIdx++;
		}

		return res;
	}

}
