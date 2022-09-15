package org.processmining.plugins.pnml.elements;

import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.AttributeMap;

/**
 * Basic PNML name object.
 * 
 * @author hverbeek
 */
public class PnmlName extends PnmlAnnotation {

	/**
	 * PNML name tag.
	 */
	public final static String TAG = "name";

	/**
	 * Creates a fresh PNML name.
	 */
	protected PnmlName() {
		super(TAG);
	}

	protected PnmlName(String text) {
		super(text, TAG);
	}

	protected String getName(String defaultName) {
		if (text != null) {
			return text.getText();
		}
		return defaultName;
	}

	public PnmlName convertFromNet(AbstractGraphElement element) {
		PnmlName result = null;
		if (element.getAttributeMap().containsKey(AttributeMap.LABEL)) {
			text = factory.createPnmlText(element.getAttributeMap().get(AttributeMap.LABEL, ""));
			result = this;
		}
		return result;
	}

	protected void setName(String name) {
		if (text != null) {
			text.setText(name);
		}
	}
}
