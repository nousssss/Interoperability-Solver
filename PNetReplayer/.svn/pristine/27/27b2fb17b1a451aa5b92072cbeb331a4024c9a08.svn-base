/**
 * 
 */
package org.processmining.plugins.petrinet.replayer.algorithms.costbasedprefix;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.components.SlickerTabbedPane;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * @author aadrians Oct 21, 2011
 * 
 */
public class CostBasedPrefixUI extends JComponent {
	private static final long serialVersionUID = 8887832188862686779L;

	// default replay value
	private static int DEFREPLAYEDEVENTCOST = 1;
	private static int DEFINAPPROPRIATETRANSFIRECOST = 6;
	private static int DEFINITIATIVEREALTASKCOST = 2;
	private static int DEFINITIATIVEINVISTASKCOST = 0;
	private static int DEFSKIPPEDEVENTCOST = 5;
	private static int DEFLIMMAXNUMINSTANCES = 1000000;
	private static int MAXLIMMAXNUMINSTANCES = 5000000;

	// overall layout
	private SlickerTabbedPane tabPane; // tab basic/advance
	private JPanel advancedPanel;
	private JPanel basicPanel;

	/**
	 * BASIC PANEL
	 */
	private boolean useBasic = true;

	private JCheckBox basIdentifyInvi;
	private JCheckBox basIdentifyReal;
	private JCheckBox basMaxInstance;
	private NiceIntegerSlider limMaxEvents;

	/**
	 * ADVANCED PANEL
	 */
	// inappropriate transitions fire
	private ButtonGroup inappropriateFiringGroup;
	private JRadioButton yesInappropriateFiring;
	private JRadioButton noInappropriateFiring;
	private NiceIntegerSlider unsatisfiedEventsSlider;

	// skipping an event
	private ButtonGroup skipEventGroup;
	private JRadioButton yesSkipEvent;
	private JRadioButton noSkipEvent;
	private NiceIntegerSlider skipEventSlider;

	// initiated execute invi task
	private ButtonGroup execInviTaskEventsGroup;
	private JRadioButton yesExecInviTask;
	private JRadioButton noExecInviTask;
	private NiceIntegerSlider execInviTaskSlider;

	// initiated execute real task
	private ButtonGroup execRealTaskEventsGroup;
	private JRadioButton yesExecRealTask;
	private JRadioButton noExecRealTask;
	private NiceIntegerSlider execRealTaskSlider;

	// enabled execute task without tokens
	private JCheckBox execTaskWOTokens;

	// use max instances limitation
	private NiceIntegerSlider maxExpInstSlider;


	
	public CostBasedPrefixUI() {
		initComponents();
	};

	/**
	 * initialization of components
	 */
	private void initComponents() {
		double mainPanelSize[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL } };
		setLayout(new TableLayout(mainPanelSize));

		// init tab
		tabPane = new SlickerTabbedPane("Choose wizard", new Color(200, 200, 200, 230), new Color(0, 0, 0, 230),
				new Color(220, 220, 220, 150));

		SlickerFactory slickerFactory = SlickerFactory.instance();

		// init advance configuration
		basicPanel = new JPanel();
		basicPanel.setBackground(new Color(200, 200, 200));
		basicPanel.setSize(700, 465);

		int basicRowIndex = 1;
		double sizeBasic[][];
		sizeBasic = new double[][] { { 400, 350 }, { 100, 30, 30, 30, 30, 30, 60, 30, 30, 30, 35, 30 } };
		basicPanel.setLayout(new TableLayout(sizeBasic));
		basicPanel.add(
				slickerFactory.createLabel("<html><h1>Configure cost</h1><p>Check appropriate options</p></html>"),
				"0, 0, 1, 0, l, t");

		// init basic instance
		advancedPanel = new JPanel();
		advancedPanel.setBackground(new Color(200, 200, 200));

		basIdentifyInvi = slickerFactory.createCheckBox("Identify unobservable activities", true);
		basIdentifyReal = slickerFactory.createCheckBox("Identify skipped activities", true);
		basMaxInstance = slickerFactory.createCheckBox("Use max instances limitation", true);

		basicPanel.add(
				slickerFactory.createLabel("<html><h2>Any other goal apart of conformance checking?</h2></html>"),
				"0, " + String.valueOf(basicRowIndex) + ", 1, " + String.valueOf(basicRowIndex));
		basicRowIndex++;
		basicPanel.add(basIdentifyInvi, "0, " + String.valueOf(basicRowIndex++));
		basicPanel.add(basIdentifyReal, "0, " + String.valueOf(basicRowIndex++));
		basicRowIndex++;

		basicPanel.add(slickerFactory.createLabel("<html><h2>Additional replay configuration</h2></html>"), "0, "
				+ String.valueOf(basicRowIndex++));
		limMaxEvents = slickerFactory.createNiceIntegerSlider("", 1000, MAXLIMMAXNUMINSTANCES, DEFLIMMAXNUMINSTANCES,
				Orientation.HORIZONTAL);
		limMaxEvents.setPreferredSize(new Dimension(300, 20));
		limMaxEvents.setMaximumSize(new Dimension(300, 20));
		basicPanel.add(basMaxInstance, "0, " + String.valueOf(basicRowIndex));
		basicPanel.add(limMaxEvents, "1, " + String.valueOf(basicRowIndex++));

		int rowIndex = 1;

		double size[][] = new double[][] { { 400, 350 }, { 45, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30 } };
		advancedPanel.setLayout(new TableLayout(size));

		advancedPanel.add(slickerFactory.createLabel("<html><h1>Configure cost</h1>"), "0, 0, 1, 0, l, t");

		// max num of explored instances
		maxExpInstSlider = slickerFactory.createNiceIntegerSlider("", 1, MAXLIMMAXNUMINSTANCES, DEFLIMMAXNUMINSTANCES,
				Orientation.HORIZONTAL);
		maxExpInstSlider.setPreferredSize(new Dimension(200, 20));
		maxExpInstSlider.setMaximumSize(new Dimension(200, 20));
		advancedPanel.add(slickerFactory.createLabel("<html><h2>Max # instances</h2></html>"),
				"0, " + String.valueOf(rowIndex));
		advancedPanel.add(maxExpInstSlider, "1, " + String.valueOf(rowIndex++));

		// skipping event cost
		skipEventSlider = slickerFactory
				.createNiceIntegerSlider("", 1, 50, DEFSKIPPEDEVENTCOST, Orientation.HORIZONTAL);
		skipEventSlider.setPreferredSize(new Dimension(200, 20));
		skipEventSlider.setMaximumSize(new Dimension(200, 20));
		skipEventGroup = new ButtonGroup();
		yesSkipEvent = slickerFactory.createRadioButton("Identify inserted activities");
		noSkipEvent = slickerFactory.createRadioButton("Don't identify inserted activities");
		noSkipEvent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (noInappropriateFiring.isSelected()) {
					JOptionPane
							.showMessageDialog(
									null,
									"Either \"Identifying inserted activities\" or \"Allowing violating sync log+model\" must be allowed",
									"Alert", JOptionPane.ERROR_MESSAGE);
					yesSkipEvent.setSelected(true);
				}
			}
		});
		skipEventGroup.add(yesSkipEvent);
		skipEventGroup.add(noSkipEvent);
		yesSkipEvent.setSelected(true);

		advancedPanel.add(slickerFactory.createLabel("<html><h2>Cost of an Inserted Activity</h2>"), "0, " + rowIndex
				+ ", 1, " + String.valueOf(rowIndex++));
		advancedPanel.add(noSkipEvent, "0, " + String.valueOf(rowIndex++));
		advancedPanel.add(yesSkipEvent, "0, " + String.valueOf(rowIndex));
		advancedPanel.add(skipEventSlider, "1, " + String.valueOf(rowIndex++));

		// initiated execute invi task
		execInviTaskSlider = slickerFactory.createNiceIntegerSlider("", 0, 50, DEFINITIATIVEINVISTASKCOST,
				Orientation.HORIZONTAL);
		execInviTaskSlider.setPreferredSize(new Dimension(200, 20));
		execInviTaskSlider.setMaximumSize(new Dimension(200, 20));
		execInviTaskEventsGroup = new ButtonGroup();
		yesExecInviTask = slickerFactory.createRadioButton("Identify unobservable activities");
		noExecInviTask = slickerFactory.createRadioButton("Don't identify unobservable activities");
		execInviTaskEventsGroup.add(yesExecInviTask);
		execInviTaskEventsGroup.add(noExecInviTask);
		yesExecInviTask.setSelected(true);

		advancedPanel.add(slickerFactory.createLabel("<html><h2>Cost of an Unobservable Activity</h2>"), "0, "
				+ rowIndex + ", 1, " + String.valueOf(rowIndex++));
		advancedPanel.add(noExecInviTask, "0, " + String.valueOf(rowIndex++));
		advancedPanel.add(yesExecInviTask, "0, " + String.valueOf(rowIndex));
		advancedPanel.add(execInviTaskSlider, "1, " + String.valueOf(rowIndex++));

		// initiated execute real task
		execRealTaskSlider = slickerFactory.createNiceIntegerSlider("", 1, 50, DEFINITIATIVEREALTASKCOST,
				Orientation.HORIZONTAL);
		execRealTaskSlider.setPreferredSize(new Dimension(200, 20));
		execRealTaskSlider.setMaximumSize(new Dimension(200, 20));
		execRealTaskEventsGroup = new ButtonGroup();
		yesExecRealTask = slickerFactory.createRadioButton("Identify skipped activities");
		noExecRealTask = slickerFactory.createRadioButton("Don't identify skipped activities");
		execRealTaskEventsGroup.add(yesExecRealTask);
		execRealTaskEventsGroup.add(noExecRealTask);
		yesExecRealTask.setSelected(true);

		advancedPanel.add(slickerFactory.createLabel("<html><h2>Cost of a Skipped Activity</h2>"), "0, " + rowIndex
				+ ", 1, " + String.valueOf(rowIndex++));
		advancedPanel.add(noExecRealTask, "0, " + String.valueOf(rowIndex++));
		advancedPanel.add(yesExecRealTask, "0, " + String.valueOf(rowIndex));
		advancedPanel.add(execRealTaskSlider, "1, " + String.valueOf(rowIndex++));

		// inappropriate firing
		unsatisfiedEventsSlider = slickerFactory.createNiceIntegerSlider("", 1, 50, DEFINAPPROPRIATETRANSFIRECOST,
				Orientation.HORIZONTAL);
		unsatisfiedEventsSlider.setPreferredSize(new Dimension(200, 20));
		unsatisfiedEventsSlider.setMaximumSize(new Dimension(200, 20));

		inappropriateFiringGroup = new ButtonGroup();
		yesInappropriateFiring = slickerFactory.createRadioButton("Allow violating sync log+model move");
		noInappropriateFiring = slickerFactory.createRadioButton("Don't allow violating sync log+model move");
		noInappropriateFiring.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (noSkipEvent.isSelected()) {
					JOptionPane
							.showMessageDialog(
									null,
									"Either \"Identifying inserted activities\" or \"Allowing violating sync log+model\" must be allowed",
									"Alert", JOptionPane.ERROR_MESSAGE);
					yesInappropriateFiring.setSelected(true);
				}
			}
		});
		inappropriateFiringGroup.add(yesInappropriateFiring);
		inappropriateFiringGroup.add(noInappropriateFiring);
		noInappropriateFiring.setSelected(true);

		advancedPanel.add(
				slickerFactory.createLabel("<html><h2>Cost of a Violating Log+Model Synchronous Activity</h2></html>"),
				"0, " + rowIndex + ", 1, " + String.valueOf(rowIndex++));
		advancedPanel.add(noInappropriateFiring, "0, " + String.valueOf(rowIndex++));
		advancedPanel.add(yesInappropriateFiring, "0, " + String.valueOf(rowIndex));
		advancedPanel.add(unsatisfiedEventsSlider, "1, " + String.valueOf(rowIndex++));

		execTaskWOTokens = slickerFactory.createCheckBox(
				"Allow lazy sync. log+model activities (fire without consuming any tokens)", false);
		advancedPanel.add(execTaskWOTokens, "0, " + rowIndex + ", 1, " + String.valueOf(rowIndex++));

		// add all tabs
		tabPane.addTab("Basic wizard", basicPanel, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useBasic = true;
			}
		});
		tabPane.addTab("Advanced", advancedPanel, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				useBasic = false;
			}
		});
		add(tabPane, "0,0");
	}

	/**
	 * num of maximal states to be explored
	 * @return
	 */
	public Integer getMaxNumOfStates() {
		if (useBasic) {
			return basMaxInstance.isSelected() ? limMaxEvents.getValue() : MAXLIMMAXNUMINSTANCES;
		} else {
			return maxExpInstSlider.getValue();
		}
	}

	/**
	 * Cost if a transition is fired without proper number of tokens
	 * @return
	 */
	public Integer getInappropriateTransFireCost() {
		if (useBasic) {
			return 0;
		} else {
			return yesInappropriateFiring.isSelected() ? unsatisfiedEventsSlider.getValue() : 0;
		}
	}

	/**
	 * Cost of event that has been replayed
	 * @return
	 */
	public Integer getReplayedEventCost() {
		return DEFREPLAYEDEVENTCOST;
	}

	/**
	 * Cost of move on log only (ignoring an event)
	 * @return
	 */
	public Integer getSkippedEventCost() {
		if (useBasic) {
			return DEFSKIPPEDEVENTCOST;
		} else {
			return yesSkipEvent.isSelected() ? skipEventSlider.getValue() : 0;
		}
	}

	/**
	 * Cost of events yet still to be replayed
	 * @return
	 */
	public Integer getHeuristicDistanceCost() {
		return DEFREPLAYEDEVENTCOST;
	}

	/**
	 * Cost of firing slient step
	 * @return
	 */
	public Integer getSelfExecInviTaskCost() {
		if (useBasic) {
			return basIdentifyInvi.isSelected() ? DEFINITIATIVEINVISTASKCOST : 0;
		} else {
			return yesExecInviTask.isSelected() ? execInviTaskSlider.getValue() : 0;
		}
	}

	/**
	 * Cost of firing transition
	 * @return
	 */
	public Integer getSelfExecRealTaskCost() {
		if (useBasic) {
			return basIdentifyReal.isSelected() ? DEFINITIATIVEREALTASKCOST : 0;
		} else {
			return yesExecRealTask.isSelected() ? execRealTaskSlider.getValue() : 0;
		}
	}

	/**
	 * Allow firing silent steps
	 * @return
	 */
	public Boolean isAllowInviTaskMove() {
		if (useBasic){
			return basIdentifyInvi.isSelected();
		} else {
			return yesExecInviTask.isSelected();
		}
	}

	/**
	 * Allow firing non-silent transitions
	 * @return
	 */
	public Boolean isAllowRealTaskMove() {
		if (useBasic){
			return basIdentifyReal.isSelected();
		} else {
			return yesExecRealTask.isSelected();
		}
	}

	/**
	 * Allow ignoring events (move on log)
	 * @return
	 */
	public Boolean isAllowEventSkip() {
		if (useBasic){
			return true;
		} else {
			return yesSkipEvent.isSelected();
		}
	}

	/**
	 * Allowing transitions to be fired without taking any tokens
	 * @return
	 */
	public Boolean isAllowExecWOTokens() {
		if (useBasic){
			return false;
		} else {
			return execTaskWOTokens.isSelected();
		}
	}

	/**
	 * Allowing transitions to be fired with only some (but not all) input tokens
	 * @return
	 */
	public Boolean isAllowExecViolating() {
		if (useBasic){
			return false;
		} else {
			return yesInappropriateFiring.isSelected();
		}
	}

}
