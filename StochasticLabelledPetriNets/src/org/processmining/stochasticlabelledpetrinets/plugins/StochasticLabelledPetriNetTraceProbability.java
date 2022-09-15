package org.processmining.stochasticlabelledpetrinets.plugins;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSemantics;
import org.processmining.stochasticlabelledpetrinets.probability.CrossProduct;
import org.processmining.stochasticlabelledpetrinets.probability.CrossProductResultSolver;
import org.processmining.stochasticlabelledpetrinets.probability.FollowerSemanticsTrace;

import lpsolve.LpSolveException;

public class StochasticLabelledPetriNetTraceProbability {
	public static double getTraceProbability(StochasticLabelledPetriNetSemantics semantics, String[] trace,
			ProMCanceller canceller) throws LpSolveException {
		semantics.setInitialState();

		CrossProductResultSolver result = new CrossProductResultSolver();
		FollowerSemanticsTrace systemB = new FollowerSemanticsTrace(trace);
		CrossProduct.traverse(semantics, systemB, result, canceller);
		return result.solve(canceller);
	}
}
