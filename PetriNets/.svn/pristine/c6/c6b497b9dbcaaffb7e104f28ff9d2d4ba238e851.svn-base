package org.processmining.models.graphbased.directed.petrinetwithdata;

import java.util.HashSet;
import java.util.Set;

public class Guard {
	Set<Set<Literal>> conjuncts;

	public Guard() {
		conjuncts = new HashSet<Set<Literal>>();

	}

	public void addConjunct(Set<Literal> conjunct) {
		conjuncts.add(conjunct);
	}

	public Set<Set<Literal>> getConjuncts() {
		return conjuncts;
	}

	public void addGuardOR(Guard grd) {
		conjuncts.addAll(grd.getConjuncts());
	}

	public void addGuardAND(Guard grd) {
		Set<Literal> c;
		Set<Set<Literal>> new_conjuncts = new HashSet<Set<Literal>>();
		Set<Set<Literal>> conjuncts_2 = grd.getConjuncts();

		if (conjuncts.isEmpty() || conjuncts_2.isEmpty()) {
			conjuncts.addAll(conjuncts_2);
		} else {
			for (Set<Literal> c1 : conjuncts) {
				for (Set<Literal> c2 : conjuncts_2) {
					c = new HashSet<Literal>();
					c.addAll(c1);
					c.addAll(c2);
					new_conjuncts.add(c);
				}
			}
			conjuncts = new_conjuncts;
		}

	}

	public Set<DataElement> dataOf() {
		Set<DataElement> result = new HashSet<DataElement>();

		for (Set<Literal> conj : conjuncts) {
			for (Literal lit : conj) {
				result.addAll(lit.getPred().getDepData());
			}
		}

		return result;

	}

	public String toString() {
		String result = "";

		if (conjuncts.isEmpty()) {
			result = "TRUE";
		} else {
			for (Set<Literal> c : conjuncts) {
				String conStr = "(";

				for (Literal lit : c) {
					if (conStr.equals("(")) {
						conStr = conStr + lit.toString();
					} else {
						conStr = conStr + " && " + lit.toString();
					}
				}
				conStr = conStr + ")";

				if (result.equals("")) {
					result = conStr;
				} else {
					result = result + " \\/ " + conStr;
				}
			}
		}
		return result;
	}
}
