package org.processmining.plugins.inductiveminer2.attributes;

public abstract class AttributeAbstract implements Attribute {

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(Attribute arg0) {
		return getName().toLowerCase().compareTo(arg0.getName().toLowerCase());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isVirtual() ? 1231 : 1237);
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AttributeAbstract other = (AttributeAbstract) obj;
		if (isVirtual() != other.isVirtual()) {
			return false;
		}
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

}