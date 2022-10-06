package org.processmining.plugins.inductiveminer2.plugins;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.AttributesInfo;
import org.processmining.plugins.InductiveMiner.Triple;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import gnu.trove.set.hash.THashSet;

public class IdentifyPartialTracesDialog extends JPanel {

	private static final long serialVersionUID = -3395251859528488471L;
	private final JComboBox<String> attributes;
	private final Triple<JScrollPane, JList<String>, DefaultListModel<String>> startList;
	private final Triple<JScrollPane, JList<String>, DefaultListModel<String>> endList;
	private final JCheckBox emptyTraces;

	@SuppressWarnings("unchecked")
	public IdentifyPartialTracesDialog(final XLog log) {

		setLayout(new BorderLayout(10, 10));

		JPanel attributePanel = new JPanel();
		attributePanel.setOpaque(false);
		attributePanel.setLayout(new BoxLayout(attributePanel, BoxLayout.LINE_AXIS));
		add(attributePanel, BorderLayout.PAGE_START);

		attributePanel.add(new JLabel("attribute"));
		attributePanel.add(Box.createRigidArea(new Dimension(10, 10)));
		attributes = SlickerFactory.instance().createComboBox(new Object[0]);
		attributePanel.add(attributes);

		JPanel listsPanel = new JPanel();
		add(listsPanel, BorderLayout.CENTER);
		listsPanel.setLayout(new GridLayout(1, 2, 10, 10));
		listsPanel.setOpaque(false);

		{
			JPanel startPanel = new JPanel();
			listsPanel.add(startPanel);
			startPanel.setOpaque(false);
			startPanel.setLayout(new BorderLayout(5, 5));

			startPanel.add(new JLabel("A trace has a reliable start if it starts with an event having:"),
					BorderLayout.PAGE_START);

			startList = getList();
			startPanel.add(startList.getA(), BorderLayout.CENTER);
		}

		{
			JPanel endPanel = new JPanel();
			listsPanel.add(endPanel);
			endPanel.setOpaque(false);
			endPanel.setLayout(new BorderLayout(5, 5));

			endPanel.add(new JLabel("A trace has a reliable end if it ends with an event having:"),
					BorderLayout.PAGE_START);

			endList = getList();
			endPanel.add(endList.getA(), BorderLayout.CENTER);
		}

		emptyTraces = SlickerFactory.instance().createCheckBox("Empty traces have a reliable start and end", true);
		add(emptyTraces, BorderLayout.PAGE_END);

		//put initialisation messages
		attributes.addItem("Initialising...");
		attributes.setEnabled(false);
		fill(startList, new ArrayList<String>(Arrays.asList("Initialising...")));
		startList.getB().setEnabled(false);
		fill(endList, new ArrayList<String>(Arrays.asList("Initialising...")));
		endList.getB().setEnabled(false);

		//read the log asynchronously
		new Thread(new Runnable() {
			public void run() {
				final AttributesInfo info = new AttributesInfo(log);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						initGui(info);
					}
				});
			}
		}).start();
	}

	private void initGui(final AttributesInfo info) {
		attributes.removeAllItems();
		for (String attribute : info.getEventAttributes()) {
			attributes.addItem(attribute);
		}
		if (info.getEventAttributesMap().containsKey("concept:name")) {
			attributes.setSelectedItem("concept:name");
		} else if (!info.getEventAttributesMap().isEmpty()) {
			attributes.setSelectedIndex(0);
		}
		attributes.setEnabled(true);

		fill(startList, info.getEventAttributesMap().get(attributes.getSelectedItem()));
		startList.getB().setEnabled(true);
		startList.getB().setSelectionInterval(0, startList.getB().getModel().getSize() - 1);

		fill(endList, info.getEventAttributesMap().get(attributes.getSelectedItem()));
		endList.getB().setEnabled(true);
		endList.getB().setSelectionInterval(0, endList.getB().getModel().getSize() - 1);

		//update the lists on change of the attributes
		attributes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fill(startList, info.getEventAttributesMap().get(attributes.getSelectedItem()));
				startList.getB().setEnabled(true);
				startList.getB().setSelectionInterval(0, startList.getB().getModel().getSize() - 1);

				fill(endList, info.getEventAttributesMap().get(attributes.getSelectedItem()));
				endList.getB().setEnabled(true);
				endList.getB().setSelectionInterval(0, endList.getB().getModel().getSize() - 1);
			}
		});
	}

	public String getAttribute() {
		return (String) attributes.getSelectedItem();
	}

	public Set<String> getStartValues() {
		return new THashSet<>(startList.getB().getSelectedValuesList());
	}

	public Set<String> getEndValues() {
		return new THashSet<>(endList.getB().getSelectedValuesList());
	}

	public boolean emptyTracesAreReliable() {
		return emptyTraces.isSelected();
	}

	public static void fill(Triple<JScrollPane, JList<String>, DefaultListModel<String>> list,
			Collection<String> values) {
		list.getC().clear();
		ArrayList<String> l = new ArrayList<String>(values);
		Collections.sort(l);
		for (String a : l) {
			list.getC().addElement(a);
		}
	}

	public static Triple<JScrollPane, JList<String>, DefaultListModel<String>> getList() {
		DefaultListModel<String> valueLiteralSelectorListModel = new DefaultListModel<>();
		JList<String> valueLiteralSelector = new JList<>(valueLiteralSelectorListModel);
		valueLiteralSelector.setCellRenderer(new ListCellRenderer<String>() {
			protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

			public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
					boolean isSelected, boolean cellHasFocus) {

				JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
				if (!isSelected) {
					renderer.setOpaque(false);
				} else {
					renderer.setOpaque(true);
				}
				return renderer;
			}
		});
		valueLiteralSelector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scrollPane = new JScrollPane(valueLiteralSelector);

		scrollPane.getViewport().setOpaque(false);
		//scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		valueLiteralSelector.setOpaque(false);
		scrollPane.setOpaque(false);
		return Triple.of(scrollPane, valueLiteralSelector, valueLiteralSelectorListModel);
	}
}
