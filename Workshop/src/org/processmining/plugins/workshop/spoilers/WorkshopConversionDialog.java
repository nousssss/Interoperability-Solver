package org.processmining.plugins.workshop.spoilers;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.models.workshop.WorkshopModel;

import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * Parameter dialog for the conversion from workshop model to workshop graph.
 * 
 * @author hverbeek
 * 
 */
public class WorkshopConversionDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9048821565595960963L;

	/**
	 * Parameter dialog for converting the given workshop model to a workflow
	 * graph.
	 * 
	 * @param model
	 *            The given workshop model.
	 * @param parameters
	 *            The parameters which will be used for the conversion.
	 */
	public WorkshopConversionDialog(WorkshopModel model, final WorkshopConversionParameters parameters) {
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
				.createNiceIntegerSlider("Select cardinality threshold", model.getMinCardinality(),
						model.getMaxCardinality(), model.getMinCardinality(), Orientation.HORIZONTAL);
		/*
		 * Create a change listener on this slider that changes the parameters
		 * accordingly.
		 */
		ChangeListener listener = new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				parameters.setMinCardinality(slider.getSlider().getValue());
			}
		};
		slider.addChangeListener(listener);
		listener.stateChanged(null);
		add(slider, "0, 1");
	}
}
