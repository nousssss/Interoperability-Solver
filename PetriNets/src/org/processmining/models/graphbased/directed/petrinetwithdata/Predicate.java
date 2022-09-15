package org.processmining.models.graphbased.directed.petrinetwithdata;

import java.util.HashSet;
import java.util.Set;

public class Predicate {

	String name;
	Set<DataElement> dep_data;

	public Predicate(String name, Set<DataElement> dep_data) {
		setName(name);
		setDepData(dep_data);
	}

	public String getName() {
		return name;
	}

	public Set<DataElement> getDepData() {
		return dep_data;
	}

	void setName(String name) {
		this.name = name;
	}

	void setDepData(Set<DataElement> dep_data) {
		this.dep_data = new HashSet<DataElement>(dep_data);
	}

	public String toString() {
		String result = name + "(" + dep_data.toString() + ")";

		return result;
	}

	public boolean equals(Predicate p) {
		return (p.getName().equals(name));
	}

	public Literal negated() {
		return new Literal(this, true);

	}
}
