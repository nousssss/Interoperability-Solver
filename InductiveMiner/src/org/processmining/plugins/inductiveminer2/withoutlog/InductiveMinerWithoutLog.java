package org.processmining.plugins.inductiveminer2.withoutlog;

import java.util.ArrayList;
import java.util.List;

import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.withoutlog.basecases.BaseCaseFinderWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;
import org.processmining.plugins.inductiveminer2.withoutlog.fallthroughs.FallThroughWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.postprocessors.PostProcessorWithoutLog;

import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;

public class InductiveMinerWithoutLog {
	/**
	 * External users: please do not use this method but use the one in
	 * InductiveMinerWithoutLogPlugin. That one will be kept stable, this one
	 * will not.
	 * 
	 * @param graph
	 * @param parameters
	 * @param canceller
	 * @return
	 */
	public static EfficientTree mineEfficientTree(DfgMsd graph, MiningParametersWithoutLog parameters,
			Canceller canceller) {
		MinerStateWithoutLog minerState = new MinerStateWithoutLog(parameters, canceller);

		if (canceller.isCancelled()) {
			minerState.shutdownThreadPools();
			return null;
		}

		EfficientTree tree = mineNode(graph, minerState);

		if (canceller.isCancelled()) {
			minerState.shutdownThreadPools();
			return null;
		}

		//reduce the tree
		if (parameters.getReduceParameters() != null) {
			try {
				EfficientTreeReduce.reduce(tree, parameters.getReduceParameters());
				debug("after reduction " + tree.getRoot(), minerState);
			} catch (UnknownTreeNodeException | ReductionFailedException e) {
				e.printStackTrace();
			}
		}

		minerState.shutdownThreadPools();

		if (canceller.isCancelled()) {
			return null;
		}

		debug("discovered tree " + tree, minerState);

		return tree;
	}

	public static EfficientTree mineNode(final DfgMsd graph, MinerStateWithoutLog minerState) {
		//output information about the log
		debug("\nMine epsilon=" + graph.getNumberOfEmptyTraces() + ", " + graph.getActivities().toString(), minerState);
		debug(graph, minerState);

		//find base cases
		EfficientTree baseCase = findBaseCases(graph, minerState);
		if (baseCase != null) {

			postProcess(baseCase, graph, minerState);

			debug(" discovered node " + baseCase, minerState);
			return baseCase;
		}

		if (minerState.isCancelled()) {
			return null;
		}

		//find cut
		Cut cut = findCut(graph, minerState.parameters.getCutFinders(), minerState);

		if (minerState.isCancelled()) {
			return null;
		}

		if (cut != null && cut.isValid()) {
			//cut is valid

			//debug the cut
			final List<List<String>> cutDebug = new ArrayList<>();
			for (TIntSet part : cut.getPartition()) {
				final List<String> s = new ArrayList<>();
				part.forEach(new TIntProcedure() {
					public boolean execute(int value) {
						s.add(graph.getActivityOfIndex(value));
						return true;
					}
				});
				cutDebug.add(s);
			}
			debug(" chosen cut: " + cut + "; " + cutDebug, minerState);

			//split logs
			DfgMsd[] subgraphs = splitLog(graph, cut, minerState);

			if (minerState.isCancelled()) {
				return null;
			}

			//make node
			EfficientTree result;

			//recurse
			if (cut.getOperator() != Operator.loop) {
				List<EfficientTree> children = new ArrayList<>();
				for (DfgMsd subgraph : subgraphs) {
					children.add(mineNode(subgraph, minerState));

					if (minerState.isCancelled()) {
						return null;
					}
				}

				switch (cut.getOperator()) {
					case concurrent :
						result = InlineTree.concurrent(children);
						break;
					case interleaved :
						result = InlineTree.interleaved(children);
						break;
					case loop :
						result = InlineTree.loop(children);
						break;
					case maybeInterleaved :
						throw new UnknownTreeNodeException();
					case or :
						result = InlineTree.or(children);
						break;
					case sequence :
						result = InlineTree.sequence(children);
						break;
					case xor :
						result = InlineTree.xor(children);
						break;
					default :
						throw new UnknownTreeNodeException();
				}
			} else {
				//loop needs special treatment, as process trees have three children

				//mine body
				DfgMsd bodySubgraph = subgraphs[0];
				EfficientTree bodyChild = mineNode(bodySubgraph, minerState);

				if (minerState.isCancelled()) {
					return null;
				}

				//mine redo parts by, if necessary, putting them under an xor
				EfficientTree redoChild;
				{
					List<EfficientTree> redoChildren = new ArrayList<>();
					for (int i = 1; i < subgraphs.length; i++) {
						DfgMsd subgraph = subgraphs[i];
						redoChildren.add(mineNode(subgraph, minerState));

						if (minerState.isCancelled()) {
							return null;
						}
					}
					if (subgraphs.length > 2) {
						redoChild = InlineTree.xor(redoChildren);
					} else {
						redoChild = redoChildren.get(0);
					}
				}

				//third child is always tau

				result = InlineTree.loop(bodyChild, redoChild, InlineTree.tau());
			}

			postProcess(result, graph, minerState);

			debug(" discovered node " + result, minerState);
			return result;

		} else {
			//cut is not valid; fall through
			EfficientTree result = findFallThrough(graph, minerState);

			postProcess(result, graph, minerState);

			debug(" discovered node " + result, minerState);
			return result;
		}
	}

	public static EfficientTree findBaseCases(DfgMsd graph, MinerStateWithoutLog minerState) {
		for (BaseCaseFinderWithoutLog baseCaseFinder : minerState.parameters.getBaseCaseFinders()) {
			EfficientTree baseCase = baseCaseFinder.findBaseCases(graph, minerState);

			if (minerState.isCancelled()) {
				return null;
			}

			if (baseCase != null) {
				return baseCase;
			}
		}
		return null;
	}

	public static Cut findCut(DfgMsd graph, Iterable<CutFinderWithoutLog> cutFinders, MinerStateWithoutLog minerState) {
		for (CutFinderWithoutLog cutFinder : cutFinders) {
			Cut cut = cutFinder.findCut(graph, minerState);

			if (cut != null && cut.isValid()) {
				return cut;
			}

			if (minerState.isCancelled()) {
				return null;
			}
		}
		return null;
	}

	public static DfgMsd[] splitLog(DfgMsd graph, Cut cut, MinerStateWithoutLog minerState) {

		if (minerState.isCancelled()) {
			return null;
		}

		switch (cut.getOperator()) {
			case concurrent :
				return minerState.parameters.splitGraphConcurrent(graph, cut.getPartition(), minerState);
			case interleaved :
				return minerState.parameters.splitGraphInterleaved(graph, cut.getPartition(), minerState);
			case loop :
				return minerState.parameters.splitGraphLoop(graph, cut.getPartition(), minerState);
			case or :
				return minerState.parameters.splitGraphOr(graph, cut.getPartition(), minerState);
			case sequence :
				return minerState.parameters.splitGraphSequence(graph, cut.getPartition(), minerState);
			case xor :
				return minerState.parameters.splitGraphXor(graph, cut.getPartition(), minerState);
			default :
				break;
		}
		throw new RuntimeException("not available");
	}

	public static EfficientTree findFallThrough(DfgMsd graph, MinerStateWithoutLog minerState) {
		for (FallThroughWithoutLog fallThrough : minerState.parameters.getFallThroughs()) {
			EfficientTree result = fallThrough.fallThrough(graph, minerState);
			if (result != null) {
				return result;
			}

			if (minerState.isCancelled()) {
				return null;
			}
		}
		return null;
	}

	private static void postProcess(EfficientTree tree, DfgMsd graph, MinerStateWithoutLog minerState) {
		for (PostProcessorWithoutLog processor : minerState.parameters.getPostProcessors()) {
			processor.postProcess(tree, graph, minerState);
		}
	}

	public static void debug(Object x, MinerStateWithoutLog minerState) {
		if (minerState.parameters.isDebug()) {
			System.out.println(x.toString());
		}
	}
}
