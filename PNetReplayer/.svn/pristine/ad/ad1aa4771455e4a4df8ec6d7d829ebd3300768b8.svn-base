/**
 * 
 */
package org.processmining.plugins.astar.petrinet.manifestreplay.ui;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayer.algorithms.IPNManifestReplayAlgorithm;

/**
 * @author aadrians
 * Feb 26, 2012
 *
 */
public class ChooseAlgorithmStep  implements ProMWizardStep<PNManifestReplayerParameter>{
	private ChooseAlgorithmPanel panel;
	private Marking initialMarking;
	private Marking[] finalMarkings;
	private PetrinetGraph net; 
	private XLog log;
	
	
	public ChooseAlgorithmStep(PetrinetGraph net, XLog log, Marking initialMarking, Marking[] finalMarkings){
		this.initialMarking = initialMarking;
		this.finalMarkings = finalMarkings;
		this.net = net;
		this.log = log;
	}
	
	public String getTitle() {
		return "Choose Algorithm";
	}

	public JComponent getComponent(PNManifestReplayerParameter model) {
		this.panel = new ChooseAlgorithmPanel(net, log, initialMarking, finalMarkings);
		return panel;
	}

	public PNManifestReplayerParameter apply(PNManifestReplayerParameter model, JComponent component) {
		model.setInitMarking(initialMarking);
		model.setFinalMarkings(finalMarkings);
		return model;
	}

	public boolean canApply(PNManifestReplayerParameter model, JComponent component) {
		return true;
	}

	/**
	 * May return null if there is no algorithm selected
	 * @return
	 */
	public IPNManifestReplayAlgorithm getSelectedAlgorithm() {
		return panel.getSelectedAlgorithm();
	}

}
