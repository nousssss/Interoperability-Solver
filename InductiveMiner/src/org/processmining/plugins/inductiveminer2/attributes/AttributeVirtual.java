package org.processmining.plugins.inductiveminer2.attributes;

import org.deckfour.xes.model.XAttributable;

public abstract class AttributeVirtual extends AttributeAbstract {

	public boolean isVirtual() {
		return true;
	}

	public abstract void add(XAttributable event);

}