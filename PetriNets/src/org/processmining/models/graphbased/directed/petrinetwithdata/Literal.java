package org.processmining.models.graphbased.directed.petrinetwithdata;

public class Literal {
	Predicate pred;
	Boolean negated;

	public Literal(Predicate pred, Boolean negated) {
		this.pred = pred;
		this.negated = negated;
	}

	public Predicate getPred() {
		return pred;
	}

	public Boolean isNegated() {
		return negated;
	}

	public String toString() {
		String result = pred.toString();

		if (negated) {
			result = "!" + result;
		}

		return result;
	}

	public boolean equals(Literal l) {
		return l.getPred().equals(pred) && (l.isNegated() == negated);
	}
}
