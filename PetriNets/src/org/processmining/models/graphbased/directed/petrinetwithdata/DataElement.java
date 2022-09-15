package org.processmining.models.graphbased.directed.petrinetwithdata;

public class DataElement {
	String value;

	public DataElement(String value) {
		setValue(value);
	}

	public String getValue() {
		return value;
	}

	void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		return value;

	}
}
