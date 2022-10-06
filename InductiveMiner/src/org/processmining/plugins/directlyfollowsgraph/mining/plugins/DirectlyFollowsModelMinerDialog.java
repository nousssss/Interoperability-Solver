package org.processmining.plugins.directlyfollowsgraph.mining.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.ClassifierChooser;
import org.processmining.plugins.InductiveMiner.mining.logs.LifeCycleClassifier;
import org.processmining.plugins.directlyfollowsgraph.mining.DFMMiningParameters;
import org.processmining.plugins.directlyfollowsgraph.mining.DFMMiningParametersAbstract;
import org.processmining.plugins.directlyfollowsgraph.mining.variants.DFMMiningParametersDefault;
import org.processmining.plugins.inductiveminer2.helperclasses.XLifeCycleClassifierIgnore;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class DirectlyFollowsModelMinerDialog extends JPanel {
	private static final long serialVersionUID = 5093343984229184234L;

	private static final String doi = null;

	private DFMMiningParametersAbstract miningParameters = new DFMMiningParametersDefault();

	public DirectlyFollowsModelMinerDialog(XLog xLog) {
		SlickerFactory factory = SlickerFactory.instance();

		int leftColumnWidth = 200;
		int columnMargin = 20;
		int rowHeight = 40;

		//setLayout(new GridBagLayout());
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		//noise threshold
		final JLabel noiseLabel;
		final JSlider noiseSlider;
		final JLabel noiseValue;
		final JLabel noiseExplanation;
		{
			noiseLabel = factory.createLabel("Noise threshold");
			add(noiseLabel);
			layout.putConstraint(SpringLayout.NORTH, noiseLabel, 5, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.EAST, noiseLabel, leftColumnWidth, SpringLayout.WEST, this);

			noiseSlider = factory.createSlider(SwingConstants.HORIZONTAL);
			noiseSlider.setMinimum(0);
			noiseSlider.setMaximum(1000);
			noiseSlider.setValue((int) (getMiningParameters().getNoiseThreshold() * 1000));
			add(noiseSlider);
			layout.putConstraint(SpringLayout.WEST, noiseSlider, columnMargin, SpringLayout.EAST, noiseLabel);
			layout.putConstraint(SpringLayout.EAST, noiseSlider, -50, SpringLayout.EAST, this);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, noiseSlider, 0, SpringLayout.VERTICAL_CENTER,
					noiseLabel);

			noiseValue = factory.createLabel(String.format("%.2f", getMiningParameters().getNoiseThreshold()));
			add(noiseValue);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, noiseValue, 0, SpringLayout.VERTICAL_CENTER, noiseLabel);
			layout.putConstraint(SpringLayout.WEST, noiseValue, columnMargin, SpringLayout.EAST, noiseSlider);

			noiseExplanation = factory.createLabel("  If set to 1.00, perfect log fitness is guaranteed.");
			add(noiseExplanation);
			noiseExplanation.setVisible(false);
			layout.putConstraint(SpringLayout.WEST, noiseExplanation, columnMargin, SpringLayout.EAST, noiseLabel);
			layout.putConstraint(SpringLayout.NORTH, noiseExplanation, rowHeight / 2, SpringLayout.VERTICAL_CENTER,
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

			classifiers = new ClassifierChooser(xLog);
			add(classifiers);
			layout.putConstraint(SpringLayout.WEST, classifiers, columnMargin, SpringLayout.EAST, classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifiers, 0, SpringLayout.VERTICAL_CENTER,
					classifierLabel);
		}

		//life cycle
		final JCheckBox useLifeCycle;
		{
			JLabel useLifeCycleLabel = factory.createLabel("Life cycle");
			add(useLifeCycleLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, useLifeCycleLabel, rowHeight,
					SpringLayout.VERTICAL_CENTER, classifiers);
			layout.putConstraint(SpringLayout.EAST, useLifeCycleLabel, leftColumnWidth, SpringLayout.WEST, this);

			useLifeCycle = factory.createCheckBox("use life cycle information (\"start\", \"complete\")", true);
			add(useLifeCycle);
			layout.putConstraint(SpringLayout.WEST, useLifeCycle, columnMargin, SpringLayout.EAST, useLifeCycleLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, useLifeCycle, 0, SpringLayout.VERTICAL_CENTER,
					useLifeCycleLabel);
		}

		//doi
		final JLabel doiLabel;
		final JLabel doiValue;
		{
			doiLabel = factory.createLabel("More information");
			doiLabel.setVisible(doi != null);
			add(doiLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, doiLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					classifiers);
			layout.putConstraint(SpringLayout.EAST, doiLabel, leftColumnWidth, SpringLayout.WEST, this);

			doiValue = factory.createLabel("  " + Objects.toString(doi, ""));
			add(doiValue);
			layout.putConstraint(SpringLayout.WEST, doiValue, columnMargin, SpringLayout.EAST, doiLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, doiValue, 0, SpringLayout.VERTICAL_CENTER, doiLabel);
		}

		//set up the controller
		noiseSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				miningParameters.setNoiseThreshold((float) (noiseSlider.getValue() / 1000.0));
				noiseValue.setText(String.format("%.2f", getMiningParameters().getNoiseThreshold()));
			}
		});

		classifiers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				miningParameters.setClassifier(classifiers.getSelectedClassifier());
			}
		});
		miningParameters.setClassifier(classifiers.getSelectedClassifier());

		useLifeCycle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean use = useLifeCycle.isSelected();
				noiseExplanation.setVisible(!use);
				if (use) {
					miningParameters.setLifeCycleClassifier(new LifeCycleClassifier());
				} else {
					miningParameters.setLifeCycleClassifier(new XLifeCycleClassifierIgnore());
				}
			}
		});
	}

	public DFMMiningParameters getMiningParameters() {
		return miningParameters;
	}

}
