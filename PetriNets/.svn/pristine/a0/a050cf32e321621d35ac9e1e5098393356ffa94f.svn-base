package org.processmining.plugins.petrinet.configurable.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureFactory;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurationUtils;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.InhibitorArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.ResetArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.petrinet.configurable.ui.widgets.linewizard.LineWizardAbstractPage;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * Line wizard page to create a new configurable feature for a feature group.
 * @author dfahland
 *
 */
public class AddFeatureWizardPage extends LineWizardAbstractPage {

	private NetElement[] netElements;
	private ConfigurableFeatureGroup_UI parent;
	
	public AddFeatureWizardPage(ConfigurableFeatureGroup_UI parent) {
		super(parent.getRoot());
		this.parent = parent;
		this.netElements = parent.getNetElements();
		initialize();
	}

	private JComboBox configuredElement;
	private JComboBox parameterType; 
	private JCheckBox parameterized;
	
	public void initializePageContents(JPanel page) {
		SlickerFactory f = SlickerFactory.instance();
		
		configuredElement = f.createComboBox(netElements);
		configuredElement.setMinimumSize(new Dimension(200, 30));
		configuredElement.setPreferredSize(new Dimension(300, 30));
		configuredElement.setMaximumSize(new Dimension(400, 30));
		configuredElement.setSelectedIndex(0);
		
		parameterType = f.createComboBox(new Object[] { 1, 2, 3 });
		parameterType.setMinimumSize(new Dimension(120, 30));
		parameterType.setPreferredSize(new Dimension(120, 30));
		parameterType.setMaximumSize(new Dimension(120, 30));

		parameterized = f.createCheckBox("parameterized", false);
		
		configuredElement.addActionListener(new ElementChoiceListener());
		parameterType.addActionListener(new ConfigurationParameterListener());
		updateParameterTypesForNetElement((NetElement)configuredElement.getSelectedItem());
		
		page.add(f.createLabel("configure"));
		page.add(configuredElement);
		page.add(Box.createHorizontalStrut(5));
		page.add(f.createLabel("on"));
		page.add(parameterType);
		page.add(Box.createHorizontalStrut(5));
		page.add(parameterized);
	}

	public void onNextButtonPress() {
		ConfigurableFeature_UI<?, ?> feat_ui = getValue();
		parent.addFeature(feat_ui);
	}

	public ConfigurableFeature_UI<?, ?> getValue() {
		DirectedGraphElement el = ((NetElement)configuredElement.getSelectedItem()).getElement();
		String featureName = (String)parameterType.getSelectedItem();
		boolean parameterized = this.parameterized.isSelected();
		
		ConfigurableFeature<?,?> feature = ConfigurableFeatureFactory.createDefaultFeature(el, featureName, parameterized);
		ConfigurableFeature_UI<?, ?> feat_ui = ConfigurableFeature_UI_Factory.getUIforFeature(getRoot(), feature);
		feat_ui.setIdEditable(true);
		
		return feat_ui;
	}
	
	private void updateParameterTypesForNetElement(NetElement choice) {
		String featureOptions[] = null;
		if (choice.getElement() instanceof Transition)
			featureOptions = ConfigurableFeatureFactory.PARAM_OPTIONS_TRANSITION;
		else if (choice.getElement() instanceof Place)
			featureOptions = ConfigurableFeatureFactory.PARAM_OPTIONS_PLACE;
		else if (choice.getElement() instanceof PetrinetEdge<?, ?>) {
			if (choice.getElement() instanceof Arc)
				featureOptions = ConfigurableFeatureFactory.PARAM_OPTIONS_ARC;
			else
				featureOptions = ConfigurableFeatureFactory.PARAM_OPTIONS_RIARC;
		} else
			featureOptions = new String[] {"unknown"};

		parameterType.removeAllItems();
		for (String option : featureOptions) {
			parameterType.addItem(option);
		}
		updateParameterizedFromType((String)parameterType.getSelectedItem());
	}
	
	private void updateParameterizedFromType(String choice) {
		if (choice == null || choice.equals(ConfigurableFeatureFactory.PARAM_FEATURE_PRESENCE)) {
			parameterized.setEnabled(false);
			parameterized.setSelected(false);
			parameterized.setVisible(false);
		} else {
			parameterized.setEnabled(true);
			parameterized.setVisible(true);
		}
	}

	
	private class ElementChoiceListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			NetElement choice = (NetElement)AddFeatureWizardPage.this.configuredElement.getSelectedItem();
			updateParameterTypesForNetElement(choice);
		}
	}
	
	private class ConfigurationParameterListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String choice = (String)AddFeatureWizardPage.this.parameterType.getSelectedItem();
			updateParameterizedFromType(choice);
		}
	}
	
	public static class NetElement implements Comparable<NetElement> {
		private DirectedGraphElement element;
		private String name;
		private String type;
		
		public NetElement(DirectedGraphElement el, String name) {
			this.element = el;
			
			if (element instanceof Transition) {
				type = "T";
			} else if (element instanceof Place) {
				type = "P";
			} else if (element instanceof Arc) {
				type = "arc";
			} else if (element instanceof ResetArc) {
				type = "reset";
			} else if (element instanceof InhibitorArc) {
				type = "inhibit";
			} else {
				type = element.getClass().toString();
			}
			
			this.name = name;
		}
		
		public String toString() {
			return type+" "+name;
		}
		
		public DirectedGraphElement getElement() {
			return element;
		}

		public int compareTo(NetElement o) {
			return this.name.compareTo(o.name);
		}
		
		/**
		 * @return a list of all net elements of the given graph
		 */
		public static NetElement[] getNetElements(PetrinetGraph net) {
			List<NetElement> trans = new LinkedList<NetElement>();
			for (Transition t : net.getTransitions()) {
				trans.add(new NetElement(t, ConfigurationUtils.generateElementIDforFeature(t)));
			}
			Collections.sort(trans);
			
			List<NetElement> places = new LinkedList<NetElement>();
			for (Place p : net.getPlaces()) {
				places.add(new NetElement(p, ConfigurationUtils.generateElementIDforFeature(p)));
			}
			Collections.sort(places);
			
			List<NetElement> edges = new LinkedList<NetElement>();
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getEdges()) {
				edges.add(new NetElement(edge, ConfigurationUtils.generateElementIDforFeature(edge)));
			}
			Collections.sort(edges);
			
			trans.addAll(places);
			trans.addAll(edges);
			
			return trans.toArray(new NetElement[trans.size()]);
		}
	}
	
}
