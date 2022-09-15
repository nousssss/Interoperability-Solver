package org.processmining.plugins.petrinet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.Cast;
import org.processmining.framework.util.collection.ComparablePair;
import org.processmining.framework.util.collection.MultiSet;
import org.processmining.framework.util.ui.scalableview.interaction.CompoundViewInteractionPanel;
import org.processmining.models.connections.petrinets.behavioral.AbstractSemanticConnection;
import org.processmining.models.connections.petrinets.behavioral.BehavioralAnalysisInformationConnection;
import org.processmining.models.connections.petrinets.behavioral.CoverabilitySetConnection;
import org.processmining.models.connections.petrinets.behavioral.DeadMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.DeadTransitionsConnection;
import org.processmining.models.connections.petrinets.behavioral.HomeMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.MarkingsetNetConnection;
import org.processmining.models.connections.petrinets.behavioral.NonLiveSequencesConnection;
import org.processmining.models.connections.petrinets.behavioral.NonLiveTransitionsConnection;
import org.processmining.models.connections.petrinets.behavioral.NonRelaxedSoundTransitionsConnection;
import org.processmining.models.connections.petrinets.behavioral.ReachabilitySetConnection;
import org.processmining.models.connections.petrinets.behavioral.UnboundedPlacesConnection;
import org.processmining.models.connections.petrinets.behavioral.UnboundedSequencesConnection;
import org.processmining.models.connections.petrinets.structural.AbstractComponentSetConnection;
import org.processmining.models.connections.petrinets.structural.AbstractInvariantMarkingConnection;
import org.processmining.models.connections.petrinets.structural.AbstractNetHandleConnection;
import org.processmining.models.connections.petrinets.structural.AbstractStructuralAnalysisInformationConnection;
import org.processmining.models.connections.petrinets.structural.NonExtendedFreeChoiceClustersConnection;
import org.processmining.models.connections.petrinets.structural.NonFreeChoiceClustersConnection;
import org.processmining.models.connections.petrinets.structural.NotPCoveredNodesConnection;
import org.processmining.models.connections.petrinets.structural.NotSCoveredNodesConnection;
import org.processmining.models.connections.petrinets.structural.PTHandleConnection;
import org.processmining.models.connections.petrinets.structural.PlaceInvariantConnection;
import org.processmining.models.connections.petrinets.structural.SComponentConnection;
import org.processmining.models.connections.petrinets.structural.SinkPlacesConnection;
import org.processmining.models.connections.petrinets.structural.SiphonConnection;
import org.processmining.models.connections.petrinets.structural.SourcePlacesConnection;
import org.processmining.models.connections.petrinets.structural.TComponentConnection;
import org.processmining.models.connections.petrinets.structural.TPHandleConnection;
import org.processmining.models.connections.petrinets.structural.TransitionInvariantConnection;
import org.processmining.models.connections.petrinets.structural.TrapConnection;
import org.processmining.models.connections.petrinets.structural.UnconnectedNodesConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.analysis.AbstractComponentSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.AbstractInvariantSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.AbstractMarkingSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.AbstractNodePairSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.DeadTransitionsSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.NetAnalysisInformation;
import org.processmining.models.graphbased.directed.petrinet.analysis.NonExtendedFreeChoiceClustersSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.NonFreeChoiceClustersSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.NonLiveSequences;
import org.processmining.models.graphbased.directed.petrinet.analysis.NonLiveTransitionsSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.NonRelaxedSoundTransitionsSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.NotPCoveredNodesSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.NotSCoveredNodesSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.SinkPlacesSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.SourcePlacesSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.UnboundedPlacesSet;
import org.processmining.models.graphbased.directed.petrinet.analysis.UnboundedSequences;
import org.processmining.models.graphbased.directed.petrinet.analysis.UnconnectedNodesSet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.AcceptStateSet;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;
import org.processmining.models.semantics.Semantics;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.util.ListSelectionPanel;

/**
 * This plugin visualizes information about a Petri net. Instead of allowing the
 * user to give all possible information, the available connections are scanned
 * and used for finding information relating the the net.
 * 
 * @author bfvdonge
 * 
 */
@Plugin(name = "@0 Visualize Petri net", level = PluginLevel.PeerReviewed, returnLabels = { "Visualized Petrinet" }, returnTypes = { JComponent.class }, parameterLabels = {
		"Petri net", "Initial Marking" }, userAccessible = true)
@Visualizer
public class PetriNetVisualization {

	protected JComponent visualizeWithAll(PluginContext context, PetrinetGraph n, Marking m) {

		boolean hasMarking;
		ConnectionManager cm = context.getConnectionManager();
		// Try to see if there is an initial marking available for visualization
		try {
			for (InitialMarkingConnection mnc : cm.getConnections(InitialMarkingConnection.class, context, n)) {
				m = mnc.getObjectWithRole(InitialMarkingConnection.MARKING);

			}
			hasMarking = true;
		} catch (ConnectionCannotBeObtained e) {
			// no initial marking, no problem
			m = new Marking();
			hasMarking = false;
		}

		//		return PetriNetVisualization.getComponent(context, n, m);

		ViewSpecificAttributeMap map = new ViewSpecificAttributeMap();
		for (Place p : m) {
			String label = "" + m.occurrences(p);
			map.putViewSpecific(p, AttributeMap.LABEL, label);
			map.putViewSpecific(p, AttributeMap.TOOLTIP, p.getLabel());
			map.putViewSpecific(p, AttributeMap.SHOWLABEL, !label.equals(""));
		}

		ProMJGraphPanel panel = ProMJGraphVisualizer.instance().visualizeGraph(context, n, map);

		CompoundViewInteractionPanel structural = new CompoundViewInteractionPanel("Structural");
		CompoundViewInteractionPanel behavioral = new CompoundViewInteractionPanel("Behavioral");

		addInvariantSet("P-Inv", "Semi positive place invariants", PlaceInvariantConnection.class, context, structural,
				n);
		addInvariantSet("T-Inv", "Semi positive transition invariants", TransitionInvariantConnection.class, context,
				structural, n);

		// Check for available non-live sequences
		try {
			ListSelectionPanel view = new ListSelectionPanel("Non-live seq.", "Non live sequences", true);
			for (NonLiveSequencesConnection inv : cm.getConnections(NonLiveSequencesConnection.class, context, n)) {
				NonLiveSequences sequences = inv.getObjectWithRole(NonLiveSequencesConnection.SEQUENCES);
				Semantics<Marking, Transition> semantics = inv.getObjectWithRole(AbstractSemanticConnection.SEMANTICS);
				for (MultiSet<PetrinetNode> sequence : sequences) {
					view.addElementCollection(sequence,
							"<html>" + sequence.toHTMLString(false) + " (" + semantics.toString() + ")</html>");
				}
			}
			behavioral.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available unbounded sequences
		try {
			ListSelectionPanel view = new ListSelectionPanel("Unb. seq.", "Unbounded sequences", true);
			for (UnboundedSequencesConnection inv : cm.getConnections(UnboundedSequencesConnection.class, context, n)) {
				UnboundedSequences sequences = inv.getObjectWithRole(UnboundedSequencesConnection.SEQUENCES);
				Semantics<Marking, Transition> semantics = inv.getObjectWithRole(AbstractSemanticConnection.SEMANTICS);
				for (MultiSet<PetrinetNode> sequence : sequences) {
					view.addElementCollection(sequence,
							"<html>" + sequence.toHTMLString(false) + " (" + semantics.toString() + ")</html>");
				}
			}
			behavioral.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available dead markings
		try {
			ListSelectionPanel view = new ListSelectionPanel("Dead markings", "Dead markings", true);
			for (DeadMarkingConnection comp : cm.getConnections(DeadMarkingConnection.class, context, n)) {
				AcceptStateSet component = comp.getObjectWithRole(DeadMarkingConnection.DEADMARKINGS);
				Semantics<Marking, Transition> semantics = comp.getObjectWithRole(AbstractSemanticConnection.SEMANTICS);
				for (Object sequence : component) {
					Marking mark = (Marking) sequence;
					view.addElementCollection(mark, "<html>" + mark.toHTMLString(false) + " (" + semantics.toString()
							+ ")</html>");
				}
			}
			behavioral.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		addComponentSet("S-comp", "S components", SComponentConnection.class, context, structural, n);
		addComponentSet("T-comp", "T components", TComponentConnection.class, context, structural, n);
		addComponentSet("Siphons", "Siphons", SiphonConnection.class, context, structural, n);
		addComponentSet("Traps", "Traps", TrapConnection.class, context, structural, n);

		// Check for available dead transitions
		try {
			ListSelectionPanel view = new ListSelectionPanel("Dead transitions", "Dead transitions", true);
			for (DeadTransitionsConnection comp : cm.getConnections(DeadTransitionsConnection.class, context, n)) {
				DeadTransitionsSet component = comp.getObjectWithRole(DeadTransitionsConnection.TRANSITIONS);
				Semantics<Marking, Transition> semantics = comp.getObjectWithRole(AbstractSemanticConnection.SEMANTICS);
				for (SortedSet<Transition> sequence : component) {
					view.addElementCollection(sequence, "<html>" + sequence.toString() + " (" + semantics.toString()
							+ ")</html>");
				}
			}
			behavioral.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available non-live transitions
		try {
			ListSelectionPanel view = new ListSelectionPanel("Non-live Transitions", "Non-live transitions", true);
			for (NonLiveTransitionsConnection comp : cm.getConnections(NonLiveTransitionsConnection.class, context, n)) {
				NonLiveTransitionsSet component = (NonLiveTransitionsSet) comp
						.getObjectWithRole(NonLiveTransitionsConnection.TRANSITIONS);
				Semantics<Marking, Transition> semantics = Cast.<Semantics<Marking, Transition>>cast(comp
						.getObjectWithRole(AbstractSemanticConnection.SEMANTICS));
				for (SortedSet<Transition> sequence : component) {
					view.addElementCollection(sequence, "<html>" + sequence.toString() + " (" + semantics.toString()
							+ ")</html>");
				}
			}
			behavioral.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available unbounded places
		try {
			ListSelectionPanel view = new ListSelectionPanel("Unbounded places", "Unbounded places", true);
			for (UnboundedPlacesConnection comp : cm.getConnections(UnboundedPlacesConnection.class, context, n)) {
				UnboundedPlacesSet component = (UnboundedPlacesSet) comp
						.getObjectWithRole(UnboundedPlacesConnection.PLACES);
				Semantics<Marking, Transition> semantics = Cast.<Semantics<Marking, Transition>>cast(comp
						.getObjectWithRole(AbstractSemanticConnection.SEMANTICS));
				for (SortedSet<Place> sequence : component) {
					view.addElementCollection(sequence, "<html>" + sequence.toString() + " (" + semantics.toString()
							+ ")</html>");
				}
			}
			behavioral.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available non-free-choice clusters
		try {
			ListSelectionPanel view = new ListSelectionPanel("NFC clusters", "Non-free-choice clusters", true);
			for (NonFreeChoiceClustersConnection comp : cm.getConnections(NonFreeChoiceClustersConnection.class,
					context, n)) {
				NonFreeChoiceClustersSet component = (NonFreeChoiceClustersSet) comp
						.getObjectWithRole(NonFreeChoiceClustersConnection.CLUSTERS);
				for (SortedSet<PetrinetNode> sequence : component) {
					view.addElementCollection(sequence);
				}
			}
			structural.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available non-extended-free-choice clusters
		try {
			ListSelectionPanel view = new ListSelectionPanel("NeFC clusters", "Non-extended-free-choice clusters", true);
			for (NonExtendedFreeChoiceClustersConnection comp : cm.getConnections(
					NonExtendedFreeChoiceClustersConnection.class, context, n)) {
				NonExtendedFreeChoiceClustersSet component = (NonExtendedFreeChoiceClustersSet) comp
						.getObjectWithRole(NonExtendedFreeChoiceClustersConnection.CLUSTERS);
				for (SortedSet<PetrinetNode> sequence : component) {
					view.addElementCollection(sequence);
				}
			}
			structural.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available source places
		try {
			ListSelectionPanel view = new ListSelectionPanel("Source places", "Source places", true);
			for (SourcePlacesConnection comp : cm.getConnections(SourcePlacesConnection.class, context, n)) {
				SourcePlacesSet component = (SourcePlacesSet) comp.getObjectWithRole(SourcePlacesConnection.PLACES);
				for (SortedSet<Place> sequence : component) {
					view.addElementCollection(sequence);
				}
			}
			structural.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available sink places
		try {
			ListSelectionPanel view = new ListSelectionPanel("Sink places", "Sink places", true);
			for (SinkPlacesConnection comp : cm.getConnections(SinkPlacesConnection.class, context, n)) {
				SinkPlacesSet component = (SinkPlacesSet) comp.getObjectWithRole(SinkPlacesConnection.PLACES);
				for (SortedSet<Place> sequence : component) {
					view.addElementCollection(sequence);
				}
			}
			structural.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available unconnected nodes
		try {
			ListSelectionPanel view = new ListSelectionPanel("Uncon. nodes", "Unconnected nodes", true);
			for (UnconnectedNodesConnection comp : cm.getConnections(UnconnectedNodesConnection.class, context, n)) {
				UnconnectedNodesSet component = (UnconnectedNodesSet) comp
						.getObjectWithRole(UnconnectedNodesConnection.NODES);
				for (SortedSet<PetrinetNode> sequence : component) {
					view.addElementCollection(sequence);
				}
			}
			structural.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available not S-covered nodes
		try {
			ListSelectionPanel view = new ListSelectionPanel("Non-S-cov. nodes", "Non-S-coverable nodes", true);
			for (NotSCoveredNodesConnection comp : cm.getConnections(NotSCoveredNodesConnection.class, context, n)) {
				NotSCoveredNodesSet component = (NotSCoveredNodesSet) comp
						.getObjectWithRole(NotSCoveredNodesConnection.NODES);
				for (SortedSet<PetrinetNode> sequence : component) {
					view.addElementCollection(sequence);
				}
			}
			structural.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available not P-covered nodes
		try {
			ListSelectionPanel view = new ListSelectionPanel("Non-p-cov. nodes", "Non-P-coverabel nodes", true);
			for (NotPCoveredNodesConnection comp : cm.getConnections(NotPCoveredNodesConnection.class, context, n)) {
				NotPCoveredNodesSet component = (NotPCoveredNodesSet) comp
						.getObjectWithRole(NotPCoveredNodesConnection.NODES);
				for (SortedSet<Place> sequence : component) {
					view.addElementCollection(sequence);
				}
			}
			structural.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available handles
		try {
			ListSelectionPanel view = new ListSelectionPanel("TP handles", "TP handles", true);
			for (TPHandleConnection nhc : cm.getConnections(TPHandleConnection.class, context, n)) {
				@SuppressWarnings("unchecked")
				AbstractNodePairSet<PetrinetNode, PetrinetNode> handle = (AbstractNodePairSet<PetrinetNode, PetrinetNode>) nhc
						.getObjectWithRole(AbstractNetHandleConnection.HANDLES);
				for (ComparablePair<PetrinetNode, PetrinetNode> sequence : handle) {
					ArrayList<DirectedGraphElement> list = new ArrayList<DirectedGraphElement>(2);
					list.add(sequence.getFirst());
					list.add(sequence.getSecond());
					view.addElementCollection(list);
				}
			}
			structural.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available handles
		try {
			ListSelectionPanel view = new ListSelectionPanel("PT handles", "PT handles", true);
			for (PTHandleConnection nhc : cm.getConnections(PTHandleConnection.class, context, n)) {
				@SuppressWarnings("unchecked")
				AbstractNodePairSet<PetrinetNode, PetrinetNode> handle = (AbstractNodePairSet<PetrinetNode, PetrinetNode>) nhc
						.getObjectWithRole(AbstractNetHandleConnection.HANDLES);
				for (ComparablePair<PetrinetNode, PetrinetNode> sequence : handle) {
					ArrayList<DirectedGraphElement> list = new ArrayList<DirectedGraphElement>(2);
					list.add(sequence.getFirst());
					list.add(sequence.getSecond());
					view.addElementCollection(list);
				}
			}
			structural.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		// Check for available cause of non relaxed sound net
		try {
			ListSelectionPanel view = new ListSelectionPanel("Non-relaxed-sound cause", "Not fireable transitions",
					true);
			for (NonRelaxedSoundTransitionsConnection comp : cm.getConnections(
					NonRelaxedSoundTransitionsConnection.class, context, n)) {
				NonRelaxedSoundTransitionsSet component = comp
						.getObjectWithRole(NonRelaxedSoundTransitionsConnection.TRANSITIONS);
				Semantics<Marking, Transition> semantics = Cast.<Semantics<Marking, Transition>>cast(comp
						.getObjectWithRole(AbstractSemanticConnection.SEMANTICS));
				for (SortedSet<Transition> sequence : component) {
					view.addElementCollection(sequence, "<html>" + sequence.toString() + " (" + semantics.toString()
							+ ")</html>");
				}
			}
			behavioral.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

		addMarkingSet("Coverability", "Coverable markings", CoverabilitySetConnection.class, context, behavioral, n);
		addMarkingSet("Reachability", "Reachable markings", ReachabilitySetConnection.class, context, behavioral, n);
		addMarkingSet("Home markings", "Home markings", HomeMarkingConnection.class, context, behavioral, n);

		// Check for available analysis information
		List<NetAnalysisInformation<?>> infoList = new ArrayList<NetAnalysisInformation<?>>();
		// First structural analysis (no marking needed)
		try {
			for (AbstractStructuralAnalysisInformationConnection analysis : cm.getConnections(
					AbstractStructuralAnalysisInformationConnection.class, context, n)) {
				NetAnalysisInformation<?> info = (NetAnalysisInformation<?>) analysis
						.getObjectWithRole(AbstractStructuralAnalysisInformationConnection.NETANALYSISINFORMATION);
				infoList.add(info);
			}
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}
		if (!infoList.isEmpty()) {
			ListSelectionPanel view = new ListSelectionPanel("Properties", "Structural properties", false);
			for (NetAnalysisInformation<?> info : infoList) {
				view.addElementCollection(Collections.<DirectedGraphElement>emptySet(),
						info.getLabel() + ": " + info.getValue());
			}
			structural.addViewInteractionPanel(view);
		}
		infoList.clear();

		Map<Semantics<?, ?>, List<NetAnalysisInformation<?>>> behavInfo = new HashMap<Semantics<?, ?>, List<NetAnalysisInformation<?>>>();
		if (m != null) {
			// Then normal information, marking needed
			try {
				for (BehavioralAnalysisInformationConnection analysis : cm.getConnections(
						BehavioralAnalysisInformationConnection.class, context, n, m)) {
					NetAnalysisInformation<?> info = (NetAnalysisInformation<?>) analysis
							.getObjectWithRole(BehavioralAnalysisInformationConnection.NETANALYSISINFORMATION);
					Semantics<Marking, Transition> semantics = Cast.<Semantics<Marking, Transition>>cast(analysis
							.getObjectWithRole(AbstractSemanticConnection.SEMANTICS));
					if (behavInfo.get(semantics) == null) {
						behavInfo.put(semantics, new ArrayList<NetAnalysisInformation<?>>());
					}
					behavInfo.get(semantics).add(info);
				}
			} catch (ConnectionCannotBeObtained e) {
				// No connections available
			}
		}
		if (!behavInfo.keySet().isEmpty()) {
			ListSelectionPanel view = new ListSelectionPanel("Properties", "Behavioral properties", false);
			for (Map.Entry<Semantics<?, ?>, List<NetAnalysisInformation<?>>> entry : behavInfo.entrySet()) {
				for (NetAnalysisInformation<?> info : entry.getValue()) {
					view.addElementCollection(Collections.<DirectedGraphElement>emptySet(), info.getLabel() + ": "
							+ info.getValue() + " (" + entry.getKey().toString() + ")");
				}
			}
			behavioral.addViewInteractionPanel(view);
		}

		if (structural.length() > 0) {
			panel.addViewInteractionPanel(structural, SwingConstants.SOUTH);
		}
		if (hasMarking && (behavioral.length() > 0)) {
			panel.addViewInteractionPanel(behavioral, SwingConstants.SOUTH);
		}
		return panel;

	}

	private static void addComponentSet(String name, String label,
			Class<? extends AbstractComponentSetConnection> clazz, PluginContext context,
			CompoundViewInteractionPanel panel, PetrinetGraph n) {
		// Check for available components
		try {
			ListSelectionPanel view = new ListSelectionPanel(name, label, true);
			for (AbstractComponentSetConnection comp : context.getConnectionManager().getConnections(clazz, context, n)) {
				AbstractComponentSet<? extends DirectedGraphElement> component = comp
						.getObjectWithRole(AbstractComponentSetConnection.COMPONENTSET);

				for (SortedSet<? extends DirectedGraphElement> sequence : component) {
					view.addElementCollection(sequence);
				}
			}
			panel.addViewInteractionPanel(view);//, SwingConstants.SOUTH);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}

	}

	private static void addInvariantSet(String name, String label,
			Class<? extends AbstractInvariantMarkingConnection> clazz, PluginContext context,
			CompoundViewInteractionPanel panel, PetrinetGraph n) {
		try {
			ListSelectionPanel view = new ListSelectionPanel(name, label, true);
			for (AbstractInvariantMarkingConnection connection : context.getConnectionManager().getConnections(clazz,
					context, n)) {
				AbstractInvariantSet<PetrinetNode> invariants = connection
						.getObjectWithRole(AbstractInvariantMarkingConnection.INVARIANTMARKING);
				for (MultiSet<PetrinetNode> invariant : invariants) {
					view.addElementCollection(invariant);
				}
			}
			panel.addViewInteractionPanel(view);//, SwingConstants.SOUTH);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}
	}

	private static void addMarkingSet(String name, String label, Class<? extends MarkingsetNetConnection> clazz,
			PluginContext context, CompoundViewInteractionPanel panel, PetrinetGraph n) {
		// Check for available marking sets
		try {
			ListSelectionPanel view = new ListSelectionPanel(name, label, true);
			for (MarkingsetNetConnection markedNet : context.getConnectionManager().getConnections(clazz, context, n)) {
				AbstractMarkingSet<?> markingsset = markedNet.getObjectWithRole(MarkingsetNetConnection.MARKINGS);
				Semantics<Marking, Transition> semantics = markedNet
						.getObjectWithRole(MarkingsetNetConnection.SEMANTICS);
				for (Marking markings : markingsset) {
					view.addElementCollection(markings, "<html>" + markings.toString() + " (" + semantics.toString()
							+ ")</html>");
				}
			}
			panel.addViewInteractionPanel(view);
		} catch (ConnectionCannotBeObtained e) {
			// No connections available
		}
	}

	@PluginVariant(requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, Petrinet n) {
		return visualizeWithAll(context, n, null);
	}

	@PluginVariant(requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, ResetNet n) {
		return visualizeWithAll(context, n, null);
	}

	@PluginVariant(requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, InhibitorNet n) {
		return visualizeWithAll(context, n, null);
	}

	@PluginVariant(requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, ResetInhibitorNet n) {
		return visualizeWithAll(context, n, null);
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public JComponent visualize(PluginContext context, Petrinet n, Marking m) {
		return visualizeWithAll(context, n, m);
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public JComponent visualize(PluginContext context, ResetNet n, Marking m) {
		return visualizeWithAll(context, n, m);
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public JComponent visualize(PluginContext context, InhibitorNet n, Marking m) {
		return visualizeWithAll(context, n, m);
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public JComponent visualize(PluginContext context, ResetInhibitorNet n, Marking m) {
		return visualizeWithAll(context, n, m);
	}

}
