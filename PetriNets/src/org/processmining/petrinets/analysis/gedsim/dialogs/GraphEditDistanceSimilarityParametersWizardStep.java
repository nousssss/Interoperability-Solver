package org.processmining.petrinets.analysis.gedsim.dialogs;

import javax.swing.JComponent;

import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.petrinets.analysis.gedsim.params.GraphEditDistanceSimilarityParameters;

import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class GraphEditDistanceSimilarityParametersWizardStep extends ProMPropertiesPanel
		implements ProMWizardStep<GraphEditDistanceSimilarityParameters> {

	private static final long serialVersionUID = 1924018499287176822L;

	private final static double SLIDER_MULTIPLICATION_FACTOR = 1000.0d;

	private final NiceSlider weightSkippedVertices;

	private final NiceSlider weightSkippedEdges;

	private final NiceSlider weightSubstituedEdges;

	public GraphEditDistanceSimilarityParametersWizardStep(String title) {
		super(title);
		add(SlickerFactory.instance().createLabel("Weight Skipped Vertices"));
		weightSkippedVertices = SlickerFactory.instance().createNiceDoubleSlider("", 0.0, 1.0,
				GraphEditDistanceSimilarityParameters.DEFAULT_WEIGHT_SKIPPED_VERTICES, Orientation.HORIZONTAL);
		add(weightSkippedVertices);
		add(SlickerFactory.instance().createLabel("Weight Skipped Edges"));
		weightSkippedEdges = SlickerFactory.instance().createNiceDoubleSlider("", 0.0, 1.0,
				GraphEditDistanceSimilarityParameters.DEFAULT_WEIGHT_SKIPPED_EDGES, Orientation.HORIZONTAL);
		add(weightSkippedEdges);
		add(SlickerFactory.instance().createLabel("Weight Substituted Vertices"));
		weightSubstituedEdges = SlickerFactory.instance().createNiceDoubleSlider("", 0.0, 1.0,
				GraphEditDistanceSimilarityParameters.DEFAULT_WWEIGHT_SUBSTITUTED_NODES, Orientation.HORIZONTAL);
		add(weightSubstituedEdges);
	}

	public GraphEditDistanceSimilarityParameters apply(GraphEditDistanceSimilarityParameters model,
			JComponent component) {
		if (canApply(model, component)) {
			GraphEditDistanceSimilarityParametersWizardStep ws = (GraphEditDistanceSimilarityParametersWizardStep) component;
			model.setWeightSkippedVertices(ws.getWeightSkippedVertices());
			model.setWeightSkippedEdges(ws.getWeightSkippedEdges());
			model.setWeightSubstitutedVertices(ws.getWeightSubstituedEdges());
		}
		return model;
	}

	public boolean canApply(GraphEditDistanceSimilarityParameters model, JComponent component) {
		return component instanceof GraphEditDistanceSimilarityParametersWizardStep;
	}

	public JComponent getComponent(GraphEditDistanceSimilarityParameters model) {
		return this;
	}

	public String getTitle() {
		return "Configure Graph Edit Distance Similarity Parameters";
	}

	public double getWeightSkippedEdges() {
		return weightSkippedEdges.getSlider().getValue() / SLIDER_MULTIPLICATION_FACTOR;
	}

	public double getWeightSkippedVertices() {
		return weightSkippedVertices.getSlider().getValue() / SLIDER_MULTIPLICATION_FACTOR;
	}

	public double getWeightSubstituedEdges() {
		return weightSubstituedEdges.getSlider().getValue() / SLIDER_MULTIPLICATION_FACTOR;
	}

}
