package org.processmining.models.graphbased.directed.opennet;

public class OpenNetLabel implements Comparable<OpenNetLabel> {

	public static enum Type {
		ASYNC_OUTPUT("output", "! "), //
		ASYNC_INPUT("input", "? "), //
		SYNC("synchronous", "# ");

		private final String value;
		private final String prefix;

		private Type(String value, String prefix) {
			this.value = value;
			this.prefix = prefix;
		}

		public static Type fromValue(String value) {
			for (Type t : Type.values()) {
				if (t.value.equals(value)) {
					return t;
				}
			}
			return null;
		}

		public String getValue() {
			return value;
		}

		public String getPrefix() {
			return prefix;
		}
	}

	private final String label;
	private final Type type;
	private final String id;

	public OpenNetLabel(String label, String id, Type type) {
		this.label = label;
		this.id = id;
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public String toString() {
		return type.getPrefix() + label;
	}

	public String getLabel() {
		return label;
	}

	public String getId() {
		return id;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object o) {
		return (o instanceof OpenNetLabel ? ((OpenNetLabel) o).id.equals(id) : false);
	}

	public int compareTo(OpenNetLabel l) {
		if (l.getType().equals(type)) {
			if (l.getLabel().equals(label)) {
				return l.getId().compareTo(id);
			} else {
				return l.getLabel().compareTo(label);
			}
		} else {
			return l.getType().compareTo(type);
		}
	}

}
