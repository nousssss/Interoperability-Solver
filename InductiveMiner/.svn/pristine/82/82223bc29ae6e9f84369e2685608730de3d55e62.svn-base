package org.processmining.plugins.inductiveminer2.attributes.virtual;

import java.util.Collection;

import org.deckfour.xes.model.XAttributable;
import org.processmining.plugins.inductiveminer2.attributes.AttributeVirtualTraceNumericAbstract;

public class AttributeVirtualTraceLength extends AttributeVirtualTraceNumericAbstract {

	@Override
	public String getName() {
		return "number of events";
	}

	public double getNumeric(XAttributable x) {
		if (x instanceof Collection<?>) {
			return ((Collection<?>) x).size();
		}
		return -Double.MAX_VALUE;
	}

}