package org.processmining.models.graphbased.directed.petrinetwithdata.unfolding;

import java.util.Set;

import org.processmining.models.graphbased.directed.petrinetwithdata.Literal;
import org.processmining.models.graphbased.directed.petrinetwithdata.Predicate;

public class PredicateEffect extends Effect {
	Predicate pred;
	Set<Literal> conj;

	public PredicateEffect(Predicate pred, Set<Literal> conj, String effect) {
		super(effect);
		this.pred = pred;
		this.conj = conj;
	}

	public String toString() {
		return "<" + pred.toString() + ",{" + conj + "}," + effect + ">";
	}
}
