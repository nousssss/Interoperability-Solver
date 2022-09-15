package org.processmining.plugins.multietc.sett;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class MultiETCSettingsUI extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6089536335693795196L;
	
	
	private final static String ALIGN_1 = "1-Align Precision";
	private final static String ALIGN_ALL = "All-Align Precision";
	private final static String ALIGN_REPRE = "Representative-Align Precision";
	private final static String ETC = "ETC Precision (no invisible/duplicates allowed)";
	
	private final ButtonGroup rdbtngpRepresentation = new ButtonGroup();
	
	private JRadioButton rdbtnSequence;
	private JRadioButton rdbtnMultiset;
	
	@SuppressWarnings("rawtypes")
	private JList algList;

	/**
	 * Create the panel.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MultiETCSettingsUI() {
		setLayout(null);
		
		SlickerFactory factory = SlickerFactory.instance();
		
		Box representationBox = Box.createHorizontalBox();
		representationBox.setBounds(47, 115, 226, 23);
		add(representationBox);
		
		rdbtnSequence = factory.createRadioButton("Sequence");
		rdbtnSequence.setText("Ordered");
		representationBox.add(rdbtnSequence);
		rdbtnSequence.setSelected(true);
		rdbtngpRepresentation.add(rdbtnSequence);
		
		rdbtnMultiset = factory.createRadioButton("MultiSet");
		rdbtnMultiset.setText("Unordered");
		representationBox.add(rdbtnMultiset);
		rdbtngpRepresentation.add(rdbtnMultiset);
		
		algList = new JList();
		algList.setModel(new AbstractListModel() {
			private static final long serialVersionUID = 5326445163613960491L;
			String[] values = new String[] {ALIGN_1, ALIGN_REPRE, ALIGN_ALL, ETC};
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				return values[index];
			}
		});
		algList.setSelectedIndex(0);
		algList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		algList.setBounds(47, 221, 591, 113);
		add(algList);
		
		JLabel lblAlgorithm = new JLabel("ALGORITHM");
		lblAlgorithm.setBounds(47, 187, 132, 23);
		add(lblAlgorithm);
		
		JLabel lblRepresentation = factory.createLabel("REPRESENTATION");
		lblRepresentation.setBounds(47, 81, 141, 33);
		add(lblRepresentation);

	}
	
	public MultiETCSettings getSettings(UIPluginContext context){
		InteractionResult result = context.showWizard("Precision Checking Settings", true, true, this);
		switch (result) {
			case CANCEL :
				return null;
			case FINISHED :
				MultiETCSettings sett = new MultiETCSettings();
				setSettings(sett);//Get and store the settings
				return sett;
			default :
				return null;
		}
	}

	private void setSettings(MultiETCSettings sett) {
		//Get the Representation
		if(this.rdbtnSequence.isSelected()) sett.put(MultiETCSettings.REPRESENTATION, MultiETCSettings.Representation.ORDERED);
		else if(this.rdbtnMultiset.isSelected()) sett.put(MultiETCSettings.REPRESENTATION, MultiETCSettings.Representation.UNORDERED);
		
		//Get the Algorithm
		if( algList.getSelectedValue() == MultiETCSettingsUI.ETC) sett.put(MultiETCSettings.ALGORITHM, MultiETCSettings.Algorithm.ETC);
		else if( algList.getSelectedValue() == MultiETCSettingsUI.ALIGN_1) sett.put(MultiETCSettings.ALGORITHM, MultiETCSettings.Algorithm.ALIGN_1);
		else if( algList.getSelectedValue() == MultiETCSettingsUI.ALIGN_REPRE) sett.put(MultiETCSettings.ALGORITHM, MultiETCSettings.Algorithm.ALIGN_REPRE);
		else if( algList.getSelectedValue() == MultiETCSettingsUI.ALIGN_ALL) sett.put(MultiETCSettings.ALGORITHM, MultiETCSettings.Algorithm.ALIGN_ALL);
	}
}
