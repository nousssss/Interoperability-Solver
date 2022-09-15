/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2006 TU/e Eindhoven * and is licensed under the * Common
 * Public License, Version 1.0 * by Eindhoven University of Technology *
 * Department of Information Systems * http://is.tm.tue.nl * *
 **********************************************************/
package org.processmining.plugins.petrinet.importing.tpn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * Parses a TPN file and produces a petri net.
 * <p>
 * This implementation parses bounds and transition labels, but does not use
 * them. Undeclared places will be created automatically without warning if they
 * occur in the in set or out set of a transition. The parser is case
 * insensitive.
 * <p>
 * The parser is built with JavaCC, a free Java parser generator (like yacc).
 * See https://javacc.dev.java.net/ for documentation.
 * <p>
 * The grammar file for the TPN parser is TpnParser.jj. The TpnParser class can
 * be rebuilt with the command javacc TpnParser.jj.
 * 
 * @author Peter van den Brand
 * @version 1.0
 */

@Plugin(name = "Open TPN file", parameterLabels = { "Filename" }, returnLabels = { "Petrinet", "Initial Marking" }, returnTypes = {
		Petrinet.class, Marking.class })
@UIImportPlugin(description = "TPN files", extensions = { "tpn" })
public class TpnImport extends AbstractImportPlugin {

	/**
	 * This value is used to indicate the event type of an invisible transition.
	 */
	public final static String INVISIBLE_EVENT_TYPE = "$invisible$";

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		context.log("TPN Import started.");
		Petrinet petrinet = PetrinetFactory.newPetrinet(filename);
		context.getFutureResult(0).setLabel(filename);
		context.getFutureResult(1).setLabel("Initial Marking of " + filename);
		TpnParser parser = new TpnParser(input);
		// LogEvents logEvents = null;

		try {
			parser.start(petrinet);
			Marking state = parser.getState();
			// context.getProvidedObjectManager().createProvidedObject("Initial
			// marking of "+p.getLabel(), state, context);

			// logEvents = new LogEvents();
			Iterator<? extends Transition> it = petrinet.getTransitions().iterator();

			while (it.hasNext()) {
				Transition t = it.next();
				String s = t.getLabel();

				String DELIM = "\\n";
				int i = s.indexOf(DELIM);
				if ((i == s.lastIndexOf(DELIM)) && (i > 0)) {

					String s2 = s.substring(i + DELIM.length(), s.length());

					if (s2.equals(INVISIBLE_EVENT_TYPE)) {
						t.setInvisible(true);
					}
				}
			}

			// p.Test("TpnImport");

			context.log("TPN Import finished.");

			Connection c = new InitialMarkingConnection(petrinet, state);
			context.addConnection(c);
			return new Object[] { petrinet, state };
			// return new ConnectedObjects(c);
		} catch (Throwable x) {
			throw new IOException(x.getMessage());
		}
	}

}
