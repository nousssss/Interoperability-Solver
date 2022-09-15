package org.processmining.plugins.petrinet.configurable.ui;

import org.processmining.plugins.petrinet.configurable.ui.widgets.linewizard.AbstractLineWizard;

/**
 * Line wizard to add a new configurable feature to a feature group.
 * 
 * @author dfahland
 */
public class AddFeatureWizard extends AbstractLineWizard {

	private static final long serialVersionUID = 1L;
	private ConfigurableFeatureGroup_UI parent;
	
	public AddFeatureWizard(ConfigurableFeatureGroup_UI parent) {
		super(parent.getRoot());
		this.parent = parent;
		initialize();
	}
	
	@Override
	protected void initialize() {
		
		super.initialize();
		
		addPage(new AddFeatureWizardPage(parent));
		
		super.finalize();
	}
	
	

}
