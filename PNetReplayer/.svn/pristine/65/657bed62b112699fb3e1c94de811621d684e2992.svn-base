/**
 * 
 */
package org.processmining.plugins.astar.petrinet.manifestreplay.ui;

import javax.swing.JComponent;

import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;

/**
 * @author aadrians
 * Feb 26, 2012
 *
 */
public class MapCostStep implements ProMWizardStep<PNManifestReplayerParameter>{
	private CreatePatternPanel patternCreatorPanel;
	private PatternMappingPanel patternMappingPanel;
	private ClassCostMapPanel costBasedCompleteGUI;
	
	public MapCostStep(CreatePatternPanel patternCreatorPanel, PatternMappingPanel patternMappingPanel){
		this.patternCreatorPanel = patternCreatorPanel;
		this.patternMappingPanel = patternMappingPanel;
	}
	
	public String getTitle() {
		return "Set Cost for Movements";
	}

	public JComponent getComponent(PNManifestReplayerParameter model) {
		this.costBasedCompleteGUI = new ClassCostMapPanel(patternMappingPanel.getTransClassCollection(), patternCreatorPanel.getEvClasses());
		return costBasedCompleteGUI;
	}

	public PNManifestReplayerParameter apply(PNManifestReplayerParameter model, JComponent component) {
		model.setMapEvClass2Cost(costBasedCompleteGUI.getMapEvClassToCost());
		model.setTrans2Cost(costBasedCompleteGUI.getMapTransClassToCost());
		model.setTransSync2Cost(costBasedCompleteGUI.getMapTransClassSyncToCost());
		model.setMaxNumOfStates(costBasedCompleteGUI.getMaxNumOfStates());
		return model;
	}

	public boolean canApply(PNManifestReplayerParameter model, JComponent component) {
		return true;
	}

}
