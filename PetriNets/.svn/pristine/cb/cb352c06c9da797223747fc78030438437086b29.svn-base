package org.processmining.plugins.petrinet.configurable.ui.widgets.linewizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class AbstractLineWizard extends JPanel {
	
	private LineWizardStartPage 			startPage;
	private List<LineWizardAbstractPage> 	wizardPages;
	
	protected JComponent root;
	
	public AbstractLineWizard(JComponent root) {
		this.root = root;
	}
	
	protected void initialize() {

		this.setOpaque(false);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		startPage = new LineWizardStartPage(root);
		this.add(startPage.getPage());
		wizardPages = new ArrayList<LineWizardAbstractPage>();

	}
	
	public void addPage(LineWizardAbstractPage page) {
		wizardPages.add(page);
		this.add(page.getPage());
	}
	
	protected void finalize() {
		startPage.setNextPage(wizardPages.get(0));
		int lastPage = wizardPages.size()-1;
		for (int i=0; i<lastPage; i++) {
			wizardPages.get(i).setNextPage(wizardPages.get(i+1));
		}
		wizardPages.get(lastPage).setNextPage(startPage);
		
		startPage.showPage(true);
		for (int i=0; i<wizardPages.size(); i++) {
			wizardPages.get(i).showPage(false);
		}
	}

	/**
	 * Handler to switch from one panel to the next panel and update
	 * visualization.
	 * 
	 * @author dfahland
	 * 
	 */
	public static class NextPanelHandler implements ActionListener {

		private JComponent root;
		private JPanel oldPanel, newPanel;
		public NextPanelHandler(JComponent root, JPanel oldPanel, JPanel newPanel) {
			this.root = root;
			this.oldPanel = oldPanel;
			this.newPanel = newPanel;
		}
		
		public void actionPerformed(ActionEvent e) {
			oldPanel.setVisible(false);
			newPanel.setVisible(true);
			root.revalidate();
		}
		
	}
	
}
