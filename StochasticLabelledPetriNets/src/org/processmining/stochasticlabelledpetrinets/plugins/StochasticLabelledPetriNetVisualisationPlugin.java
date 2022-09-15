package org.processmining.stochasticlabelledpetrinets.plugins;

import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public abstract class StochasticLabelledPetriNetVisualisationPlugin<N extends StochasticLabelledPetriNet> {

	public DotPanel visualise(N net) {
		Dot dot = new Dot();

		dot.setOption("forcelabels", "true");

		TIntObjectMap<DotNode> place2dotNode = new TIntObjectHashMap<>(10, 0.5f, -1);

		for (int place = 0; place < net.getNumberOfPlaces(); place++) {
			DotNode dotNode = dot.addNode("");
			dotNode.setOption("shape", "circle");
			place2dotNode.put(place, dotNode);

			if (net.isInInitialMarking(place) > 0) {
				dotNode.setOption("style", "filled");
				dotNode.setOption("fillcolor", "#80ff00");
			}

			decoratePlace(net, place, dotNode);
		}

		for (int transition = 0; transition < net.getNumberOfTransitions(); transition++) {
			DotNode dotNode;

			if (net.isTransitionSilent(transition)) {
				dotNode = dot.addNode("" + transition);
				dotNode.setOption("style", "filled");
				dotNode.setOption("fillcolor", "gray");
			} else {
				dotNode = dot.addNode(net.getTransitionLabel(transition));
			}

			dotNode.setOption("shape", "box");

			decorateTransition(net, transition, dotNode);

			for (int place : net.getOutputPlaces(transition)) {
				dot.addEdge(dotNode, place2dotNode.get(place));
			}

			for (int place : net.getInputPlaces(transition)) {
				dot.addEdge(place2dotNode.get(place), dotNode);
			}
		}

		return new DotPanel(dot);
	}

	public abstract void decoratePlace(N net, int place, DotNode dotNode);

	public abstract void decorateTransition(N net, int transition, DotNode dotNode);

}