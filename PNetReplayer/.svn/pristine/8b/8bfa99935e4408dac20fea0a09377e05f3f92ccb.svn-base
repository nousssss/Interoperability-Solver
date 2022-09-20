/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.syncproduct;

import info.clearthought.layout.TableLayout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * @author aadrians
 * Oct 21, 2011
 *
 */
public class SyncProductUI extends JComponent {
	private static final long serialVersionUID = -8021459349452621542L;

	// skipping an event
	private NiceIntegerSlider moveOnLogOnlySlider;

	// initiated execute invi task
	private NiceIntegerSlider execInviTaskSlider;

	// initiated execute real task
	private NiceIntegerSlider execRealTaskSlider;

	// violating task execution 
	private JCheckBox useViolatingTrans;
	private JCheckBox useSubViolatingTrans;
	private NiceIntegerSlider execSyncViolateSlider;

	public SyncProductUI() {
		initComponents();
	}

	private void initComponents() {
		// init instance
		SlickerFactory slickerFactory = SlickerFactory.instance();
		int rowIndex = 1;

		double size[][];
		size = new double[][] { { 400, 350 },
				{ 100, 40, 40, 40, 40, 30, 20, 20} };
		setLayout(new TableLayout(size));

		add(slickerFactory.createLabel("<html><h1>Synchronous product replay</h1>"), "0, 0, 1, 0, l, t");

		// skipping event cost
		moveOnLogOnlySlider = slickerFactory.createNiceIntegerSlider("", 0, 100, 5, Orientation.HORIZONTAL);
		moveOnLogOnlySlider.setPreferredSize(new Dimension(200, 20));
		moveOnLogOnlySlider.setMaximumSize(new Dimension(200, 20));
		add(slickerFactory.createLabel("<html><h2>Cost of an Inserted Activity</h2>"), "0, "
				+ String.valueOf(rowIndex));
		add(moveOnLogOnlySlider, "1, " + String.valueOf(rowIndex++));

		// initiated execute invi task
		execInviTaskSlider = slickerFactory.createNiceIntegerSlider("", 0, 100, 0, Orientation.HORIZONTAL);
		execInviTaskSlider.setPreferredSize(new Dimension(200, 20));
		execInviTaskSlider.setMaximumSize(new Dimension(200, 20));
		add(slickerFactory.createLabel("<html><h2>Cost of an Unobservable Activity</h2>"), "0, "
				+ String.valueOf(rowIndex));
		add(execInviTaskSlider, "1, " + String.valueOf(rowIndex++));

		// initiated execute real task
		execRealTaskSlider = slickerFactory.createNiceIntegerSlider("", 0, 100, 2, Orientation.HORIZONTAL);
		execRealTaskSlider.setPreferredSize(new Dimension(200, 20));
		execRealTaskSlider.setMaximumSize(new Dimension(200, 20));
		add(slickerFactory.createLabel("<html><h2>Cost of a Skipped Activity</h2>"), "0, " + String.valueOf(rowIndex));
		add(execRealTaskSlider, "1, " + String.valueOf(rowIndex++));

		// executing violation cost
		useViolatingTrans = slickerFactory.createCheckBox("Allow violating synchronization", false);
		useViolatingTrans.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!useViolatingTrans.isSelected()) {
					useSubViolatingTrans.setSelected(false);
				}
			}
		});
		
		execSyncViolateSlider = slickerFactory.createNiceIntegerSlider("", 0, 100, 5, Orientation.HORIZONTAL);
		execSyncViolateSlider.setPreferredSize(new Dimension(220, 20));
		add(slickerFactory.createLabel("<html><h2>Violating synchronization</h2>"), "0, " + String.valueOf(rowIndex++));
		add(useViolatingTrans, "0, " + String.valueOf(rowIndex));
		add(execSyncViolateSlider, "1, " + String.valueOf(rowIndex++));

		useSubViolatingTrans = slickerFactory.createCheckBox("Allow partially taking tokens", false);
		useSubViolatingTrans.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useViolatingTrans.setSelected(true);
			}
		});

		add(useSubViolatingTrans, "0, " + String.valueOf(rowIndex++));
	}

	public Integer getCostMoveOnLogOnly() {
		return moveOnLogOnlySlider.getValue();
	}

	public Integer getCostMoveOnModelOnlyInvi() {
		return execInviTaskSlider.getValue();
	}

	public Integer getCostMoveOnLogModelOnlyReal() {
		return execRealTaskSlider.getValue();
	}

	public Integer getCostMoveSyncViolating() {
		return useViolatingTrans.isSelected() ? execSyncViolateSlider.getValue() : -1;
	}

	public Boolean isSyncViolatingPartiallyAllowed() {
		return useSubViolatingTrans.isSelected();
	}
}
