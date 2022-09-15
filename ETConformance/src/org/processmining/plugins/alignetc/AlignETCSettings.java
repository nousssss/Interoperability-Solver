package org.processmining.plugins.alignetc;

import info.clearthought.layout.TableLayout;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.processmining.plugins.alignetc.result.AlignETCResult;

import com.fluxicon.slickerbox.colors.SlickerColors;
import com.fluxicon.slickerbox.factory.SlickerFactory;




public class AlignETCSettings {
	
	/** Results object to store the settings */
	AlignETCResult res;
	
	/** Spinner for Escaping Threshold */
	JSpinner spinEscTh;
	
	/**
	 * Main constructor that initialize the default setting values.
	 */
	public AlignETCSettings(AlignETCResult res) {
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
		panel.setLayout(new TableLayout(new double[][] { { 50, 50, 50, 250},
				{ 30, 30, 30, 30, 30} }));
		
		
		//ESCAPING THRESHOLD SPINNER-------------------------------------------
        Double currentEsc = res.escTh;
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
				.createLabel("<html><h2>Escaping States Threshold</h2></html>"), "0, 0, 3, 0");
        panel.add(spinEscTh, "0, 1, 1, 1");
        panel.add(factory
				.createLabel("  Gamma"), "2, 1, 3, 1");
        //---------------------------------------------------------------------
        
    	return panel;
	}
	
	/**
	 * Store the current values of the UI components to the result object.
	 */
	public void setSettings(){
		res.escTh = (Double) spinEscTh.getValue();
	}

}
