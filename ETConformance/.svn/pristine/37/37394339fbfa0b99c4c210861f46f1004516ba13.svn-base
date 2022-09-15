/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/

package org.processmining.plugins.etconformance;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.fluxicon.slickerbox.colors.SlickerColors;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * Settings (and Setting UI) for ETConformance.
 * 
 * @author Jorge Munoz-Gama
 */
public class ETCSettings {

	/** Results object to store the settings */
	ETCResults res;
	
	/** Spinner for Escaping Threshold */
	JSpinner spinEscTh;
	/** CheckBox of Confidence */
	JCheckBox cBConfidence;
	/** Spinner for K parameter of confidence */
	JSpinner spinK;
	/** CheckBox of MDT */
	JCheckBox cBMDT;
	/** CheckBox of Severity */
	JCheckBox cBSeverity;
	/** Spinner for Severity Threshold */
	JSpinner spinSeverity;
	/** CheckBox of Severity */
	JCheckBox cBAutomaton;
	/** CheckBox of Lazy Invisible Heuristics */
	JCheckBox cBLazyInv;
	/** CheckBox of Lazy Invisible Heuristics */
	JCheckBox cBRandomIndet;

	/**
	 * Main constructor that initialize the default setting values.
	 */
	public ETCSettings(ETCResults res) {
		this.res = res;
	}

	/**
	 * Initialize user interface
	 */
	public JComponent initComponents() {
		//Factory to create ProM swing components
		SlickerFactory factory = SlickerFactory.instance();

		//Setting the Panel
		JPanel panel = factory.createRoundedPanel(20, SlickerColors.COLOR_BG_1);
		panel.setLayout(new TableLayout(new double[][] { { 50, 50, 50, 50, 50, 50, 50},
				{ 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30} }));
		
		
		//ESCAPING THRESHOLD SPINNER-------------------------------------------
        Double currentEsc = res.getEscTh();
        Double minEsc = new Double(0.00);
        Double maxEsc = new Double(1.00);
        Double stepEsc = new Double(0.01);
        //creating the number spinner model
        SpinnerNumberModel modelEsc = new SpinnerNumberModel(currentEsc, minEsc, maxEsc, stepEsc);
        //creating the number spinner
        spinEscTh = new JSpinner(modelEsc);
        JFormattedTextField tfEsc =
  	      ((JSpinner.DefaultEditor)spinEscTh.getEditor()).getTextField();
        tfEsc.setBackground( SlickerColors.COLOR_BG_3 );
        tfEsc.setForeground( SlickerColors.COLOR_FG );
        //Add
        panel.add(factory
				.createLabel("<html><h2>Esaping States Threshold</h2></html>"), "0, 0, 5, 0");
        panel.add(spinEscTh, "0, 1, 1, 1");
        panel.add(factory
				.createLabel("  Gamma"), "2, 1, 3, 1");
        //---------------------------------------------------------------------

        
		//CONFIDENCE------------------------------------------------------------		
		panel.add(factory
				.createLabel("<html><h2>Confidence Interval</h2></html>"), "0, 3, 5, 3");
		cBConfidence = factory.createCheckBox("Compute Confidence Interval", res.isConfidence());
		cBConfidence.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton)actionEvent.getSource();
		        spinK.setEnabled(abstractButton.getModel().isSelected());
			}
		});
		panel.add(cBConfidence, "0, 4, 5, 4");
		//Create confidence K parameter spinner
		int currentK = res.getkConfidence();
        int minK = 0;
        int maxK = Integer.MAX_VALUE;
        int stepK = 1;
        //creating the number spinner model
        SpinnerNumberModel modelK = new SpinnerNumberModel(currentK, minK, maxK, stepK);
        //creating the number spinner
        spinK = new JSpinner(modelK);
        spinK.setEnabled(cBConfidence.isSelected());
        JFormattedTextField tfK =
  	      ((JSpinner.DefaultEditor)spinK.getEditor()).getTextField();
        tfK.setBackground( SlickerColors.COLOR_BG_3 );
        tfK.setForeground( SlickerColors.COLOR_FG );
        panel.add(spinK, "0, 5, 1, 5");
        panel.add(factory
				.createLabel("  K"), "2, 5");
        //---------------------------------------------------------------------
        
        //MDT and SEVERITY-------------------------------------------------------------
        panel.add(factory
				.createLabel("<html><h2>Imprecisions</h2></html>"), "0, 7, 5, 7");
        cBMDT = factory.createCheckBox("Minimal Disconformant Traces (MDT)", res.isMdt());
        cBMDT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton)actionEvent.getSource();
				cBSeverity.setEnabled(abstractButton.getModel().isSelected());
		        spinSeverity.setEnabled(abstractButton.getModel().isSelected());
			}
		});
        panel.add(cBMDT, "0, 8, 5, 8");
        
        
        cBSeverity = factory.createCheckBox("Compute Severity", res.isSeverity());
        cBSeverity.setEnabled(cBMDT.isSelected());
        cBSeverity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton)actionEvent.getSource();
		        spinSeverity.setEnabled(abstractButton.getModel().isSelected());
			}
		});
        panel.add(cBSeverity, "1, 9, 5, 9");
        //Create confidence K parameter spinner
        Double currentSev = res.getSeverityTh();
        Double minSev = new Double(0.00);
        Double maxSev = new Double(Double.MAX_VALUE);
        Double stepSev = new Double(0.01);
        //creating the number spinner model
        SpinnerNumberModel modelSev = new SpinnerNumberModel(currentSev, minSev, maxSev, stepSev);
        //creating the number spinner
        spinSeverity = new JSpinner(modelSev);
        spinSeverity.setEnabled(cBMDT.isSelected() && cBSeverity.isSelected());
        JFormattedTextField tfSev =
  	      ((JSpinner.DefaultEditor)spinSeverity.getEditor()).getTextField();
        tfSev.setBackground( SlickerColors.COLOR_BG_3 );
        tfSev.setForeground( SlickerColors.COLOR_FG );
        panel.add(spinSeverity, "1, 10, 2, 10");
        panel.add(factory
				.createLabel("  Tau"), "3, 10");
        
        //----------------------------------------------------------------------
        
        // PREFIX AUTOMATON ----------------------------------------------------
        panel.add(factory
				.createLabel("<html><h2>Prefix Automaton</h2></html>"), "0, 12, 5, 12");
        cBAutomaton = factory.createCheckBox("Show Prefix Automaton", res.isAutomaton());
        panel.add(cBAutomaton, "0, 13, 5, 13");
        
        //----------------------------------------------------------------------
        
        
     // HEURISTICS ----------------------------------------------------
        panel.add(factory
				.createLabel("<html><h2>Heuristics</h2></html>"), "0, 15, 5, 15");
        cBLazyInv = factory.createCheckBox("Lazy Invisibles", res.isLazyInv());
        panel.add(cBLazyInv, "0, 16, 5, 16");
        cBRandomIndet = factory.createCheckBox("First Indeterminism Solving", res.isRandomIndet());
        panel.add(cBRandomIndet, "0, 17, 5, 17");
        
        //----------------------------------------------------------------------
		
		return panel;
	}
	
	/**
	 * Store the current values of the UI components to the result object.
	 */
	public void setSettings(){
		res.setEscTh((Double) spinEscTh.getValue());
		res.setConfidence(cBConfidence.isSelected());
		res.setkConfidence((Integer) spinK.getValue());
		res.setMdt(cBMDT.isSelected());
		res.setSeverity(cBSeverity.isSelected());
		res.setSeverityTh((Double) spinSeverity.getValue());
		res.setAutomaton(cBAutomaton.isSelected());
		res.setLazyInv(cBLazyInv.isSelected());
		res.setRandomIndet(cBRandomIndet.isSelected());
	}

}
