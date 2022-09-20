/**
 * 
 */
package org.processmining.plugins.petrinet.replayresult.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.processmining.framework.util.ui.widgets.ProMHeaderPanel;
import org.processmining.framework.util.ui.widgets.WidgetColors;

import com.fluxicon.slickerbox.components.RoundedPanel;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

/**
 * @author aadrians Oct 25, 2011
 * 
 */
public class ProMPropertiesPanelWithComp extends ProMHeaderPanel {
	private static final long serialVersionUID = 550951913919050808L;

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

	private final JPanel properties;
	protected final JScrollPane scrollPane;
	private boolean first = true;

	/**
	 * @param title
	 */
	public ProMPropertiesPanelWithComp(final String title) {
		super(title);
		properties = new ScrollablePanel();
		properties.setOpaque(false);
		properties.setLayout(new BoxLayout(properties, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(properties);
		scrollPane.setOpaque(true);
		scrollPane.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		scrollPane.getViewport().setOpaque(true);
		scrollPane.getViewport().setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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

	public void setPosition(double ratio) {
		if ((ratio < 0) || (ratio > 1)) {
			JScrollBar bar = scrollPane.getVerticalScrollBar();
			bar.setValue(bar.getMinimum());
			
			bar = scrollPane.getHorizontalScrollBar();
			bar.setValue(bar.getMinimum());
		} else {
			JScrollBar bar = scrollPane.getVerticalScrollBar();
			bar.setValue(((int) Math.floor((bar.getMaximum() - bar.getMinimum()) * ratio)) + bar.getMinimum());
			
			bar = scrollPane.getHorizontalScrollBar();
			bar.setValue(((int) Math.floor((bar.getMaximum() - bar.getMinimum()) * ratio)) + bar.getMinimum());			
		}
	}

	/**
	 * @param <T>
	 * @param name
	 * @param component
	 * @return
	 */
	public <T extends JComponent> T addProperty(final JComponent lblComponent, final T component) {
		if (!first) {
			properties.add(Box.createVerticalStrut(3));
		} else {
			first = false;
		}
		properties.add(packInfo(lblComponent, component));
		return component;
	}

	protected JComponent packInfo(final JComponent nameLabel, final JComponent component) {
		final RoundedPanel packed = new RoundedPanel(10, 0, 0);
		packed.setBackground(new Color(60, 60, 60, 160));
		packed.setLayout(new BoxLayout(packed, BoxLayout.X_AXIS));

		packed.add(Box.createHorizontalStrut(5));
		packed.add(nameLabel);
//		packed.add(Box.createHorizontalGlue());
		packed.add(component);
		packed.add(Box.createHorizontalStrut(5));
		
		packed.revalidate();
		return packed;
	}
}
