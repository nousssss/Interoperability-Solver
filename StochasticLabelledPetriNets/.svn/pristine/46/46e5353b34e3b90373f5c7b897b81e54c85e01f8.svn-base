package org.processmining.stochasticlabelledpetrinets.probability;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSemantics;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSemanticsSimpleWeightsImpl;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSimpleWeightsImpl;
import org.processmining.stochasticlabelledpetrinets.plugins.StochasticLabelledPetriNetImportPlugin;

import lpsolve.LpSolveException;

public class TestTrace {
	public static void main(String[] args)
			throws NumberFormatException, FileNotFoundException, IOException, LpSolveException {
		StochasticLabelledPetriNetSimpleWeightsImpl netA = StochasticLabelledPetriNetImportPlugin.read(new FileInputStream(
				new File("/home/sander/Documents/svn/51 - hybrid stochastic models - marco/PetriNet.slpn")));
		StochasticLabelledPetriNetSemantics semanticsA = new StochasticLabelledPetriNetSemanticsSimpleWeightsImpl(netA);

		String[] trace = new String[] { "a", "c" };
		FollowerSemanticsTrace systemB = new FollowerSemanticsTrace(trace);

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		{
			CrossProductResultDot resultDot = new CrossProductResultDot();
			CrossProduct.traverse(semanticsA, systemB, resultDot, canceller);

			System.out.println(resultDot.toDot());
		}

		{
			CrossProductResultSolver resultSolver = new CrossProductResultSolver();

			CrossProduct.traverse(semanticsA, systemB, resultSolver, canceller);

			System.out.println(resultSolver.solve(canceller));
		}

	}
}