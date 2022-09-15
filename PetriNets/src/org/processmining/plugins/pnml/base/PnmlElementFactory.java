package org.processmining.plugins.pnml.base;

import org.processmining.plugins.pnml.elements.PnmlArc;
import org.processmining.plugins.pnml.elements.PnmlName;
import org.processmining.plugins.pnml.elements.PnmlNet;
import org.processmining.plugins.pnml.elements.PnmlPage;
import org.processmining.plugins.pnml.elements.PnmlPlace;
import org.processmining.plugins.pnml.elements.PnmlReferencePlace;
import org.processmining.plugins.pnml.elements.PnmlReferenceTransition;
import org.processmining.plugins.pnml.elements.PnmlText;
import org.processmining.plugins.pnml.elements.PnmlTransition;
import org.processmining.plugins.pnml.elements.extensions.PnmlArcType;
import org.processmining.plugins.pnml.elements.extensions.PnmlInitialMarking;
import org.processmining.plugins.pnml.elements.extensions.PnmlInscription;
import org.processmining.plugins.pnml.elements.extensions.configurations.PnmlConfiguration;
import org.processmining.plugins.pnml.elements.extensions.configurations.PnmlFeature;
import org.processmining.plugins.pnml.elements.extensions.configurations.PnmlGroup;
import org.processmining.plugins.pnml.elements.extensions.configurations.PnmlParameter;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlFinalMarking;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlFinalMarkings;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlLabel;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlLabelConnection;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlMarkedPlace;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlModule;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlPort;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlPorts;
import org.processmining.plugins.pnml.elements.graphics.PnmlAnnotationGraphics;
import org.processmining.plugins.pnml.elements.graphics.PnmlArcGraphics;
import org.processmining.plugins.pnml.elements.graphics.PnmlDimension;
import org.processmining.plugins.pnml.elements.graphics.PnmlFill;
import org.processmining.plugins.pnml.elements.graphics.PnmlFont;
import org.processmining.plugins.pnml.elements.graphics.PnmlLine;
import org.processmining.plugins.pnml.elements.graphics.PnmlNodeGraphics;
import org.processmining.plugins.pnml.elements.graphics.PnmlOffset;
import org.processmining.plugins.pnml.elements.graphics.PnmlPosition;
import org.processmining.plugins.pnml.toolspecific.PnmlToolSpecific;

public interface PnmlElementFactory {

	public abstract PnmlModule createPnmlModule();

	public abstract PnmlNet createPnmlNet();

	public abstract PnmlAnnotationGraphics createPnmlAnnotationGraphics();

	public abstract PnmlArcGraphics createPnmlArcGraphics();

	public abstract PnmlInscription createPnmlInscription();

	public abstract PnmlArcType createPnmlArcType(String tag);

	public abstract PnmlToolSpecific createPnmlToolSpecific();

	public abstract PnmlName createPnmlName();

	public abstract PnmlName createPnmlName(String name);

	public abstract PnmlReferenceTransition createPnmlReferenceTransition();

	public abstract PnmlTransition createPnmlTransition();

	public abstract PnmlPlace createPnmlPlace();

	public abstract PnmlArc createPnmlArc();

	public abstract PnmlLabelConnection.Sync createPnmlLabelConnectionSync();

	public abstract PnmlLabelConnection.Send createPnmlLabelConnectionSend();

	public abstract PnmlLabelConnection.Receive createPnmlLabelConnectionReceive();

	public abstract PnmlText createPnmlText(String text);

	public abstract PnmlText createPnmlText();

	public abstract PnmlGroup createPnmlGroup();

	public abstract PnmlParameter createPnmlParameter();

	public abstract PnmlFeature createPnmlFeature();

	public abstract PnmlMarkedPlace createPnmlMarkedPlace();

	public abstract PnmlPorts createPnmlPorts();

	public abstract PnmlLabel.Input createPnmlLabelInput();

	public abstract PnmlLabel.Output createPnmlLabelOutput();

	public abstract PnmlLabel.Sync createPnmlLabelSync();

	public abstract PnmlPosition createPnmlPosition();

	public abstract PnmlPage createPnmlPage();

	public abstract PnmlReferencePlace createPnmlReferencePlace();

	public abstract PnmlFinalMarkings createPnmlFinalMarkings();

	public abstract PnmlPort createPnmlPort();

	public abstract PnmlConfiguration createPnmlConfiguration();

	public abstract PnmlInitialMarking createPnmlInitialMarking();

	public abstract PnmlNodeGraphics createPnmlNodeGraphics();

	public abstract PnmlFinalMarking createPnmlFinalMarking();

	public abstract PnmlDimension createPnmlDimension();

	public abstract PnmlFill createPnmlFill();

	public abstract PnmlLine createPnmlLine();

	public abstract PnmlOffset createPnmlOffset();

	public abstract PnmlFont createPnmlFont();

	public abstract PnmlOffset createPnmlOffset(double x, double y);

}