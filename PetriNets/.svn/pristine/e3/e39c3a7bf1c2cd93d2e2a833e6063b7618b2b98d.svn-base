package org.processmining.plugins.pnml.toolspecific;

import java.util.UUID;

import org.processmining.models.graphbased.LocalNodeID;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.PnmlElement;
import org.xmlpull.v1.XmlPullParser;

public class PnmlToolSpecific extends PnmlElement {

	public final static String TAG = "toolspecific";

	protected final static String PROM = "ProM";
	protected final static String VERSION = "6.4";
	protected final static String INVISIBLE = "$invisible$";

	protected String tool;
	protected String version;
	protected String activity;
	protected String localNodeID;

	public PnmlToolSpecific() {
		super(TAG);
		tool = null;
		version = null;
		activity = null;
		localNodeID = null;
	}

	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		importTool(xpp);
		importVersion(xpp);
		importActivity(xpp);
		importLocalNodeID(xpp);
	}

	protected String exportAttributes(Pnml pnml) {
		return super.exportAttributes(pnml) + exportTool(pnml) + exportVersion(pnml) + exportActivity(pnml)
				+ exportLocalNodeID(pnml);
	}

	private void importTool(XmlPullParser xpp) {
		String value = xpp.getAttributeValue(null, "tool");
		if (value != null) {
			tool = value;
		}
	}

	private String exportTool(Pnml pnml) {
		if (tool != null) {
			return exportAttribute("tool", tool, pnml);
		}
		return "";
	}

	private void importVersion(XmlPullParser xpp) {
		String value = xpp.getAttributeValue(null, "version");
		if (value != null) {
			version = value;
		}
	}

	private String exportVersion(Pnml pnml) {
		if (version != null) {
			return exportAttribute("version", version, pnml);
		}
		return "";
	}

	private void importActivity(XmlPullParser xpp) {
		String value = xpp.getAttributeValue(null, "activity");
		if (value != null) {
			activity = value;
		}
	}

	private String exportActivity(Pnml pnml) {
		if (activity != null) {
			return exportAttribute("activity", activity, pnml);
		}
		return "";
	}

	private void importLocalNodeID(XmlPullParser xpp) {
		String value = xpp.getAttributeValue(null, "localNodeID");
		if (value != null) {
			localNodeID = value;
		}
	}

	private String exportLocalNodeID(Pnml pnml) {
		if (localNodeID != null) {
			return exportAttribute("localNodeID", localNodeID, pnml);
		}
		return "";
	}

	protected void checkValidity(Pnml pnml) {
		super.checkValidity(pnml);
		if ((tool == null) || (version == null)) {
			pnml.log(tag, lineNumber, "Expected tool and version");
		}
	}

	public void convertToNet(Transition transition) {
		if (tool.equals(PROM)) {
			if (version.equals(VERSION)) {
				if (activity.equals(INVISIBLE)) {
					transition.setInvisible(true);
				}
				if (localNodeID != null) {
					transition.setLocalID(new LocalNodeID(UUID.fromString(localNodeID)));
				}
			}
		}
	}

	public void convertToNet(Place place) {
		if (tool.equals(PROM)) {
			if (version.equals(VERSION)) {
				if (localNodeID != null) {
					place.setLocalID(new LocalNodeID(UUID.fromString(localNodeID)));
				}
			}
		}
	}

	public void convertToNet(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge) {
		if (tool.equals(PROM)) {
			if (version.equals(VERSION)) {
				if (localNodeID != null) {
					edge.setLocalID(new LocalNodeID(UUID.fromString(localNodeID)));
				}
			}
		}
	}

	public void convertFromNet(Transition transition) {
		tool = PROM;
		version = VERSION;
		activity = transition.isInvisible() ? INVISIBLE : transition.getLabel();
		localNodeID = transition.getLocalID() != null ? transition.getLocalID().toString() : null;
	}

	public void convertFromNet(Place place) {
		tool = PROM;
		version = VERSION;
		localNodeID = place.getLocalID() != null ? place.getLocalID().toString() : null;
	}

	public void convertFromNet(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge) {
		tool = PROM;
		version = VERSION;
		localNodeID = edge.getLocalID() != null ? edge.getLocalID().toString() : null;
	}
}
