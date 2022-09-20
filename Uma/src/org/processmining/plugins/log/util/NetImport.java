package org.processmining.plugins.log.util;

import java.io.File;
import java.io.FileInputStream;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.importing.PnmlImportUtils;

public class NetImport {
	
	public static Object[] loadNet(PluginContext context, String fileName) {
		
		PnmlImportUtils utils = new PnmlImportUtils();
		File pattern = new File(fileName);
		
		Pnml pnml = null;
		try {
			FileInputStream input = new FileInputStream(pattern);
			pnml = utils.importPnmlFromStream(context, input, pattern.getName(), pattern.length());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[Compliance]: could not read "+pattern.getName()+"\n"+e);						
		}
		if (pnml == null) {
			System.out.println("[Compliance]: could not read "+pattern.getName());
			return null;
		}

		PetrinetGraph net = PetrinetFactory.newResetInhibitorNet(pnml.getLabel() + " (imported from " + pattern.getName() + ")");
		
		// populate array with net (promNet[0]) and marking (promNet[1]) and keep this
		// array in memory when calling the replayer: only this way the marking and
		// the connection are available when the replayer is called, otherwise the
		// initial marking gets lost
		Object[] promNet = null; 
		net = PetrinetFactory.newPetrinet(pnml.getLabel() + " (imported from " + pattern.getName() + ")");
		promNet = connectNet(context, pnml, net);
		return promNet;
	}
	
	private static Object[] connectNet(PluginContext context, Pnml pnml, PetrinetGraph net) {
		/*
		 * Create a fresh marking.
		 */
		Marking marking = new Marking();

		GraphLayoutConnection layout = new GraphLayoutConnection(net);
		/*
		 * Initialize the Petri net and marking from the PNML element.
		 */
		pnml.convertToNet(net, marking, layout);

		/*
		 * Add a connection from the Petri net to the marking.
		 */
		InitialMarkingConnection c = context.addConnection(new InitialMarkingConnection(net, marking));
		context.addConnection(layout);
		System.out.println("connect initial marking: "+c);

		/*
		 * Return the net and the marking.
		 */
		Object[] objects = new Object[2];
		objects[0] = net;
		objects[1] = marking;
		return objects;
	}
	
}
