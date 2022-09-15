package org.processmining.plugins.pnml.elements.extensions.opennet;

import java.util.Map;

import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.processmining.plugins.pnml.elements.PnmlText;
import org.xmlpull.v1.XmlPullParser;

public class PnmlMarkedPlace extends PnmlElement {

	public final static String TAG = "place";

	private String idref;

	private PnmlText text;

	protected PnmlMarkedPlace() {
		super(TAG);

		idref = null;

		text = null;
	}

	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		String value = xpp.getAttributeValue(null, "idref");
		if (value == null) {
			// used to be id instead of idref. included for backwards compatibility
			value = xpp.getAttributeValue(null, "id");
		}
		if (value != null) {
			idref = value;
		}
	}

	protected String exportAttributes(Pnml pnml) {
		String s = super.exportAttributes(pnml);
		if (idref != null) {
			s += exportAttribute("idref", idref, pnml);
		}
		return s;
	}

	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		if (super.importElements(xpp, pnml)) {
			/*
			 * Start tag corresponds to a known child element of a PNML
			 * annotation.
			 */
			return true;
		}
		if (xpp.getName().equals(PnmlText.TAG)) {
			text = factory.createPnmlText();
			text.importElement(xpp, pnml);
			return true;
		}
		return false;
	}

	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		if (text != null) {
			s += text.exportElement(pnml);
		}
		return s;
	}

	public void convertToOpenNet(Marking marking, Map<String, Place> placeMap) {
		try {
			Place place = placeMap.get(idref);
			int weight = Integer.valueOf(text.getText());
			if (weight > 0) {
				marking.add(place, weight);
			}
		} catch (Exception ex) {
			// Ignore, either place not found or no proper weight specified.
		}
	}

	public PnmlMarkedPlace convertFromOpenNet(String id, int nofTokens) {
		idref = id;
		text = factory.createPnmlText();
		text.setText("" + nofTokens);
		return this;
	}
}
