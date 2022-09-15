package org.processmining.petrinets;

/**
 * Specifies the different possible PetriNet File Formats. Mainly used within
 * RapidProM.
 *
 */
public enum PetriNetFileFormat {
	PNML("pnml"), EPNML("epnml");

	private final String format;

	private PetriNetFileFormat(String format) {
		this.format = format;
	}
	
	@Override
	public String toString() {
		return format;
	}

}
