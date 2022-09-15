package org.processmining.plugins.petrinet.configurable.ui.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.deckfour.uitopia.ui.components.ImageLozengeButton;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeature;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.InvalidConfigurationException;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ParameterizedArc;
import org.processmining.models.graphbased.directed.petrinet.configurable.elements.ParameterizedPlaceMarking;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableParameterInteger;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ParameterizedIntegerFeature;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.plugins.petrinet.configurable.ui.ConfigurableFeature_UI;
import org.processmining.plugins.petrinet.configurable.ui.widgets.Structured_UI;
import org.processmining.plugins.petrinet.configurable.ui.widgets.UIUtils;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * UI to represent a {@link ParameterizedIntegerFeature}
 * 
 * @author dfahland
 *
 * @param <T> node type of the {@link ParameterizedIntegerFeature}
 */
public abstract class ParameterizedIntegerFeature_UI<T extends DirectedGraphElement> extends ConfigurableFeature_UI<T, Integer> {

	public ParameterizedIntegerFeature_UI(JComponent root, ParameterizedIntegerFeature<T> feature, int line_height) {
		super(root, feature);
		initialize(feature.getId(), line_height);
	}

	private static final long serialVersionUID = 1L;
	

	protected ProMTextField expression;
	protected ProMTextField minValue;
	protected ProMTextField maxValue;
	
	private List<IntegerParameter_UI> parameters = new LinkedList<IntegerParameter_UI>();
	private JPanel parametersPanel;
	private ImageLozengeButton addParameterButton;
	
	protected void initializeFeatureOptionsPanel(final JPanel optionsPanel) {
		SlickerFactory f = SlickerFactory.instance();
		
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		
		JPanel optionsMainPanel = new JPanel();
		optionsMainPanel.setOpaque(false);
		optionsMainPanel.setLayout(new BoxLayout(optionsMainPanel, BoxLayout.X_AXIS));
		optionsMainPanel.add(f.createLabel("expression"));
		optionsMainPanel.add(Box.createHorizontalStrut(5));
		expression = new ProMTextField();
		expression.setMinimumSize(new Dimension(200, 30));
		expression.setPreferredSize(new Dimension(200, 30));
		expression.setMaximumSize(new Dimension(200, 30));
		optionsMainPanel.add(expression);
		optionsMainPanel.add(Box.createHorizontalGlue());

		optionsMainPanel.add(f.createLabel("min"));
		optionsMainPanel.add(Box.createHorizontalStrut(5));
			
		minValue = new ProMTextField();
		minValue.setMinimumSize(new Dimension(60, 30));
		minValue.setPreferredSize(new Dimension(60, 30));
		minValue.setMaximumSize(new Dimension(60, 30));
		optionsMainPanel.add(minValue);
		optionsMainPanel.add(Box.createHorizontalStrut(10));

		optionsMainPanel.add(f.createLabel("max"));
		optionsMainPanel.add(Box.createHorizontalStrut(5));
		
		maxValue = new ProMTextField();
		maxValue.setMinimumSize(new Dimension(60, 30));
		maxValue.setPreferredSize(new Dimension(60, 30));
		maxValue.setMaximumSize(new Dimension(60, 30));
		optionsMainPanel.add(maxValue);
		optionsMainPanel.add(Box.createHorizontalStrut(5+30));

		
		optionsPanel.add(optionsMainPanel);
		
		parametersPanel = new JPanel();
		parametersPanel.setOpaque(false);
		parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
		
		optionsPanel.add(parametersPanel);
		
		JPanel addPanel = new JPanel();
		addPanel.setOpaque(false);
		addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.X_AXIS));
		addParameterButton = new ImageLozengeButton(UIUtils.plusSign, "add parameter", new Color(160,160,160), new Color(130,170,130), 0);
		addParameterButton.setLabelColor(Color.DARK_GRAY);
		addParameterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ConfigurableParameter<Integer> param = new ConfigurableParameterInteger("name", 0, 10, 1);
					ParameterizedIntegerFeature_UI.this.addParameter(param);
					ParameterizedIntegerFeature_UI.this.resizeToContents();
				} catch (InvalidConfigurationException e1) {
				}
			}
		});
		addPanel.add(Box.createHorizontalGlue());
		addPanel.add(addParameterButton);
		addPanel.add(Box.createHorizontalGlue());
		
		optionsPanel.add(Box.createVerticalGlue());
		optionsPanel.add(addPanel);
	}
	
	public void resizeToContents() {
		
		int height = (parameters.size()+2)*38;
		
		getPanel().setMinimumSize(new Dimension(300, height));
		getPanel().setPreferredSize(new Dimension(680, height));
		getPanel().setMaximumSize(new Dimension(1000, height));
		
		getRoot().revalidate();
	}

	public void setValues(ConfigurableFeature<?,?> input) {
		if (input instanceof ParameterizedIntegerFeature<?>) {
			ParameterizedIntegerFeature<?> feature = (ParameterizedIntegerFeature<?>)input;
			expression.setText(feature.getExpression());
			minValue.setText(feature.getIntervalMin().toString());
			maxValue.setText(feature.getIntervalMax().toString());
			
			parameters.clear();
			parametersPanel.removeAll();
			
			for (ConfigurableParameter<Integer> param : feature.getInputParametersByName()) {
				addParameter(param);
			}

			resizeToContents();
		}
	}
	
	private void addParameter(ConfigurableParameter<Integer> param) {
		IntegerParameter_UI param_ui = new IntegerParameter_UI(param);
		parameters.add(param_ui);
		parametersPanel.add(param_ui.getPanel());
	}
	
	private void removeParameter(IntegerParameter_UI param_ui) {
		parameters.remove(param_ui);
		parametersPanel.remove(param_ui.getPanel());
		resizeToContents();
	}
	
	protected List<ConfigurableParameter<Integer>> getInputParameters() throws Exception {
		List<ConfigurableParameter<Integer>> inputParameters = new LinkedList<ConfigurableParameter<Integer>>();
		for (IntegerParameter_UI param_ui : parameters) {
			inputParameters.add(param_ui.getConfigured());
		}
		return inputParameters;
	}
	
	public static class ParameterizedPlaceMarkingFeature_UI extends ParameterizedIntegerFeature_UI<Place> {
		public ParameterizedPlaceMarkingFeature_UI(JComponent root, ParameterizedPlaceMarking feature, int line_height) {
			super(root, feature, line_height);
		}

		public ParameterizedPlaceMarking getConfigured() throws InvalidConfigurationException {
			try { 
				return new ParameterizedPlaceMarking(getConfiguredElement(), Integer.parseInt(minValue.getText()), Integer.parseInt(maxValue.getText()), expression.getText(), getInputParameters());
			} catch (Exception e) {
				throw new InvalidConfigurationException(e);
			}
		}

	}
	
	public static class ParameterizedArcWeightFeature_UI extends ParameterizedIntegerFeature_UI<Arc> {
		public ParameterizedArcWeightFeature_UI(JComponent root, ParameterizedArc feature, int line_height) {
			super(root, feature, line_height);
		}
		public ParameterizedArc getConfigured() throws InvalidConfigurationException {
			try { 
				return new ParameterizedArc(getConfiguredElement(), Integer.parseInt(minValue.getText()), Integer.parseInt(maxValue.getText()), expression.getText(), getInputParameters());
			} catch (Exception e) {
				throw new InvalidConfigurationException(e);
			}

		}
	}

	public class IntegerParameter_UI implements Structured_UI<ConfigurableParameter<Integer>, ConfigurableParameter<Integer>>, FocusListener {
		private JPanel optionsPanel;
		
		private Color BACKGROUND_INACTIVE = new Color(160, 160, 160);
		private Color BACKGROUND_ACTIVE = new Color(160, 170, 160);
		
		public IntegerParameter_UI(ConfigurableParameter<Integer> param) {
			initialize();
			setValues(param);
			installHighlighter(optionsPanel, this);
		}
		
		protected ProMTextField parameterName;
		protected NiceIntegerSlider slider;
		protected ProMTextField minValue;
		protected ProMTextField maxValue;
		private JButton removeButton;
		private JPanel removeButtonReplacement;
		
		private void initialize() {
			SlickerFactory f = SlickerFactory.instance();
			
			optionsPanel = f.createRoundedPanel(10,BACKGROUND_INACTIVE);
			optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
			optionsPanel.setOpaque(false);
			optionsPanel.add(f.createLabel("parameter"));
			optionsPanel.add(Box.createHorizontalStrut(5));
			parameterName = new ProMTextField();
			parameterName.setMinimumSize(new Dimension(60, 30));
			parameterName.setPreferredSize(new Dimension(60, 30));
			parameterName.setMaximumSize(new Dimension(60, 30));
			optionsPanel.add(parameterName);
			optionsPanel.add(Box.createHorizontalGlue());
			
			minValue = new ProMTextField();
			minValue.addFocusListener(this);
			minValue.setMinimumSize(new Dimension(40, 30));
			minValue.setPreferredSize(new Dimension(40, 30));
			minValue.setMaximumSize(new Dimension(40, 30));
			optionsPanel.add(minValue);
			optionsPanel.add(Box.createHorizontalStrut(10));

			
			slider = f.createNiceIntegerSlider("value", 0, 100, 0, Orientation.HORIZONTAL);
			optionsPanel.add(slider);
			optionsPanel.add(Box.createHorizontalStrut(10));
			
			maxValue = new ProMTextField();
			maxValue.addFocusListener(this);
			maxValue.setMinimumSize(new Dimension(40, 30));
			maxValue.setPreferredSize(new Dimension(40, 30));
			maxValue.setMaximumSize(new Dimension(40, 30));
			optionsPanel.add(maxValue);
			optionsPanel.add(Box.createHorizontalStrut(5));
			
			removeButton = UIUtils.createCrossSignButton();
			removeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					IntegerParameter_UI.this.removeFromParent();
				}
			});
			removeButton.setVisible(false);
			removeButtonReplacement = new JPanel();
			removeButtonReplacement.setOpaque(false);
			removeButtonReplacement.setMinimumSize(new Dimension(30, 30));
			removeButtonReplacement.setPreferredSize(new Dimension(30, 30));
			removeButtonReplacement.setMaximumSize(new Dimension(30, 30));
			removeButtonReplacement.setVisible(true);
			optionsPanel.add(removeButton);
			optionsPanel.add(removeButtonReplacement);
		}
		
		public void setValues(ConfigurableParameter<Integer> param) {
			parameterName.setText(param.getId());
			minValue.setText(param.getIntervalMin().toString());
			maxValue.setText(param.getIntervalMax().toString());
			updateSliderFromMinMax();
			slider.setValue(param.getValue());
		}
		
		public ConfigurableParameter<Integer> getConfigured() throws Exception {
			return new ConfigurableParameterInteger(parameterName.getText(), slider.getSlider().getMinimum(), slider.getSlider().getMaximum(), slider.getValue());
		}
		
		public JPanel getPanel() {
			return optionsPanel;
		}
		
		public void focusGained(FocusEvent e) {
			updateSliderFromMinMax();
		}
		
		public void focusLost(FocusEvent e) {
			updateSliderFromMinMax();
		}
		
		protected void updateSliderFromMinMax() {
			try {
				Integer minVal = Integer.parseInt(minValue.getText());
				Integer maxVal = Integer.parseInt(maxValue.getText());
				slider.getSlider().setMinimum(minVal);
				slider.getSlider().setMaximum(maxVal);
				
				if (slider.getValue() < minVal) slider.setValue(minVal);
				if (slider.getValue() > maxVal) slider.setValue(maxVal);
			} catch (NullPointerException e) {
			}
		}
		
		private void removeFromParent() {
			ParameterizedIntegerFeature_UI.this.removeParameter(this);
		}
		
		private void installHighlighter(final Component component, final IntegerParameter_UI target) {
			component.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(final MouseEvent arg0) { /* ignore */
				}

				@Override
				public void mouseEntered(final MouseEvent arg0) {
					target.removeButton.setVisible(true);
					target.removeButtonReplacement.setVisible(false);
					target.getPanel().setBackground(BACKGROUND_ACTIVE);
					target.getPanel().repaint();
				}

				@Override
				public void mouseExited(final MouseEvent arg0) {
					target.removeButton.setVisible(false);
					target.removeButtonReplacement.setVisible(true);
					target.getPanel().setBackground(BACKGROUND_INACTIVE);
					target.getPanel().repaint();
				}

				@Override
				public void mousePressed(final MouseEvent arg0) { /* ignore */
				}

				@Override
				public void mouseReleased(final MouseEvent arg0) { /* ignore */
				}
			});
			if (component instanceof Container) {
				for (final Component child : ((Container) component).getComponents()) {
					installHighlighter(child, target);
				}
			}
		}
		
	}
	
}
