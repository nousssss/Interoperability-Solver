package org.processmining.plugins.petrinet.configurable.ui.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.framework.util.ui.widgets.WidgetColors;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * Abstract class for configuration UIs providing a standard panel for controls
 * used by all subclasses.
 * 
 * @author dfahland
 * 
 * @param <INPUT>
 * @param <OUTPUT>
 */
public abstract class Configuration_UI<INPUT,OUTPUT> implements Structured_UI<INPUT, OUTPUT> {
	
	private		JLabel 			id_fixed;
	private		ProMTextField 	id_editable;
	private 		JPanel optionsPanel;
	private 		JPanel container;
	
	private boolean hasEditableId;
	
	public static final Color BACKGROUND_INACTIVE = WidgetColors.PROPERTIES_BACKGROUND;
	public static final Color BACKGROUND_ACTIVE = new Color(210, 210, 210);

	protected void initialize(String label, int line_height) {
		SlickerFactory f = SlickerFactory.instance();
		
		container = SlickerFactory.instance().createRoundedPanel(10, BACKGROUND_INACTIVE);
		container.setOpaque(false);
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		container.setMinimumSize(new Dimension(300, line_height));
		container.setPreferredSize(new Dimension(680, line_height));
		container.setMaximumSize(new Dimension(1000, line_height));
		
		id_fixed = f.createLabel(label);
		id_fixed.setMinimumSize(new Dimension(150, line_height));
		id_fixed.setPreferredSize(new Dimension(150, line_height));
		id_fixed.setMaximumSize(new Dimension(150, line_height));
		
		id_editable = new ProMTextField();
		id_editable.setText(label);
		id_editable.setMinimumSize(new Dimension(150, line_height));
		id_editable.setPreferredSize(new Dimension(150, line_height));
		id_editable.setMaximumSize(new Dimension(150, line_height));

		container.add(id_fixed);
		container.add(id_editable);
		
		setIdEditable(false);
		setId(label);
		
		container.add(Box.createHorizontalStrut(20));
		
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
		optionsPanel.setOpaque(false);
//		optionsPanel.setMinimumSize(new Dimension(300, line_height));
//		optionsPanel.setPreferredSize(new Dimension(500, line_height));
//		optionsPanel.setMaximumSize(new Dimension(1000, line_height));
		container.add(optionsPanel);

		initializeOptionsPanel(optionsPanel);
		
		installHighlighter(container);
	}
	
	/**
	 * Toggle whether name of this feature is editable
	 * @param editable
	 */
	public void setIdEditable(boolean editable) {
		this.hasEditableId = editable;
		id_editable.setVisible(editable);
		id_fixed.setVisible(!editable);
	}
	
	/**
	 * @return name set for this feature (either fixed or editable)
	 */
	public String getId() {
		if (hasEditableId) return id_editable.getText();
		else return id_fixed.getText();
	}
	
	/**
	 * Set name set for this feature (either fixed or editable)
	 * @param name
	 */
	public void setId(String name) {
		id_editable.setText(name);
		id_fixed.setText(name);
	}

	/**
	 * The provided panel is filled with controls that set the values of this feature. 
	 * 
	 * @param optionsPanel
	 */
	protected abstract void initializeOptionsPanel(JPanel optionsPanel);
	
	/**
	 * Whether to show the options provided by this panel
	 * @param show
	 */
	public void showOptions(boolean show) {
		optionsPanel.setVisible(show);
	}
	
	/**
	 * @return the panel containing all visual controls
	 */
	public JPanel getPanel() {
		return container;
	}
	
	private JComponent root;
	
	/**
	 * Set the root component of this ui element. May be needed to update
	 * visualization of the component in case of layout changes.
	 * 
	 * @param root
	 */
	public void setRoot(JComponent root) {
		this.root = root;
	}
	
	/**
	 * @return root component of this ui
	 */
	public JComponent getRoot() {
		return root;
	}
	
	/**
	 * called when the mouse enters the panel
	 */
	protected void handlePanelIsActive() {
		container.setBackground(BACKGROUND_ACTIVE);
		container.repaint();
	}
	
	/**
	 * called when the mouse leaves the panel
	 */
	protected void handlePanelIsInActive() {
		container.setBackground(BACKGROUND_INACTIVE);
		container.repaint();
	}
	
	
	private void installHighlighter(final Component component) {
		component.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(final MouseEvent arg0) { /* ignore */
			}

			@Override
			public void mouseEntered(final MouseEvent arg0) {
				handlePanelIsActive();

			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
				handlePanelIsInActive();

			}

			@Override
			public void mousePressed(final MouseEvent arg0) { /* ignore */
			}

			@Override
			public void mouseReleased(final MouseEvent arg0) { /* ignore */
			}
		});
		if (component instanceof Container) {
			for (final Component child : ((Container) component).getComponents()) {
				installHighlighter(child);
			}
		}
	}
	
}
