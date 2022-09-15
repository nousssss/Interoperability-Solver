package org.processmining.plugins.pnml.elements.extensions.opennet;

import java.util.Map;

import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.opennet.OpenNet;
import org.processmining.models.graphbased.directed.opennet.OpenNetLabel;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

public abstract class PnmlLabelConnection extends PnmlElement {

	protected PnmlLabelConnection(String tag) {
		super(tag);
	}

	protected String idRef;

	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		String value = xpp.getAttributeValue(null, "idref");
		if (value == null) {
			// used to be id instead of idref. included for backwards compatibility
			value = xpp.getAttributeValue(null, "id");
		}
		if (value != null) {
			idRef = value;
		}
	}

	protected String exportAttributes(Pnml pnml) {
		String s = super.exportAttributes(pnml);
		if (idRef != null) {
			s += exportAttribute("idref", idRef, pnml);
		}
		return s;
	}

	public void convertToOpenNet(OpenNet net, Transition transition) {
		net.addConnection(transition, net.getInterface().findLabel(idRef));
	}

	public PnmlLabelConnection convertFromOpenNet(OpenNetLabel label,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> map) {
		idRef = label.getId();
		return this;
	}

	public static class Receive extends PnmlLabelConnection {
		public final static String TAG = "receive";

		protected Receive() {
			super(TAG);

			idRef = null;
		}

	}

	public static class Send extends PnmlLabelConnection {
		public final static String TAG = "send";

		protected Send() {
			super(TAG);

			idRef = null;
		}

	}

	public static class Sync extends PnmlLabelConnection {
		public final static String TAG = "synchronize";

		protected Sync() {
			super(TAG);

			idRef = null;
		}

	}
}