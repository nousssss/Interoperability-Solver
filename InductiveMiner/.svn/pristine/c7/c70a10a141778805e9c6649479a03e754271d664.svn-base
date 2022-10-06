package org.processmining.plugins.inductiveminer2.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.ClassifierChooser;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.inductiveminer2.mining.MiningParameters;
import org.processmining.plugins.inductiveminer2.variants.InductiveMinerVariant;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIM;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequent;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequentLifeCycle;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequentPartialTraces;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequentPartialTracesAli;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMLifeCycle;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMPartialTraces;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class InductiveMinerDialog extends JPanel {

	private static final long serialVersionUID = 1441839136397807436L;
	public InductiveMinerVariant[] variants = new InductiveMinerVariant[] { //
			new MiningParametersIM(), //
			new MiningParametersIMInfrequent(), //
			new MiningParametersIMLifeCycle(), //
			new MiningParametersIMInfrequentLifeCycle(), //
			new MiningParametersIMPartialTraces(), //
			new MiningParametersIMInfrequentPartialTraces(), //
			new MiningParametersIMInfrequentPartialTracesAli() };
	protected JComboBox<InductiveMinerVariant> variantCombobox;

	@SuppressWarnings("unchecked")
	public InductiveMinerDialog(XLog log) {
		SlickerFactory factory = SlickerFactory.instance();

		int leftColumnWidth = 200;
		int columnMargin = 20;
		int rowHeight = 40;

		//setLayout(new GridBagLayout());
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		//variant
		JLabel variantLabel;
		{
			variantLabel = factory.createLabel("Variant");
			add(variantLabel);
			layout.putConstraint(SpringLayout.NORTH, variantLabel, 5, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.EAST, variantLabel, leftColumnWidth, SpringLayout.WEST, this);

			variantCombobox = factory.createComboBox(variants);
			variantCombobox.setSelectedIndex(1);
			variantCombobox.setPreferredSize(variantCombobox.getMaximumSize());
			add(variantCombobox);
			layout.putConstraint(SpringLayout.WEST, variantCombobox, columnMargin, SpringLayout.EAST, variantLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, variantCombobox, 0, SpringLayout.VERTICAL_CENTER,
					variantLabel);
		}

		//noise threshold
		final JLabel noiseLabel;
		final JSlider noiseSlider;
		final JLabel noiseValue;
		final JLabel noiseExplanation;
		final JLabel fitnessExplanation;
		{
			noiseLabel = factory.createLabel("Noise threshold");
			noiseLabel.setVisible(getVariant().hasNoise());
			add(noiseLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, noiseLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					variantLabel);
			layout.putConstraint(SpringLayout.EAST, noiseLabel, leftColumnWidth, SpringLayout.WEST, this);

			noiseSlider = factory.createSlider(SwingConstants.HORIZONTAL);
			noiseSlider.setMinimum(0);
			noiseSlider.setMaximum(1000);
			noiseSlider.setValue((int) (getMiningParameters().getNoiseThreshold() * 1000));
			noiseSlider.setVisible(getVariant().hasNoise());
			add(noiseSlider);
			layout.putConstraint(SpringLayout.WEST, noiseSlider, columnMargin, SpringLayout.EAST, noiseLabel);
			layout.putConstraint(SpringLayout.EAST, noiseSlider, -50, SpringLayout.EAST, this);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, noiseSlider, 0, SpringLayout.VERTICAL_CENTER,
					noiseLabel);

			noiseValue = factory.createLabel(String.format("%.2f", getMiningParameters().getNoiseThreshold()));
			noiseValue.setVisible(getVariant().hasNoise());
			add(noiseValue);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, noiseValue, 0, SpringLayout.VERTICAL_CENTER, noiseLabel);
			layout.putConstraint(SpringLayout.WEST, noiseValue, columnMargin, SpringLayout.EAST, noiseSlider);

			noiseExplanation = factory.createLabel("  If set to 0.00, perfect log fitness is guaranteed.");
			noiseExplanation.setVisible(getVariant().hasNoise() && getVariant().noNoiseImpliesFitness());
			add(noiseExplanation);
			layout.putConstraint(SpringLayout.WEST, noiseExplanation, columnMargin, SpringLayout.EAST, noiseLabel);
			layout.putConstraint(SpringLayout.NORTH, noiseExplanation, rowHeight / 2, SpringLayout.VERTICAL_CENTER,
					noiseLabel);

			fitnessExplanation = factory.createLabel("  Perfect log fitness is guaranteed.");
			fitnessExplanation.setVisible(getVariant().hasFitness());
			add(fitnessExplanation);
			layout.putConstraint(SpringLayout.WEST, fitnessExplanation, columnMargin, SpringLayout.EAST, noiseLabel);
			layout.putConstraint(SpringLayout.NORTH, fitnessExplanation, rowHeight / 2, SpringLayout.VERTICAL_CENTER,
					noiseLabel);
		}

		//classifiers
		final ClassifierChooser classifiers;
		{
			JLabel classifierLabel = factory.createLabel("Event classifier");
			add(classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifierLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					noiseExplanation);
			layout.putConstraint(SpringLayout.EAST, classifierLabel, leftColumnWidth, SpringLayout.WEST, this);

			classifiers = new ClassifierChooser(log);
			add(classifiers);
			classifiers.setPreferredSize(variantCombobox.getPreferredSize());
			layout.putConstraint(SpringLayout.WEST, classifiers, columnMargin, SpringLayout.EAST, classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifiers, 0, SpringLayout.VERTICAL_CENTER,
					classifierLabel);
		}

		//doi
		final JLabel doiLabel;
		final JLabel doiValue;
		{
			doiLabel = factory.createLabel("More information");
			doiLabel.setVisible(getVariant().getDoi() != null);
			add(doiLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, doiLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					classifiers);
			layout.putConstraint(SpringLayout.EAST, doiLabel, leftColumnWidth, SpringLayout.WEST, this);

			doiValue = factory.createLabel("  " + Objects.toString(getVariant().getDoi(), ""));
			add(doiValue);
			layout.putConstraint(SpringLayout.WEST, doiValue, columnMargin, SpringLayout.EAST, doiLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, doiValue, 0, SpringLayout.VERTICAL_CENTER, doiLabel);
		}

		variantCombobox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InductiveMinerVariant variant = getVariant();

				noiseValue.setText(String.format("%.2f", getMiningParameters().getNoiseThreshold()));
				noiseValue.setVisible(variant.hasNoise());
				noiseLabel.setVisible(variant.hasNoise());
				noiseSlider.setVisible(variant.hasNoise());
				noiseExplanation.setVisible(variant.hasNoise() && variant.noNoiseImpliesFitness());
				fitnessExplanation.setVisible(variant.hasFitness());

				if (variant.getDoi() != null) {
					doiValue.setText("  " + variant.getDoi());
				}
				doiLabel.setVisible(variant.getDoi() != null);
				doiValue.setVisible(variant.getDoi() != null);
			}
		});

		noiseSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				for (InductiveMinerVariant variant : variants) {
					variant.getMiningParameters().setNoiseThreshold((float) (noiseSlider.getValue() / 1000.0));
				}
				noiseValue.setText(String.format("%.2f", getMiningParameters().getNoiseThreshold()));
			}
		});
		for (InductiveMinerVariant variant : variants) {
			variant.getMiningParameters().setNoiseThreshold((float) (noiseSlider.getValue() / 1000.0));
		}

		classifiers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (InductiveMinerVariant variant : variants) {
					variant.getMiningParameters().setClassifier(classifiers.getSelectedClassifier());
				}
			}
		});
		for (InductiveMinerVariant variant : variants) {
			variant.getMiningParameters().setClassifier(classifiers.getSelectedClassifier());
		}

		doiValue.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String doi = getVariant().getDoi();
				if (doi != null) {
					IMMiningDialog.openWebPage(doi);
				}
			}
		});
	}

	public MiningParameters getMiningParameters() {
		return getVariant().getMiningParameters();
	}

	public InductiveMinerVariant getVariant() {
		return ((InductiveMinerVariant) variantCombobox.getSelectedItem());
	}

}
