package org.processmining.petrinets;

/**
 * @see PetriNetFileFormat
 */
@Deprecated
public enum FileFormat {
	PNML("pnml"), EPNML("epnml");

	private final String format;

	private FileFormat(String format) {
		this.format = format;
	}

	@Override
	public String toString() {
		return format;
	}

}
