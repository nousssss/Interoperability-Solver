/**
 * 
 */
package org.processmining.plugins.astar.petrinet.manifestreplay.ui;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayer.TransClass2PatternMap;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClasses;

/**
 * @author aadrians
 * Feb 26, 2012
 *
 */
public class MapPattern2TransStep implements ProMWizardStep<PNManifestReplayerParameter>{
	private PatternMappingPanel patternMappingPanel;
	private CreatePatternPanel patternCreatorPanel;
	private PetrinetGraph net;
	private XLog log;
	
	public MapPattern2TransStep(PetrinetGraph net, XLog log, CreatePatternPanel patternCreatorPanel) {
		this.patternMappingPanel = new PatternMappingPanel();
		this.patternCreatorPanel = patternCreatorPanel;
		this.net = net;
		this.log = log;
	}

	public String getTitle() {
		return "Map Transition Classes to Patterns";
	}

	public JComponent getComponent(PNManifestReplayerParameter model) {
		patternMappingPanel.initiateTransClass(net, patternMappingPanel.getSelectedClassifier(), patternCreatorPanel.getEvClassPatternArr());
		return patternMappingPanel;
	}

	public PNManifestReplayerParameter apply(PNManifestReplayerParameter model, JComponent component) {
		// the mapping needs to be stored
		TransClasses transClasses = patternMappingPanel.getTransClasses();
		TransClass2PatternMap mapping = new TransClass2PatternMap(log, net, patternCreatorPanel.getSelectedEvClassifier(),
				transClasses, patternMappingPanel.getMapPattern());
		model.setMapping(mapping);
		return model;
	}

	public boolean canApply(PNManifestReplayerParameter model, JComponent component) {
		return true;
	}
	
	public PatternMappingPanel getPatternMappingPanel(){
		return patternMappingPanel;
	}
}
