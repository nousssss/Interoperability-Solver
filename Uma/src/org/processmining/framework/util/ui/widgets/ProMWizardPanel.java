package org.processmining.framework.util.ui.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import com.fluxicon.slickerbox.components.RoundedPanel;
import com.fluxicon.slickerbox.ui.SlickerRadioButtonUI;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

public class ProMWizardPanel extends ProMHeaderPanel {
	
	private static class ScrollablePanel extends JPanel implements Scrollable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		@Override
		public int getScrollableBlockIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
			return orientation == SwingConstants.VERTICAL ? getParent().getHeight() : getParent().getWidth();
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		@Override
		public int getScrollableUnitIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
			final int hundredth = (orientation == SwingConstants.VERTICAL ? getParent().getHeight() / 100 : getParent()
					.getWidth()) / 100;
			return hundredth == 0 ? 1 : hundredth;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel properties;

	private boolean first = true;
	
	private ButtonGroup allOptions = new ButtonGroup();
	
	public LinkedList<JRadioButton> optionButtons = new LinkedList<JRadioButton>();

	/**
	 * @param title
	 */
	public ProMWizardPanel(final String title) {
		super(title);
		properties = new ScrollablePanel();
		properties.setOpaque(false);
		properties.setLayout(new BoxLayout(properties, BoxLayout.Y_AXIS));
		final JScrollPane scrollPane = new JScrollPane(properties);
		scrollPane.setOpaque(true);
		scrollPane.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		scrollPane.getViewport().setOpaque(true);
		scrollPane.getViewport().setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(true);
		vBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		vBar = scrollPane.getHorizontalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(true);
		vBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		add(scrollPane);
	}

	/**
	 * @param <T>
	 * @param name
	 * @param component
	 * @return
	 */
	public <T extends JComponent> T addOption(final String name, final T component) {
		if (!first) {
			properties.add(Box.createVerticalStrut(3));
		} else {
			first = false;
		}
		properties.add(packInfoOption(name, component));
		return component;
	}

	private Component findComponent(final Component component) {
		if (component instanceof AbstractButton)
			return component;
		if (component instanceof JTextComponent)
			return component;
		if (component instanceof Container) {
			for (final Component child : ((Container) component).getComponents()) {
				final Component result = findComponent(child);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	private void installHighlighter(final Component component, final RoundedPanel target) {
		component.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(final MouseEvent arg0) { /* ignore */
			}

			@Override
			public void mouseEntered(final MouseEvent arg0) {
				target.setBackground(new Color(60, 60, 60, 240));
				target.repaint();
			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
				target.setBackground(new Color(60, 60, 60, 160));
				target.repaint();
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
				installHighlighter(child, target);
			}
		}
	}
	
	protected RoundedPanel packInfo_Background(final JComponent component) {
		final RoundedPanel packed = new RoundedPanel(10, 0, 0);
		packed.setBackground(new Color(60, 60, 60, 160));
		final RoundedPanel target = packed;
		final Component actualComponent = findComponent(component);
		packed.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(final MouseEvent arg0) {
				if (actualComponent != null) {
					if (actualComponent instanceof AbstractButton) {
						final AbstractButton button = (AbstractButton) actualComponent;
						button.doClick();
					}
					if (actualComponent instanceof JTextComponent) {
						final JTextComponent text = (JTextComponent) actualComponent;
						if (text.isEnabled() && text.isEditable()) {
							text.selectAll();
						}
						text.grabFocus();
					}
				}
			}

			@Override
			public void mouseEntered(final MouseEvent arg0) {
				target.setBackground(new Color(60, 60, 60, 240));
				target.repaint();
			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
				target.setBackground(new Color(60, 60, 60, 160));
				target.repaint();
			}

			@Override
			public void mousePressed(final MouseEvent arg0) { /* ignore */
			}

			@Override
			public void mouseReleased(final MouseEvent arg0) { /* ignore */
			}
		});
		installHighlighter(component, target);
		packed.setLayout(new BoxLayout(packed, BoxLayout.X_AXIS));
		return packed;
	}
	
	protected RoundedPanel packInfo(final JComponent component) {

		RoundedPanel packed = packInfo_Background(component);
		
		packed.add(Box.createHorizontalStrut(5));
		packed.add(component);
		packed.add(Box.createHorizontalGlue());
		packed.add(Box.createHorizontalStrut(5));
		packed.revalidate();
		return packed;
	}

	protected RoundedPanel packInfoOption(final String name, final JComponent component) {

		final JRadioButton button = new JRadioButton(name, first);
		button.setUI(new SlickerRadioButtonUI());
		button.setOpaque(false);
		button.setForeground(WidgetColors.TEXT_COLOR);
		button.setFont(button.getFont().deriveFont(12f));
		button.setMinimumSize(new Dimension(250, 20));
		button.setMaximumSize(new Dimension(250, 1000));
		button.setPreferredSize(new Dimension(250, 30));
		
		allOptions.add(button);
		optionButtons.add(button);
		
		RoundedPanel packed;
		if (component != null) {
			packed = packInfo_Background(component);
			packed.add(Box.createHorizontalStrut(5));
			packed.add(button);
			packed.add(Box.createHorizontalGlue());
			packed.add(component);
			packed.add(Box.createHorizontalStrut(5));
			packed.revalidate();
		} else {
			packed = packInfo_Background(button);
			packed.add(Box.createHorizontalStrut(5));
			packed.add(button);
			packed.add(Box.createHorizontalGlue());
			packed.add(Box.createHorizontalStrut(5));
			packed.revalidate();
		}
		return packed;
	}

}
