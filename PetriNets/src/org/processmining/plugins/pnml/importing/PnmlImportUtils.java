package org.processmining.plugins.pnml.importing;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.connections.opennet.OpenNetConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.opennet.OpenNet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.FullPnmlElementFactory;
import org.processmining.plugins.pnml.base.Pnml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class PnmlImportUtils {
	public Pnml importPnmlFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {

		FullPnmlElementFactory pnmlFactory = new FullPnmlElementFactory();
		return importPnmlFromStream(context, pnmlFactory, input, filename, fileSizeInBytes);
	}

	public Pnml importPnmlFromStream(PluginContext context, FullPnmlElementFactory pnmlFactory, InputStream input,
			String filename, long fileSizeInBytes) throws Exception {
		/*
		 * Get an XML pull parser.
		 */
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		/*
		 * Initialize the parser on the provided input.
		 */
		xpp.setInput(input, null);
		/*
		 * Get the first event type.
		 */
		int eventType = xpp.getEventType();
		/*
		 * Create a fresh PNML object.
		 */
		Pnml pnml = new Pnml();
		synchronized (pnmlFactory) {
			pnml.setFactory(pnmlFactory);

			/*
			 * Skip whatever we find until we've found a start tag.
			 */
			while (eventType != XmlPullParser.START_TAG) {
				eventType = xpp.next();
			}
			/*
			 * Check whether start tag corresponds to PNML start tag.
			 */
			if (xpp.getName().equals(Pnml.TAG)) {
				/*
				 * Yes it does. Import the PNML element.
				 */
				pnml.importElement(xpp, pnml);
			} else {
				/*
				 * No it does not. Return null to signal failure.
				 */
				pnml.log(Pnml.TAG, xpp.getLineNumber(), "Expected pnml");
			}
			if (pnml.hasErrors()) {
				context.getProvidedObjectManager().createProvidedObject("Log of PNML import", pnml.getLog(),
						XLog.class, context);
				return null;
			}
			return pnml;
		}
	}

	protected Pnml createPnml() {
		Pnml pnml = new Pnml();
		pnml.setFactory(new FullPnmlElementFactory());
		return pnml;
	}

	public Object connectNet(PluginContext context, Pnml pnml, PetrinetGraph net) {
		/*
		 * Create fresh marking(s) and layout.
		 */
		Marking marking = new Marking();
		Collection<Marking> finalMarkings = new HashSet<Marking>();
		GraphLayoutConnection layout = new GraphLayoutConnection(net);

		/*
		 * Initialize the Petri net, marking(s), and layout from the PNML
		 * element.
		 */
		pnml.convertToNet(net, marking, finalMarkings, layout);

		/*
		 * Add a connection from the Petri net to the marking(s) and layout.
		 */
		context.addConnection(new InitialMarkingConnection(net, marking));
		for (Marking finalMarking : finalMarkings) {
			context.addConnection(new FinalMarkingConnection(net, finalMarking));
		}
		context.addConnection(layout);

		/*
		 * Set the label of the Petri net.
		 */
		context.getFutureResult(0).setLabel(net.getLabel());
		/*
		 * set the label of the marking.
		 */
		context.getFutureResult(1).setLabel("Marking of " + net.getLabel());

		/*
		 * Return the net and the marking.
		 */
		Object[] objects = new Object[2];
		objects[0] = net;
		objects[1] = marking;
		return objects;
	}

	public Object connectOpenNet(PluginContext context, Pnml pnml, OpenNet openNet) {
		/*
		 * Create a fresh marking.
		 */
		Marking marking = new Marking();
		Collection<Marking> finalMarkings = new HashSet<Marking>();

		GraphLayoutConnection layout = new GraphLayoutConnection(openNet);
		/*
		 * Initialize the Petri net and marking from the PNML element.
		 */
		pnml.convertToNet(openNet, marking, finalMarkings, layout);

		/*
		 * Add a connection from the Petri net to the marking.
		 */
		context.addConnection(new InitialMarkingConnection(openNet, marking));
		for (Marking finalMarking : finalMarkings) {
			context.addConnection(new FinalMarkingConnection(openNet, finalMarking));
		}
		context.addConnection(layout);

		/*
		 * Add a self-connection from the open net to the open net. This results
		 * in the ports and final markings being shown.
		 */
		context.addConnection(new OpenNetConnection(openNet, openNet));
		/*
		 * Set the label of the Petri net.
		 */
		context.getFutureResult(0).setLabel(openNet.getLabel());
		/*
		 * set the label of the marking.
		 */
		context.getFutureResult(1).setLabel("Marking of " + openNet.getLabel());

		/*
		 * Return the net and the marking.
		 */
		Object[] objects = new Object[2];
		objects[0] = openNet;
		objects[1] = marking;
		return objects;
	}
}
