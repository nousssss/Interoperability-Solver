/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.matchinstances.ui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.KeepInProMCache;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayMultipleAlignmentAlgorithm;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.IPNMatchInstancesLogReplayAlgorithm;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.fluxicon.slickerbox.ui.SlickerComboBoxUI;

/**
 * @author aadrians
 * 
 */
@PNReplayMultipleAlignmentAlgorithm
@KeepInProMCache
public class PNRepMatchInstancesAlgorithmStep extends PNReplayStep {

	private static final long serialVersionUID = 8899781737579767946L;

	private JComboBox combo;
	private JLabel infoLabel;

	public PNRepMatchInstancesAlgorithmStep(PluginContext context) {
		initComponents(context);
	}

	private void initComponents(PluginContext context) {
		// init instance
		SlickerFactory slickerFactory = SlickerFactory.instance();

		double size[][] = { { TableLayoutConstants.FILL }, { 80, 30, TableLayoutConstants.FILL } };
		setLayout(new TableLayout(size));
		String body = "<p>Select your replay algorithm.</p>";
		add(slickerFactory.createLabel("<html><h1>Select Algorithm</h1>" + body), "0, 0, l, t");

		// add combobox
		Set<Class<?>> coverageEstimatorClasses = context.getPluginManager().getKnownClassesAnnotatedWith(
				PNReplayMultipleAlignmentAlgorithm.class);
		IPNMatchInstancesLogReplayAlgorithm[] availAlgorithms = null;
		if (coverageEstimatorClasses != null) {
			List<IPNMatchInstancesLogReplayAlgorithm> algList = new LinkedList<IPNMatchInstancesLogReplayAlgorithm>();
			for (Class<?> coverClass : coverageEstimatorClasses) {
				try {
					Object inst = coverClass.newInstance();
					if (inst instanceof IPNMatchInstancesLogReplayAlgorithm) {
						IPNMatchInstancesLogReplayAlgorithm alg = (IPNMatchInstancesLogReplayAlgorithm) inst;
						algList.add(alg);
					}
				} catch (InstantiationException e1) {
					// do nothing
				} catch (IllegalAccessException e1) {
					// do nothing
				} catch (Exception exc) {
					// do nothing
				}
			}
			Collections.sort(algList, new Comparator<IPNMatchInstancesLogReplayAlgorithm>() {

				public int compare(IPNMatchInstancesLogReplayAlgorithm o1, IPNMatchInstancesLogReplayAlgorithm o2) {
					return o1.toString().compareTo(o2.toString());
				}
			});
			availAlgorithms = algList.toArray(new IPNMatchInstancesLogReplayAlgorithm[algList.size()]);
		}

		combo = new JComboBox(availAlgorithms);
		combo.setPreferredSize(new Dimension(150, 25));
		combo.setSize(new Dimension(150, 25));
		combo.setMinimumSize(new Dimension(150, 25));
		combo.setSelectedItem(0);
		combo.setUI(new SlickerComboBoxUI());
		add(combo, "0, 1");

		infoLabel = new JLabel();
		add(infoLabel, "0,2");

		// add action
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoLabel.setText(((IPNMatchInstancesLogReplayAlgorithm) combo.getSelectedItem()).getHTMLInfo());
			}
		});
		infoLabel.setText(((IPNMatchInstancesLogReplayAlgorithm) combo.getSelectedItem()).getHTMLInfo());
	}

	public IPNMatchInstancesLogReplayAlgorithm getAlgorithm() {
		return (IPNMatchInstancesLogReplayAlgorithm) combo.getSelectedItem();
	}

}
