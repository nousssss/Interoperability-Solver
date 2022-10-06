package org.processmining.plugins.inductiveminer2.framework.cutfinders;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;

import gnu.trove.set.TIntSet;

public class Cut {
	
	private List<TIntSet> partition = null;
	private Operator operator = null;

	public boolean isValid() {
		if (getOperator() == null || getPartition().size() <= 1) {
			return false;
		}
		for (TIntSet part : getPartition()) {
			if (part.size() == 0) {
				return false;
			}
		}
		return true;
	}
	
	public Cut(Operator operator, List<TIntSet> partition) {
		this.partition = new ArrayList<>(partition);
		this.operator = operator;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(operator);
		result.append(" ");
		result.append(partition);
		return result.toString();
	}

	public List<TIntSet> getPartition() {
		return partition;
	}

	public void setPartition(List<TIntSet> partition) {
		this.partition = partition;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
}
