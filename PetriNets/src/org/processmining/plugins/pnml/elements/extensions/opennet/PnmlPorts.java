package org.processmining.plugins.pnml.elements.extensions.opennet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.opennet.OpenNet;
import org.processmining.models.graphbased.directed.opennet.OpenNetInterface;
import org.processmining.models.graphbased.directed.opennet.OpenNetPort;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         Pnml ports. Contains just a collection of Pnml ports.
 */
public class PnmlPorts extends PnmlElement {

	public final static String TAG = "ports";

	/*
	 * The collection of Pnml ports.
	 */
	private final List<PnmlPort> portList;

	/**
	 * Construct a fresh Pnml port.
	 */
	protected PnmlPorts() {
		super(TAG);

		portList = new ArrayList<PnmlPort>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.plugins.pnml.PnmlElement#importElements(org.xmlpull
	 * .v1.XmlPullParser, org.processmining.plugins.pnml.Pnml)
	 * 
	 * Imports ports.
	 */
	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		if (super.importElements(xpp, pnml)) {
			/*
			 * Start tag corresponds to a known child element of a PNML
			 * annotation.
			 */
			return true;
		}
		if (xpp.getName().equals(PnmlPort.TAG)) {
			PnmlPort port = factory.createPnmlPort();
			port.importElement(xpp, pnml);
			portList.add(port);
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.plugins.pnml.PnmlElement#exportElements()
	 * 
	 * Exports ports.
	 */
	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		for (PnmlPort port : portList) {
			s += port.exportElement(pnml);
		}
		return s;
	}

	/**
	 * Converts all ports to the given Open net.
	 * 
	 * @param openNet
	 *            The given Open net.
	 */
	public void convertToOpenNet(OpenNet openNet) {
		for (PnmlPort port : portList) {
			port.convertToOpenNet(openNet);
		}
	}

	/**
	 * Converts the given collection of ports to Pnml ports.
	 * 
	 * @param openNetPorts
	 *            The given collection of ports.
	 * @param map
	 *            The id map.
	 * @return The Pnml ports.
	 */
	public PnmlPorts convertFromOpenNet(OpenNetInterface openNetPorts,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> map) {
		for (OpenNetPort openNetPort : openNetPorts) {
			/*
			 * Create a Pnml port for every port found.
			 */
			PnmlPort port = factory.createPnmlPort();
			port.convertFromOpenNet(openNetPort, map);
			portList.add(port);
		}
		return this;
	}
}
