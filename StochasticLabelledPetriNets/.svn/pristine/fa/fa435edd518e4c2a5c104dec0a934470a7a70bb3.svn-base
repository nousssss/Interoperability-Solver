package org.processmining.stochasticlabelledpetrinets.probability;

import java.util.ArrayList;
import java.util.List;

import org.processmining.ltl2automaton.plugins.automaton.Automaton;
import org.processmining.ltl2automaton.plugins.automaton.State;
import org.processmining.ltl2automaton.plugins.automaton.Transition;
import org.processmining.ltl2automaton.plugins.formula.DefaultParser;
import org.processmining.ltl2automaton.plugins.formula.Formula;
import org.processmining.ltl2automaton.plugins.formula.conjunction.ConjunctionFactory;
import org.processmining.ltl2automaton.plugins.formula.conjunction.ConjunctionTreeLeaf;
import org.processmining.ltl2automaton.plugins.formula.conjunction.ConjunctionTreeNode;
import org.processmining.ltl2automaton.plugins.formula.conjunction.DefaultTreeFactory;
import org.processmining.ltl2automaton.plugins.formula.conjunction.GroupedTreeConjunction;
import org.processmining.ltl2automaton.plugins.formula.conjunction.TreeFactory;
import org.processmining.ltl2automaton.plugins.ltl.SyntaxParserException;

public abstract class FollowerSemanticsLTL implements FollowerSemantics<State> {

	private final Automaton automaton;
	private final boolean tausAllowed;

	public FollowerSemanticsLTL(String formula, boolean tausAllowed) {
		List<Formula> formulaeParsed = new ArrayList<Formula>();
		try {
			formulaeParsed.add(new DefaultParser(formula).parse());
		} catch (SyntaxParserException e) {
			e.printStackTrace();
		}
		TreeFactory<ConjunctionTreeNode, ConjunctionTreeLeaf> treeFactory = DefaultTreeFactory.getInstance();
		ConjunctionFactory<? extends GroupedTreeConjunction> conjunctionFactory = GroupedTreeConjunction
				.getFactory(treeFactory);
		GroupedTreeConjunction conjunction = conjunctionFactory.instance(formulaeParsed);
		Automaton automaton = conjunction.getAutomaton().op.reduce();
		this.automaton = automaton;
		this.tausAllowed = tausAllowed;
	}

	public State getInitialState() {
		return automaton.getInit();
	}

	public State takeStep(State state, String label) {
		// this method returns null if there is no outgoing transitions from state labeled with label
		State outputState = null;
		if (tausAllowed && label == null) {
			return state;
		} else {
			for (Transition out : state.getOutput()) {
				if (out.parses(label)) {
					outputState = out.getTarget();
					break;
				}
			}
			return outputState;
		}
	}

	public boolean isFinalState(State state) {
		return state.isAccepting();
	}

}
