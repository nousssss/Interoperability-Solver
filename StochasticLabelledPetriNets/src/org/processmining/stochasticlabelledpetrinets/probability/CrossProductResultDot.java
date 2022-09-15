package org.processmining.stochasticlabelledpetrinets.probability;

import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.DotNode;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class CrossProductResultDot implements CrossProductResult {

	private Dot dot;
	private TIntObjectMap<DotNode> index2dotNode;

	public CrossProductResultDot() {
		dot = new Dot();
		index2dotNode = new TIntObjectHashMap<DotNode>(10, 0.5f, -1);
	}

	public void reportFinalState(int stateIndex) {
		DotNode dotNode = index2dotNode.get(stateIndex);
		if (dotNode == null) {
			dotNode = dot.addNode(stateIndex + "");
			allNodes(dotNode);
			index2dotNode.put(stateIndex, dotNode);
		}

		dotNode.setOption("style", "filled");
		dotNode.setOption("fillcolor", "#E40000");
	}

	public void reportDeadState(int stateIndex) {
		DotNode dotNode = index2dotNode.get(stateIndex);
		if (dotNode == null) {
			dotNode = dot.addNode(stateIndex + "");
			allNodes(dotNode);
			index2dotNode.put(stateIndex, dotNode);
		}

		dotNode.setOption("style", "filled");
		dotNode.setOption("fillcolor", "blue");
	}

	public void reportNonFinalState(int stateIndex, TIntList nextStateIndexes, TDoubleList nextStateProbabilities) {
		DotNode source = index2dotNode.get(stateIndex);
		if (source == null) {
			source = dot.addNode(stateIndex + "");
			allNodes(source);
			index2dotNode.put(stateIndex, source);
		}

		TIntIterator itI = nextStateIndexes.iterator();
		TDoubleIterator itP = nextStateProbabilities.iterator();
		while (itI.hasNext()) {
			int targetIndex = itI.next();
			double targetProbability = itP.next();

			DotNode target = index2dotNode.get(targetIndex);
			if (target == null) {
				target = dot.addNode(targetIndex + "");
				allNodes(target);
				index2dotNode.put(targetIndex, target);
			}

			dot.addEdge(source, target, targetProbability + "");
		}
	}

	public void reportInitialState(int stateIndex) {
		DotNode dotNode = index2dotNode.get(stateIndex);
		if (dotNode == null) {
			dotNode = dot.addNode(stateIndex + "");
			index2dotNode.put(stateIndex, dotNode);
		}

		allNodes(dotNode);
		dotNode.setOption("style", "filled");
		dotNode.setOption("fillcolor", "#80ff00");
	}

	private static void allNodes(DotNode dotNode) {
		dotNode.setOption("shape", "circle");
	}

	public Dot toDot() {
		return dot;
	}
}