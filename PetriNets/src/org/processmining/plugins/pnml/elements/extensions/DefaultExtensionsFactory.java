package org.processmining.plugins.pnml.elements.extensions;

public class DefaultExtensionsFactory {

	public PnmlInitialMarking createPnmlInitialMarking() {
		return new PnmlInitialMarking();
	}

	public PnmlArcType createPnmlArcType(String tag) {
		return new PnmlArcType(tag);
	}

	public PnmlInscription createPnmlInscription() {
		return new PnmlInscription();
	}

}
