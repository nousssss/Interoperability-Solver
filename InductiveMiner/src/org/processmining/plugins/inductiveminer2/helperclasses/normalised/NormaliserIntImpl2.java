package org.processmining.plugins.inductiveminer2.helperclasses.normalised;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class NormaliserIntImpl2 implements NormaliserInt2 {

	private TIntIntHashMap vertex2normalIndex = new TIntIntHashMap(10, 0.5f, Integer.MIN_VALUE, Integer.MIN_VALUE);
	private TIntArrayList normalIndex2vertex = new TIntArrayList();

	public int add(int node) {
		int oldNormalised = vertex2normalIndex.putIfAbsent(node, normalIndex2vertex.size());
		if (oldNormalised != Integer.MIN_VALUE) {
			return oldNormalised;
		}

		normalIndex2vertex.add(node);
		return normalIndex2vertex.size() - 1;
	}

	public int toNormal(int node) {
		return vertex2normalIndex.get(node);
	}

	public int getNumberOfActivities() {
		return normalIndex2vertex.size();
	}

	public int deNormalise(int normalisedIndex) {
		return normalIndex2vertex.get(normalisedIndex);
	}

	public List<TIntSet> deNormalise(List<TIntSet> partition) {
		List<TIntSet> result = new ArrayList<>();
		for (TIntSet part : partition) {
			final TIntSet newPart = new TIntHashSet(part.size(), 0.5f, Integer.MIN_VALUE);
			part.forEach(new TIntProcedure() {
				public boolean execute(int value) {
					newPart.add(normalIndex2vertex.get(value));
					return true;
				}
			});
			result.add(newPart);
		}
		return result;
	}

	public Cut deNormalise(Cut newCut) {
		return new Cut(newCut.getOperator(), deNormalise(newCut.getPartition()));
	}

	public NormaliserIntImpl2 clone() {
		NormaliserIntImpl2 result;
		try {
			result = (NormaliserIntImpl2) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

		result.vertex2normalIndex = new TIntIntHashMap(vertex2normalIndex);
		result.normalIndex2vertex = new TIntArrayList(normalIndex2vertex);

		return result;
	}
}
