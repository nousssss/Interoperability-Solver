package org.processmining.models.workshop.klunnel;

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

public class KlunnelMiningDialog extends JPanel {

	/**
	 * Eclipse complained.
	 */
	private static final long serialVersionUID = 7800636166076962966L;

	/**
	 * Provide a dialog to allow a user to set their desired
	 * KlunnelMiningParameters.
	 * 
	 * @param log
	 * @param parameters
	 */
	public KlunnelMiningDialog(XLog log, final KlunnelMiningParameters parameters) {
		// Create table layout of two rows
		double size[][] = { { TableLayoutConstants.FILL }, { 30, TableLayoutConstants.FILL } };
		setLayout(new TableLayout(size));

		// Add title to the top row
		add(SlickerFactory.instance().createLabel("<html><h2>Select mining parameters</h2>"), "0, 0");

		// Get the classifiers to be shown
		Object classifiers[] = log.getClassifiers().toArray();

		// And put them in the bottom row
		final JList<Object> classifierList = new JList<>(classifiers);
		classifierList.setName("Select classifier");
		classifierList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Update the parameters when a user clicks on things
		classifierList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				parameters.setClassifier((XEventClassifier) classifierList.getSelectedValue());
			}
		});

		// And put the list in a scroll pane in case there are many many classifiers
		JScrollPane classifierScrollPane = new JScrollPane();
		SlickerDecorator.instance().decorate(classifierScrollPane, SlickerColors.COLOR_BG_3, SlickerColors.COLOR_FG,
				SlickerColors.COLOR_BG_1);
		classifierScrollPane.setPreferredSize(new Dimension(250, 300));
		classifierScrollPane.setViewportView(classifierList);
		add(classifierScrollPane, "0, 1");
	}

}
