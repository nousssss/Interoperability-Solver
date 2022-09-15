package org.processmining.plugins.workshop.nouss;

import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;

import com.fluxicon.slickerbox.colors.SlickerColors;
import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;


public class MiningDialog extends JPanel {
	
	public MiningDialog(XLog log, final MiningParameters parameters) {
		/*
		 * Get a layout containing a single column and two rows, where the top
		 * row height equals 30.
		 */
		double size[][] = { { TableLayoutConstants.FILL }, { 30, TableLayoutConstants.FILL } };
		setLayout(new TableLayout(size));

		/*
		 * Put a meaningful text in the top row.
		 */
		add(SlickerFactory.instance().createLabel("<html><h2>Select mining parameters</h2>"), "0, 0");

		/*
		 * Put a list containing all available classifiers in the bottom row.
		 */
		Object classifiers[] = log.getClassifiers().toArray();

		final JList<?> classifierList = new javax.swing.JList(classifiers);
		classifierList.setName("Select classifier");
		classifierList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		/*
		 * Create a selection listener on this list that changes the parameters
		 * accordingly.
		 */
		classifierList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				parameters.setClassifier((XEventClassifier) classifierList.getSelectedValue());
			}
		});
		JScrollPane classifierScrollPane = new javax.swing.JScrollPane();
		SlickerDecorator.instance().decorate(classifierScrollPane, SlickerColors.COLOR_BG_3, SlickerColors.COLOR_FG,
				SlickerColors.COLOR_BG_1);
		classifierScrollPane.setPreferredSize(new Dimension(250, 300));
		classifierScrollPane.setViewportView(classifierList);
		add(classifierScrollPane, "0, 1");
	}
}
