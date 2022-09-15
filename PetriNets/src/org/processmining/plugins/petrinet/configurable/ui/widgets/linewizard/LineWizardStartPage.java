package org.processmining.plugins.petrinet.configurable.ui.widgets.linewizard;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;

import org.processmining.plugins.petrinet.configurable.ui.widgets.UIUtils;

import com.fluxicon.slickerbox.colors.SlickerColors;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class LineWizardStartPage extends LineWizardPage {
	
	public LineWizardStartPage(JComponent root) {
		super(root);
		initialize();
	}
	
	protected void initialize() {
		pagePanel = SlickerFactory.instance().createRoundedPanel(10, SlickerColors.COLOR_TRANSPARENT);
		pagePanel.setOpaque(false);
		pagePanel.setLayout(new BoxLayout(pagePanel, BoxLayout.X_AXIS));
		JButton addFeatureButton = UIUtils.createPlusButton();
		pagePanel.add(Box.createHorizontalGlue());
		pagePanel.add(addFeatureButton);
		pagePanel.add(Box.createHorizontalGlue());

		setWizardNextButton(addFeatureButton);
		
		showPage(true);
	}
	
	public void onNextButtonPress() {
	}

}