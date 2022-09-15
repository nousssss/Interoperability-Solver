package org.processmining.plugins.petrinet.configurable.ui.widgets.linewizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public abstract class LineWizardPage {

	protected JPanel 	pagePanel;
	private JButton 	wizardButton;
	
	private LineWizardPage nextPage;
	private JComponent root;

	public LineWizardPage(JComponent root) {
		this.root = root;
	}
	
	public final void setNextPage(LineWizardPage nextPage) {
		this.nextPage = nextPage;
	}
	
	protected abstract void initialize();
	
	/**
	 * Define the "next" button of this wizard page. It will install a button
	 * handerl to show the next page {@link #nextPage} and execute additional
	 * operations as defined in {@link #onNextButtonPress()}. This method must
	 * be called during initialization of the wizard page.
	 */
	protected final void setWizardNextButton(JButton button) {
		wizardButton = button;
		wizardButton.addActionListener(new LineWizardButtonHandler());
	}

	public final JPanel getPage() {
		return pagePanel;
	}
	
	public void showPage(boolean show) {
		pagePanel.setVisible(show);
	}
	
	public abstract void onNextButtonPress();

	public class LineWizardButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			LineWizardPage.this.onNextButtonPress();
			
			LineWizardPage.this.showPage(false);
			LineWizardPage.this.nextPage.showPage(true);
			LineWizardPage.this.getRoot().revalidate();
		}
	}
	
	public JComponent getRoot() {
		return root;
	}
	
}
