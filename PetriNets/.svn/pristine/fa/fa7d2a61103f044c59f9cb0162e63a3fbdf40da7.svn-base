package org.processmining.plugins.pnml.base;

import org.processmining.plugins.pnml.elements.DefaultPnmlElementFactory;
import org.processmining.plugins.pnml.elements.PnmlArc;
import org.processmining.plugins.pnml.elements.PnmlName;
import org.processmining.plugins.pnml.elements.PnmlNet;
import org.processmining.plugins.pnml.elements.PnmlPage;
import org.processmining.plugins.pnml.elements.PnmlPlace;
import org.processmining.plugins.pnml.elements.PnmlReferencePlace;
import org.processmining.plugins.pnml.elements.PnmlReferenceTransition;
import org.processmining.plugins.pnml.elements.PnmlText;
import org.processmining.plugins.pnml.elements.PnmlTransition;
import org.processmining.plugins.pnml.elements.extensions.DefaultExtensionsFactory;
import org.processmining.plugins.pnml.elements.extensions.PnmlArcType;
import org.processmining.plugins.pnml.elements.extensions.PnmlInitialMarking;
import org.processmining.plugins.pnml.elements.extensions.PnmlInscription;
import org.processmining.plugins.pnml.elements.extensions.configurations.DefaultConfigurationExtensionsFactory;
import org.processmining.plugins.pnml.elements.extensions.configurations.PnmlConfiguration;
import org.processmining.plugins.pnml.elements.extensions.configurations.PnmlFeature;
import org.processmining.plugins.pnml.elements.extensions.configurations.PnmlGroup;
import org.processmining.plugins.pnml.elements.extensions.configurations.PnmlParameter;
import org.processmining.plugins.pnml.elements.extensions.opennet.DefaultOpenNetExtensionsFactory;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlFinalMarking;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlFinalMarkings;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlLabel;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlLabelConnection;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlMarkedPlace;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlModule;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlPort;
import org.processmining.plugins.pnml.elements.extensions.opennet.PnmlPorts;
import org.processmining.plugins.pnml.elements.graphics.DefaultGraphicsFactory;
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

public class FullPnmlElementFactory implements PnmlElementFactory {

	private DefaultPnmlElementFactory elementFactory;
	private DefaultExtensionsFactory extensionsFactory;
	private DefaultConfigurationExtensionsFactory configurationFactory;
	private DefaultOpenNetExtensionsFactory opennetFactory;
	private DefaultGraphicsFactory graphicsFactory;

	public FullPnmlElementFactory() {
		this(new DefaultPnmlElementFactory(), new DefaultExtensionsFactory(),
				new DefaultConfigurationExtensionsFactory(), new DefaultOpenNetExtensionsFactory(),
				new DefaultGraphicsFactory());
	}

	public FullPnmlElementFactory(DefaultPnmlElementFactory elementFactory, DefaultExtensionsFactory extensionsFactory,
			DefaultConfigurationExtensionsFactory configurationFactory, DefaultOpenNetExtensionsFactory openNetFactory,
			DefaultGraphicsFactory graphicsFactory) {
		this.elementFactory = elementFactory;
		this.extensionsFactory = extensionsFactory;
		this.configurationFactory = configurationFactory;
		this.opennetFactory = openNetFactory;
		this.setGraphicsFactory(graphicsFactory);
	}

	public DefaultPnmlElementFactory getElementFactory() {
		return elementFactory;
	}

	public void setElementFactory(DefaultPnmlElementFactory elementFactory) {
		this.elementFactory = elementFactory;
	}

	public DefaultExtensionsFactory getExtensionsFactory() {
		return extensionsFactory;
	}

	public void setExtensionsFactory(DefaultExtensionsFactory extensionsFactory) {
		this.extensionsFactory = extensionsFactory;
	}

	public DefaultConfigurationExtensionsFactory getConfigurationFactory() {
		return configurationFactory;
	}

	public void setConfigurationFactory(DefaultConfigurationExtensionsFactory configurationFactory) {
		this.configurationFactory = configurationFactory;
	}

	public DefaultOpenNetExtensionsFactory getOpennetFactory() {
		return opennetFactory;
	}

	public void setOpennetFactory(DefaultOpenNetExtensionsFactory opennetFactory) {
		this.opennetFactory = opennetFactory;
	}

	public DefaultGraphicsFactory getGraphicsFactory() {
		return graphicsFactory;
	}

	public void setGraphicsFactory(DefaultGraphicsFactory graphicsFactory) {
		this.graphicsFactory = graphicsFactory;
	}

	/* Basic elements */

	public PnmlReferencePlace createPnmlReferencePlace() {
		return elementFactory.createPnmlReferencePlace();
	}

	public PnmlNet createPnmlNet() {
		return elementFactory.createPnmlNet();
	}

	public PnmlToolSpecific createPnmlToolSpecific() {
		return elementFactory.createPnmlToolSpecific();
	}

	public PnmlName createPnmlName() {
		return elementFactory.createPnmlName();
	}

	public PnmlName createPnmlName(String name) {
		return elementFactory.createPnmlName(name);
	}

	public PnmlReferenceTransition createPnmlReferenceTransition() {
		return elementFactory.createPnmlReferenceTransition();
	}

	public PnmlTransition createPnmlTransition() {
		return elementFactory.createPnmlTransition();
	}

	public PnmlPlace createPnmlPlace() {
		return elementFactory.createPnmlPlace();
	}

	public PnmlArc createPnmlArc() {
		return elementFactory.createPnmlArc();
	}

	public PnmlText createPnmlText(String text) {
		return elementFactory.createPnmlText(text);
	}

	public PnmlText createPnmlText() {
		return elementFactory.createPnmlText();
	}

	public PnmlLabel.Output createPnmlLabelOutput() {
		return elementFactory.createPnmlLabelOutput();
	}

	public PnmlLabel.Sync createPnmlLabelSync() {
		return elementFactory.createPnmlLabelSync();
	}

	public PnmlLabel.Input createPnmlLabelInput() {
		return elementFactory.createPnmlLabelInput();
	}

	public PnmlPage createPnmlPage() {
		return elementFactory.createPnmlPage();
	}

	/* Default Configuration Extensions */

	public PnmlInitialMarking createPnmlInitialMarking() {
		return extensionsFactory.createPnmlInitialMarking();
	}

	public PnmlArcType createPnmlArcType(String tag) {
		return extensionsFactory.createPnmlArcType(tag);
	}

	public PnmlInscription createPnmlInscription() {
		return extensionsFactory.createPnmlInscription();
	}

	/* Default Configuration Extensions */

	public PnmlConfiguration createPnmlConfiguration() {
		return configurationFactory.createPnmlConfiguration();
	}

	public PnmlGroup createPnmlGroup() {
		return configurationFactory.createPnmlGroup();
	}

	public PnmlParameter createPnmlParameter() {
		return configurationFactory.createPnmlParameter();
	}

	public PnmlFeature createPnmlFeature() {
		return configurationFactory.createPnmlFeature();
	}

	/* OpenNet Extensions */

	public PnmlLabelConnection.Sync createPnmlLabelConnectionSync() {
		return opennetFactory.createPnmlLabelConnectionSync();
	}

	public PnmlLabelConnection.Send createPnmlLabelConnectionSend() {
		return opennetFactory.createPnmlLabelConnectionSend();
	}

	public PnmlLabelConnection.Receive createPnmlLabelConnectionReceive() {
		return opennetFactory.createPnmlLabelConnectionReceive();
	}

	public PnmlMarkedPlace createPnmlMarkedPlace() {
		return opennetFactory.createPnmlMarkedPlace();
	}

	public PnmlPorts createPnmlPorts() {
		return opennetFactory.createPnmlPorts();
	}

	public PnmlModule createPnmlModule() {
		return opennetFactory.createPnmlModule();
	}

	public PnmlFinalMarkings createPnmlFinalMarkings() {
		return opennetFactory.createPnmlFinalMarkings();
	}

	public PnmlPort createPnmlPort() {
		return opennetFactory.createPnmlPort();
	}

	public PnmlFinalMarking createPnmlFinalMarking() {
		return opennetFactory.createPnmlFinalMarking();
	}

	/* Default Graphics Elements */

	public PnmlAnnotationGraphics createPnmlAnnotationGraphics() {
		return graphicsFactory.createPnmlAnnotationGraphics();
	}

	public PnmlArcGraphics createPnmlArcGraphics() {
		return graphicsFactory.createPnmlArcGraphics();
	}

	public PnmlPosition createPnmlPosition() {
		return graphicsFactory.createPnmlPosition();
	}

	public PnmlNodeGraphics createPnmlNodeGraphics() {
		return graphicsFactory.createPnmlNodeGraphics();
	}

	public PnmlDimension createPnmlDimension() {
		return graphicsFactory.createPnmlDimension();
	}

	public PnmlFill createPnmlFill() {
		return graphicsFactory.createPnmlFill();
	}

	public PnmlLine createPnmlLine() {
		return graphicsFactory.createPnmlLine();
	}

	public PnmlOffset createPnmlOffset() {
		return graphicsFactory.createPnmlOffset();
	}

	public PnmlFont createPnmlFont() {
		return graphicsFactory.createPnmlFont();
	}

	public PnmlOffset createPnmlOffset(double x, double y) {
		return graphicsFactory.createPnmlOffset(x, y);
	}

}
