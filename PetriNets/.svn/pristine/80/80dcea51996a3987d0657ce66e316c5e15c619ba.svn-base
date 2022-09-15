package org.processmining.petrinets.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;

public class PetriNetUtils {

	public static void addInitialMarking(PluginContext context, Petrinet net, Marking marking) {
		Connection connection = new InitialMarkingConnection(net, marking);
		context.getConnectionManager().addConnection(connection);
	}

	public static void addFinalMarking(PluginContext context, Petrinet net, Marking marking) {
		Connection connection = new FinalMarkingConnection(net, marking);
		context.getConnectionManager().addConnection(connection);
	}

	public static Marking[] getInitialMarkings(PluginContext context, Petrinet net) {
		Marking[] markings = null;
		try {
			Collection<InitialMarkingConnection> initialMarkingConnections = context.getConnectionManager()
					.getConnections(InitialMarkingConnection.class, context, net);
			if (initialMarkingConnections.size() != 0) {
				Set<Marking> setInitialMarkings = new HashSet<Marking>();
				for (InitialMarkingConnection conn : initialMarkingConnections) {
					setInitialMarkings.add((Marking) conn.getObjectWithRole(InitialMarkingConnection.MARKING));
				}
				markings = setInitialMarkings.toArray(new Marking[setInitialMarkings.size()]);
			} else {
				markings = new Marking[0];
			}
		} catch (ConnectionCannotBeObtained exc) {
			// no initial marking provided
			markings = new Marking[0];
		}
		return markings;
	}

	public static Marking[] getFinalMarkings(PluginContext context, Petrinet net) {
		Marking[] finalMarkings = null;
		try {
			Collection<FinalMarkingConnection> finalMarkingConnections = context.getConnectionManager()
					.getConnections(FinalMarkingConnection.class, context, net);
			if (finalMarkingConnections.size() != 0) {
				Set<Marking> setFinalMarkings = new HashSet<Marking>();
				for (FinalMarkingConnection conn : finalMarkingConnections) {
					setFinalMarkings.add((Marking) conn.getObjectWithRole(FinalMarkingConnection.MARKING));
				}
				finalMarkings = setFinalMarkings.toArray(new Marking[setFinalMarkings.size()]);
			} else {
				finalMarkings = new Marking[0];
			}
		} catch (ConnectionCannotBeObtained exc) {
			// no final marking provided
			finalMarkings = new Marking[0];
		}
		return finalMarkings;
	}

	public static int getNumberOfInitialMarkings(PluginContext context, Petrinet net) {
		return getInitialMarkings(context, net).length;
	}

	public static int getNumberOfFinalMarkgins(PluginContext context, Petrinet net) {
		return getFinalMarkings(context, net).length;
	}

	public static boolean hasInitialMarkings(PluginContext context, Petrinet net) {
		return getInitialMarkings(context, net).length > 0;
	}

	public static boolean hasNumberOfFinalMarkgins(PluginContext context, Petrinet net) {
		return getFinalMarkings(context, net).length > 0;
	}
}
