package org.processmining.plugins.ghzbue;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.models.ghzbue.GhzModel;

import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

/**
 * Parameter dialog for the conversion from workshop model to workshop graph.
 * 
 * @author hverbeek
 * 
 */

public class GhzConversionDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4721868992908620112L;

	/**
	 * Parameter dialog for converting the given workshop model to a workflow
	 * graph.
	 * 
	 * @param model
	 *            The given workshop model.
	 * @param parameters
	 *            The parameters which will be used for the conversion.
	 */
	public GhzConversionDialog(GhzModel model, final GhzConversionConfiguration configurations) {
		/*
		 * Get a layout containing a single column and two rows, where the top
		 * row height equals 30.
		 */
		double size[][] = { { TableLayoutConstants.FILL }, { 30, TableLayoutConstants.FILL } };
		setLayout(new TableLayout(size));

		/*
		 * Put a meaningful text in the top row.
		 */
		add(SlickerFactory.instance().createLabel("<html><h2>Select conversion parameters</h2>"), "0, 0");

		/*
		 * Put a slider in the bottom row.
		 */
		final NiceSlider slider = SlickerFactory.instance()
				.createNiceIntegerSlider("Select cardinality threshold", model.getMinVal(),
						model.getMaxVal(), model.getMinVal(), Orientation.HORIZONTAL);
		/*
		 * Create a change listener on this slider that changes the parameters
		 * accordingly.
		 */
		ChangeListener listener = new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				configurations.setMinCardinality(slider.getSlider().getValue());
			}
		};
		slider.addChangeListener(listener);
		listener.stateChanged(null);
		add(slider, "0, 1");
	}
}
