package org.processmining.plugins.inductiveminer2.mining;

import java.util.ArrayList;
import java.util.List;

import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinder;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.CutFinder;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThrough;
import org.processmining.plugins.inductiveminer2.framework.postprocessor.PostProcessor;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;

import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;

public class InductiveMiner {

	/**
	 * External users: please do not use this method but use the one in
	 * InductiveMinerPlugin. That one will be kept stable, this one will not.
	 * 
	 * @param log
	 * @param parameters
	 * @param canceller
	 * @return
	 */
	public static EfficientTree mineEfficientTree(IMLog log, MiningParameters parameters, Canceller canceller) {
		//repair life cycle if necessary
		if (parameters.isRepairLifeCycles()) {
			//log = new LifeCycles(parameters.isDebug()).preProcessLog(log);
			System.out.println("life cycle repair not yet implemented");
		}

		MinerState minerState = new MinerState(parameters, canceller);

		if (canceller.isCancelled()) {
			minerState.shutdownThreadPools();
			return null;
		}

		EfficientTree tree = mineNode(log, minerState);

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

	public static EfficientTree mineNode(final IMLog log, MinerState minerState) {
		//construct basic information about log
		IMLogInfo logInfo = minerState.parameters.getLog2LogInfo().createLogInfo(log);

		//output information about the log
		debug("\nMine epsilon=" + logInfo.getDfg().getNumberOfEmptyTraces() + ", "
				+ logInfo.getDfg().getActivities().toString(), minerState);
		debug(log, minerState);

		//find base cases
		EfficientTree baseCase = findBaseCases(log, logInfo, minerState);
		if (baseCase != null) {

			postProcess(baseCase, log, logInfo, minerState);

			debug(" discovered node " + baseCase, minerState);
			return baseCase;
		}

		if (minerState.isCancelled()) {
			return null;
		}

		//find cut
		Cut cut = findCut(log, logInfo, minerState.parameters.getCutFinders(), minerState);

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
						s.add(log.getActivity(value));
						return true;
					}
				});
				cutDebug.add(s);
			}
			debug(" chosen cut: " + cut + "; " + cutDebug, minerState);

			//split logs
			IMLog[] sublogs = splitLog(log, logInfo, cut, minerState);

			if (minerState.isCancelled()) {
				return null;
			}

			//make node
			EfficientTree result;

			//recurse
			if (cut.getOperator() != Operator.loop) {
				List<EfficientTree> children = new ArrayList<>();
				for (IMLog sublog : sublogs) {
					children.add(mineNode(sublog, minerState));

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
				IMLog bodySublog = sublogs[0];
				EfficientTree bodyChild = mineNode(bodySublog, minerState);

				if (minerState.isCancelled()) {
					return null;
				}

				//mine redo parts by, if necessary, putting them under an xor
				EfficientTree redoChild;
				{
					List<EfficientTree> redoChildren = new ArrayList<>();
					for (int i = 1; i < sublogs.length; i++) {
						IMLog sublog = sublogs[i];
						redoChildren.add(mineNode(sublog, minerState));

						if (minerState.isCancelled()) {
							return null;
						}
					}
					if (sublogs.length > 2) {
						redoChild = InlineTree.xor(redoChildren);
					} else {
						redoChild = redoChildren.get(0);
					}
				}

				//third child is always tau

				result = InlineTree.loop(bodyChild, redoChild, InlineTree.tau());
			}

			postProcess(result, log, logInfo, minerState);

			debug(" discovered node " + result, minerState);
			return result;

		} else {
			//cut is not valid; fall through
			EfficientTree result = findFallThrough(log, logInfo, minerState);

			postProcess(result, log, logInfo, minerState);

			debug(" discovered node " + result, minerState);
			return result;
		}
	}

	public static EfficientTree findBaseCases(IMLog log, IMLogInfo logInfo, MinerState minerState) {
		for (BaseCaseFinder baseCaseFinder : minerState.parameters.getBaseCaseFinders()) {
			EfficientTree baseCase = baseCaseFinder.findBaseCases(log, logInfo, minerState);

			if (minerState.isCancelled()) {
				return null;
			}

			if (baseCase != null) {
				return baseCase;
			}
		}
		return null;
	}

	public static Cut findCut(IMLog log, IMLogInfo logInfo, Iterable<CutFinder> cutFinders, MinerState minerState) {
		for (CutFinder cutFinder : cutFinders) {
			Cut cut = cutFinder.findCut(log, logInfo, minerState);

			if (cut != null && cut.isValid()) {
				return cut;
			}

			if (minerState.isCancelled()) {
				return null;
			}
		}
		return null;
	}

	public static IMLog[] splitLog(IMLog log, IMLogInfo logInfo, Cut cut, MinerState minerState) {

		if (minerState.isCancelled()) {
			return null;
		}

		switch (cut.getOperator()) {
			case concurrent :
				return minerState.parameters.splitLogConcurrent(log, logInfo, cut.getPartition(), minerState);
			case interleaved :
				return minerState.parameters.splitLogInterleaved(log, logInfo, cut.getPartition(), minerState);
			case loop :
				return minerState.parameters.splitLogLoop(log, logInfo, cut.getPartition(), minerState);
			case or :
				return minerState.parameters.splitLogOr(log, logInfo, cut.getPartition(), minerState);
			case sequence :
				return minerState.parameters.splitLogSequence(log, logInfo, cut.getPartition(), minerState);
			case xor :
				return minerState.parameters.splitLogXor(log, logInfo, cut.getPartition(), minerState);
			default :
				break;
		}
		throw new RuntimeException("not available");
	}

	public static EfficientTree findFallThrough(IMLog log, IMLogInfo logInfo, MinerState minerState) {
		for (FallThrough fallThrough : minerState.parameters.getFallThroughs()) {
			EfficientTree result = fallThrough.fallThrough(log, logInfo, minerState);
			if (result != null) {
				return result;
			}

			if (minerState.isCancelled()) {
				return null;
			}
		}
		return null;
	}

	private static void postProcess(EfficientTree tree, IMLog log, IMLogInfo logInfo, MinerState minerState) {
		for (PostProcessor processor : minerState.parameters.getPostProcessors()) {
			processor.postProcess(tree, log, logInfo, minerState);
		}
	}

	public static void debug(Object x, MinerState minerState) {
		if (minerState.parameters.isDebug()) {
			System.out.println(x.toString());
		}
	}
}
