/**
 * 
 */
package org.processmining.plugins.astar.petrinet.manifestreplay.ui;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;

/**
 * @author aadrians
 * Feb 26, 2012
 *
 */
public class CreatePatternStep implements ProMWizardStep<PNManifestReplayerParameter>{
	private CreatePatternPanel patternCreatorPanel;
	
	public CreatePatternStep(XLog log, XEventClassifier[] availableClassifiers){
		this.patternCreatorPanel = new CreatePatternPanel(log, availableClassifiers);
	}
	
	public String getTitle() {
		return "Create Event Class Pattern";
	}

	public JComponent getComponent(PNManifestReplayerParameter model) {
		return patternCreatorPanel;
	}

	public PNManifestReplayerParameter apply(PNManifestReplayerParameter model, JComponent component) {
		return model;
	}

	public boolean canApply(PNManifestReplayerParameter model, JComponent component) {
		return true;
	}
	
	public XEventClassifier getSelectedEvClassifier(){
		return patternCreatorPanel.getSelectedEvClassifier();
	}
	
	public CreatePatternPanel getPatternCreatorPanel(){
		return patternCreatorPanel;
	}
}
