package org.processmining.plugins.petrinet.configurable.ui.widgets.linewizard;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.processmining.plugins.petrinet.configurable.ui.widgets.UIUtils;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public abstract class LineWizardAbstractPage extends LineWizardPage {
	
	private JPanel fieldPanel;
	
	public LineWizardAbstractPage(JComponent root) {
		super(root);
	}
	
	protected void initialize() {
		pagePanel = SlickerFactory.instance().createRoundedPanel(10, new Color(192, 210, 192));
		pagePanel.setOpaque(true);
		pagePanel.setBackground(new Color(192, 210, 192));
		pagePanel.setLayout(new BoxLayout(pagePanel, BoxLayout.X_AXIS));
		
		fieldPanel = new JPanel();
		fieldPanel.setOpaque(false);
		
		pagePanel.add(fieldPanel);
		initializePageContents(pagePanel);
		
		JButton setFeatureButton = UIUtils.createCheckMarkButton();
		pagePanel.add(Box.createHorizontalStrut(10));
		pagePanel.add(Box.createHorizontalGlue());
		pagePanel.add(setFeatureButton);
		
		setWizardNextButton(setFeatureButton);
		
		showPage(false);
	}
	
	public abstract void initializePageContents(JPanel page);
	
}
