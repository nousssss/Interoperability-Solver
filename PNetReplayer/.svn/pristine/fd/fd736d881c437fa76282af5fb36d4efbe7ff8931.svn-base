/**
 * 
 */
package org.processmining.plugins.petrinet.replayresult.visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.processmining.framework.util.ui.widgets.WidgetColors;

import com.fluxicon.slickerbox.components.RoundedPanel;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

/**
 * @author aadrians Oct 26, 2011
 * 
 */
public class ProMTableWithoutHeader extends RoundedPanel{

		private static final long serialVersionUID = -6957341403454873052L;

		private final JTable table;

		public ProMTableWithoutHeader(final TableModel model) {
			this(model, null);
		}

		public ProMTableWithoutHeader(final TableModel model, final TableColumnModel columnModel) {
			super(10, 5, 0);

			table = createTable(model, columnModel);
			table.setBackground(WidgetColors.COLOR_LIST_BG);
			table.setForeground(WidgetColors.COLOR_LIST_FG);
			table.setSelectionBackground(WidgetColors.COLOR_LIST_SELECTION_BG);
			table.setSelectionForeground(WidgetColors.COLOR_LIST_SELECTION_FG);

			final JScrollPane scroller = new JScrollPane(table);
			scroller.setOpaque(false);
			scroller.setBorder(BorderFactory.createEmptyBorder());
			scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			JScrollBar vBar = scroller.getVerticalScrollBar();
			vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
					WidgetColors.COLOR_NON_FOCUS, 4, 12));
			vBar.setOpaque(true);
			vBar.setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
			vBar = scroller.getHorizontalScrollBar();
			vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
					WidgetColors.COLOR_NON_FOCUS, 4, 12));
			vBar.setOpaque(true);
			vBar.setBackground(WidgetColors.COLOR_ENCLOSURE_BG);

			table.setTableHeader(null);
			table.setShowHorizontalLines(false);
			table.setGridColor(WidgetColors.COLOR_ENCLOSURE_BG);
			table.setFont(table.getFont().deriveFont(Font.BOLD));

			scroller.getViewport().setBackground(WidgetColors.COLOR_LIST_BG);

			setBackground(new Color(0, 0, 0, 160));
			setLayout(new BorderLayout());
			setMinimumSize(new Dimension(200, 100));
			setMaximumSize(new Dimension(1000, 1000));
			setPreferredSize(new Dimension(1000, 200));
			add(Box.createHorizontalStrut(5), BorderLayout.WEST);
			add(scroller, BorderLayout.CENTER);
			add(Box.createHorizontalStrut(5), BorderLayout.EAST);

			table.setColumnSelectionAllowed(false);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		}

		public TableCellEditor getCellEditor(final int row, final int col) {
			return table.getCellEditor(row, col);
		}

		public JTableHeader getTableHeader() {
			return null;
		}

		public Object getValueAt(final int row, final int column) {
			return table.getValueAt(row, column);
		}

		public void setPreferredWidth(final int column, final int width) {
			table.getColumnModel().getColumn(column).setPreferredWidth(width);
		}

		public void setRowSorter(final int column, final Comparator<?> comparator) {
			final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
			sorter.setSortsOnUpdates(true);
			sorter.setComparator(column, null);
			setRowSorter(sorter);
			sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(column, SortOrder.ASCENDING)));
		}

		public void setRowSorter(final TableRowSorter<? extends TableModel> sorter) {
			table.setRowSorter(sorter);
			sorter.sort();
		}

		protected JTable createTable(final TableModel model, final TableColumnModel columnModel) {
			return new JTable(model, columnModel);
		}

}
