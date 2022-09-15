package org.processmining.plugins.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;

@Plugin(name = "Remove Unconnected Nodes", parameterLabels = { "Petri net" }, returnLabels = { "Petri net" }, returnTypes = { Petrinet.class }, help = "Removes unconnected nodes, i.e. places / transitions."
        + " An unconnected node is a node that does not have any incoming or outgoing arcs.")
public class RemoveUnconnectedNodesPlugin {

    @UITopiaVariant(affiliation = "Eindhoven University of Technology", author = "S.J. van Zelst", email = "s.j.v.zelst@tue.nl")
    @PluginVariant(variantLabel = "Remove Unconnected Nodes", requiredParameterLabels = { 0 })
    public static Petrinet apply(final UIPluginContext context,
            final Petrinet net) {

        Map<DirectedGraphElement, DirectedGraphElement> pnElementsMap = new HashMap<DirectedGraphElement, DirectedGraphElement>();
        Petrinet clone = PetrinetFactory.clonePetrinet(net, pnElementsMap);

        context.log("Removing unconnected Nodes...");
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(3);
        context.getProgress().setValue(0);

        context.log("Removing unconnected places (1/3) ...");
        context.getProgress().setValue(1);
        clone = removePlaces(clone);

        context.log("Removing unconnected transitions (2/3) ...");
        context.getProgress().setValue(2);
        clone = removeTransitions(clone);

        context.log("Computing new marking (3/3)...");
        context.getProgress().setValue(3);
        Marking marking = computeNewMarking(context, net, pnElementsMap);
        if (marking != null) {
            Connection newMarkingConnection = new InitialMarkingConnection(
                    clone, marking);
            context.getConnectionManager().addConnection(newMarkingConnection);
        }
        return clone;
    }

    /**
     * Context-unaware version, does not allow us to find markings!
     * 
     * @param net
     * @return
     */
    public static Petrinet apply(Petrinet net) {
        Map<DirectedGraphElement, DirectedGraphElement> pnEMap = new HashMap<DirectedGraphElement, DirectedGraphElement>();
        Petrinet clone = PetrinetFactory.clonePetrinet(net, pnEMap);
        clone = removePlaces(clone);
        clone = removeTransitions(clone);
        return clone;
    }

    private static Petrinet removePlaces(Petrinet net) {
        Set<Place> placesToRemove = placesToRemove(net);
        for (Place place : placesToRemove) {
            net.removePlace(place);
        }
        return net;
    }

    private static Petrinet removeTransitions(Petrinet net) {
        Set<Transition> transitionsToRemove = transitionsToRemove(net);
        for (Transition tr : transitionsToRemove) {
            net.removeTransition(tr);
        }
        return net;
    }

    private static Set<Transition> transitionsToRemove(Petrinet net) {
        Set<Transition> transitionsToRemove = new HashSet<Transition>();
        for (Transition transition : net.getTransitions()) {
            if (net.getInEdges(transition).isEmpty()
                    && net.getOutEdges(transition).isEmpty()) {
                transitionsToRemove.add(transition);
            }
        }
        return transitionsToRemove;
    }

    private static Set<Place> placesToRemove(Petrinet net) {
        Set<Place> placesToRemove = new HashSet<Place>();
        for (Place place : net.getPlaces()) {
            if (net.getInEdges(place).isEmpty()
                    && net.getOutEdges(place).isEmpty()) {
                placesToRemove.add(place);
            }
        }
        return placesToRemove;
    }

    private static Marking computeNewMarking(UIPluginContext context,
            Petrinet net,
            Map<DirectedGraphElement, DirectedGraphElement> pnElementsMap) {
        Marking existingMarking = null;
        Marking newMarking = null;
        try {
            Collection<InitialMarkingConnection> connections = context
                    .getConnectionManager().getConnections(
                            InitialMarkingConnection.class, context);
            for (InitialMarkingConnection connection : connections) {
                if (connection.getObjectWithRole(InitialMarkingConnection.NET)
                        .equals(net)) {
                    existingMarking = connection
                            .getObjectWithRole(InitialMarkingConnection.MARKING);
                }
            }
        } catch (ConnectionCannotBeObtained e) {
            e.printStackTrace();
        }

        if (existingMarking != null) {
            newMarking = new Marking();
            for (Place p : existingMarking) {
                Place pPrime = (Place) pnElementsMap.get(p);
                newMarking.add(pPrime, existingMarking.occurrences(p));
            }
        }
        return newMarking;
    }

}
