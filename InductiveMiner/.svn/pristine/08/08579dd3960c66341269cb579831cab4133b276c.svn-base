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

import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.inductiveminer2.withoutlog.MiningParametersWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;
import org.processmining.plugins.inductiveminer2.withoutlog.variants.InductiveMinerWithoutLogVariant;
import org.processmining.plugins.inductiveminer2.withoutlog.variants.MiningParametersIMWithoutLog;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class InductiveMinerWithoutLogDialog extends JPanel {

	private static final long serialVersionUID = 1441839136397807436L;
	public InductiveMinerWithoutLogVariant[] variants = new InductiveMinerWithoutLogVariant[] { //
			new MiningParametersIMWithoutLog() //
	};
	protected JComboBox<InductiveMinerWithoutLogVariant> variantCombobox;

	@SuppressWarnings("unchecked")
	public InductiveMinerWithoutLogDialog(DfgMsd graph) {
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
			//TODO variantCombobox.setSelectedIndex(1);
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

		//doi
		final JLabel doiLabel;
		final JLabel doiValue;
		{
			doiLabel = factory.createLabel("More information");
			doiLabel.setVisible(getVariant().getDoi() != null);
			add(doiLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, doiLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					noiseSlider);
			layout.putConstraint(SpringLayout.EAST, doiLabel, leftColumnWidth, SpringLayout.WEST, this);

			doiValue = factory.createLabel("  " + Objects.toString(getVariant().getDoi(), ""));
			add(doiValue);
			layout.putConstraint(SpringLayout.WEST, doiValue, columnMargin, SpringLayout.EAST, doiLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, doiValue, 0, SpringLayout.VERTICAL_CENTER, doiLabel);
		}

		variantCombobox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InductiveMinerWithoutLogVariant variant = getVariant();

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
				for (InductiveMinerWithoutLogVariant variant : variants) {
					variant.getMiningParameters().setNoiseThreshold((float) (noiseSlider.getValue() / 1000.0));
				}
				noiseValue.setText(String.format("%.2f", getMiningParameters().getNoiseThreshold()));
			}
		});
		for (InductiveMinerWithoutLogVariant variant : variants) {
			variant.getMiningParameters().setNoiseThreshold((float) (noiseSlider.getValue() / 1000.0));
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

	public MiningParametersWithoutLog getMiningParameters() {
		return getVariant().getMiningParameters();
	}

	public InductiveMinerWithoutLogVariant getVariant() {
		return ((InductiveMinerWithoutLogVariant) variantCombobox.getSelectedItem());
	}

}
