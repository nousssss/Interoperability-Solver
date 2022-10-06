package org.processmining.plugins.inductiveminer2.plugins;

import java.util.Iterator;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotEdge;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

public class ProcessTreeVisualisation {

	@Plugin(name = "Efficient tree visualisation (Inductive visual Miner)", returnLabels = {
			"Dot visualization" }, returnTypes = {
					JComponent.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise process tree", requiredParameterLabels = { 0 })
	public DotPanel fancy(PluginContext context, EfficientTree tree) throws UnknownTreeNodeException {
		Dot dot = fancy(tree);
		return new DotPanel(dot);
	}

	public static Dot fancy(EfficientTree tree) throws UnknownTreeNodeException {
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.leftRight);
		int root = tree.getRoot();

		//source & sink
		DotNode source = dot.addNode("");
		source.setOption("width", "0.2");
		source.setOption("shape", "circle");
		source.setOption("style", "filled");
		source.setOption("fillcolor", "#80ff00");
		DotNode sink = dot.addNode("");
		sink.setOption("width", "0.2");
		sink.setOption("shape", "circle");
		sink.setOption("style", "filled");
		sink.setOption("fillcolor", "#E40000");

		//convert root node
		convertNode(dot, tree, root, source, sink, true);

		return dot;
	}

	private static void convertNode(Dot dot, EfficientTree tree, int node, DotNode source, DotNode sink,
			boolean directionForward) throws UnknownTreeNodeException {
		if (tree.isSequence(node)) {
			convertSequence(dot, tree, node, source, sink, directionForward);
		} else if (tree.isLoop(node)) {
			convertLoop(dot, tree, node, source, sink, directionForward);
		} else if (tree.isInterleaved(node)) {
			convertInterleaved(dot, tree, node, source, sink, directionForward);
		} else if (tree.isConcurrent(node)) {
			convertConcurrent(dot, tree, node, source, sink, directionForward);
		} else if (tree.isOr(node)) {
			convertOr(dot, tree, node, source, sink, directionForward);
		} else if (tree.isXor(node)) {
			convertXor(dot, tree, node, source, sink, directionForward);
		} else if (tree.isActivity(node)) {
			convertActivity(dot, tree, node, source, sink, directionForward);
		} else if (tree.isTau(node)) {
			convertTau(dot, tree, node, source, sink, directionForward);
		} else {
			throw new UnknownTreeNodeException();
		}
	}

	private static void convertActivity(Dot dot, EfficientTree tree, int unode, DotNode source, DotNode sink,
			boolean directionForward) throws UnknownTreeNodeException {
		DotNode dotNode = dot.addNode(tree.getActivityName(unode));
		dotNode.setOption("shape", "box");
		dotNode.setOption("style", "rounded");
		dotNode.setOption("fontsize", "12");

		addArc(dot, tree, source, dotNode, unode, directionForward, false);
		addArc(dot, tree, dotNode, sink, unode, directionForward, false);
	}

	private static void convertTau(Dot dot, EfficientTree tree, int unode, DotNode source, DotNode sink,
			boolean directionForward) throws UnknownTreeNodeException {
		addArc(dot, tree, source, sink, unode, directionForward, false);
	}

	private static DotNode xorNode(Dot dot) {
		DotNode dotNode = dot.addNode("");
		dotNode.setOption("width", "0.05");
		dotNode.setOption("shape", "circle");
		return dotNode;
	}

	private static DotNode diamondNode(Dot dot, String label) {
		DotNode dotNode = dot.addNode(label);
		dotNode.setOption("shape", "diamond");
		dotNode.setOption("fixedsize", "true");
		dotNode.setOption("height", "0.25");
		dotNode.setOption("width", "0.27");
		return dotNode;
	}

	private static void convertSequence(Dot dot, EfficientTree tree, int node, DotNode source, DotNode sink,
			boolean directionForward) throws UnknownTreeNodeException {
		DotNode split;
		DotNode join = source;

		Iterator<Integer> it = tree.getChildren(node).iterator();
		while (it.hasNext()) {
			int child = it.next();

			split = join;
			if (it.hasNext()) {
				join = xorNode(dot);
			} else {
				join = sink;
			}

			convertNode(dot, tree, child, split, join, directionForward);
		}
	}

	private static void convertLoop(Dot dot, EfficientTree tree, int node, DotNode source, DotNode sink,
			boolean directionForward) throws UnknownTreeNodeException {

		//operator split
		DotNode split = xorNode(dot);
		addArc(dot, tree, source, split, node, directionForward, true);

		//operator join
		DotNode join = xorNode(dot);

		int bodyChild = tree.getChild(node, 0);
		convertNode(dot, tree, bodyChild, split, join, directionForward);

		int redoChild = tree.getChild(node, 1);
		convertNode(dot, tree, redoChild, join, split, !directionForward);

		int exitChild = tree.getChild(node, 2);
		convertNode(dot, tree, exitChild, join, sink, directionForward);

	}

	private static void convertConcurrent(Dot dot, EfficientTree tree, int node, DotNode source, DotNode sink,
			boolean directionForward) throws UnknownTreeNodeException {

		//operator split
		DotNode split = diamondNode(dot, "+");
		addArc(dot, tree, source, split, node, directionForward, true);

		//operator join
		DotNode join = diamondNode(dot, "+");
		addArc(dot, tree, join, sink, node, directionForward, true);

		for (int child : tree.getChildren(node)) {
			convertNode(dot, tree, child, split, join, directionForward);
		}
	}

	private static void convertInterleaved(Dot dot, EfficientTree tree, int node, DotNode source, DotNode sink,
			boolean directionForward) throws UnknownTreeNodeException {

		//operator split
		DotNode split = diamondNode(dot, "\u2194");
		addArc(dot, tree, source, split, node, directionForward, true);

		//operator join
		DotNode join = diamondNode(dot, "\u2194");
		addArc(dot, tree, join, sink, node, directionForward, true);

		for (int child : tree.getChildren(node)) {
			convertNode(dot, tree, child, split, join, directionForward);
		}
	}

	private static void convertOr(Dot dot, EfficientTree tree, int node, DotNode source, DotNode sink,
			boolean directionForward) throws UnknownTreeNodeException {

		//operator split
		DotNode split = diamondNode(dot, "o");
		addArc(dot, tree, source, split, node, directionForward, true);

		//operator join
		DotNode join = diamondNode(dot, "o");
		addArc(dot, tree, join, sink, node, directionForward, true);

		for (int child : tree.getChildren(node)) {
			convertNode(dot, tree, child, split, join, directionForward);
		}
	}

	private static void convertXor(Dot dot, EfficientTree tree, int node, DotNode source, DotNode sink,
			boolean directionForward) throws UnknownTreeNodeException {

		//operator split
		DotNode split = xorNode(dot);
		addArc(dot, tree, source, split, node, directionForward, true);

		//operator join
		DotNode join = xorNode(dot);
		addArc(dot, tree, join, sink, node, directionForward, true);

		for (int child : tree.getChildren(node)) {
			convertNode(dot, tree, child, split, join, directionForward);
		}
	}

	private static DotEdge addArc(Dot dot, EfficientTree tree, final DotNode from, final DotNode to, final int node,
			boolean directionForward, boolean includeModelMoves) throws UnknownTreeNodeException {
		DotEdge edge;
		if (directionForward) {
			edge = dot.addEdge(from, to);
		} else {
			edge = dot.addEdge(to, from);
			edge.setOption("dir", "back");
		}
		return edge;
	}

}
