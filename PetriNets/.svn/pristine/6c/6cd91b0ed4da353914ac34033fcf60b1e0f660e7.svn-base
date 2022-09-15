package org.processmining.petrinets.analysis.gedsim.plugins;

import java.util.ArrayList;
import java.util.List;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.wizard.ListWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.petrinets.analysis.gedsim.algorithms.GraphEditDistanceSimilarityAlgorithm;
import org.processmining.petrinets.analysis.gedsim.algorithms.impl.GraphEditDistanceSimilarityGreedy;
import org.processmining.petrinets.analysis.gedsim.dialogs.GraphEditDistanceSimilarityParametersWizardStep;
import org.processmining.petrinets.analysis.gedsim.params.GraphEditDistanceSimilarityParameters;

/**
 * Calculates the Graph Edit Distance Similarity between two directed graphs,
 * using default settings. This is a ported version of ProM5 code. The code is
 * not really clean yet I *have the feeling* the greedy algorithm fits its
 * purpose. If at some point someone feels like re-implementing the code, that
 * would be very convenient, time-wise, that is currently not interesting for me
 * (svzelst).
 * 
 */
@Plugin(name = "Calculate Graph Edit Distance Similarity", parameterLabels = { "Petri net A", "Petri net B",
		"Parameters" }, returnLabels = { "Graph Edit Distance Similarity" }, returnTypes = { Double.class })
public class GraphEditDistanceSimilarityPlugin {

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J. van Zelst", email = "s.j.v.zelst@tue.nl", pack = "PetriNets")
	@PluginVariant(variantLabel = "Calculate Graph Edit Distance, Petrinet, Choose Weights", requiredParameterLabels = {
			0, 1 })
	public static Double calculatePetrinetDefaultAB(UIPluginContext context, Petrinet modelA, Petrinet modelB) {
		ProMWizardStep<GraphEditDistanceSimilarityParameters> ws1 = new GraphEditDistanceSimilarityParametersWizardStep(
				"Configure Graph Edit Distance Parameters");
		List<ProMWizardStep<GraphEditDistanceSimilarityParameters>> wList = new ArrayList<ProMWizardStep<GraphEditDistanceSimilarityParameters>>();
		wList.add(ws1);
		ListWizard<GraphEditDistanceSimilarityParameters> wizard = new ListWizard<GraphEditDistanceSimilarityParameters>(wList);
		GraphEditDistanceSimilarityParameters params = ProMWizardDisplay.show(context, wizard,
				new GraphEditDistanceSimilarityParameters());
		if (params != null) {
			return calculatePetrinetDefaultAB(modelA, modelB, params);
		} else {
			return -1.0;
		}
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J. van Zelst", email = "s.j.v.zelst@tue.nl", pack = "PetriNets")
	@PluginVariant(variantLabel = "Calculate Graph Edit Distance, Petrinet, default, A->B", requiredParameterLabels = {
			0, 1 })
	public static Double calculatePetrinetDefaultAB(PluginContext context, Petrinet modelA, Petrinet modelB) {
		GraphEditDistanceSimilarityParameters params = new GraphEditDistanceSimilarityParameters();
		return calculatePetrinetDefaultAB(modelA, modelB, params);
	}

	public static Double calculatePetrinetDefaultAB(Petrinet modelA, Petrinet modelB,
			GraphEditDistanceSimilarityParameters params) {
		GraphEditDistanceSimilarityAlgorithm<Petrinet> algo = new GraphEditDistanceSimilarityGreedy<Petrinet>(params);
		return 1 - algo.compute(modelA, modelB);
	}

}
