package org.processmining.models.workshop.klunnel;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.models.workshop.WorkshopModel;

import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class KlunnelConversionDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2111417692065273431L;

	public KlunnelConversionDialog(WorkshopModel model, final KlunnelConversionParameters parameters) {
		// Create table layout of two rows
		double size[][] = { { TableLayoutConstants.FILL }, { 30, TableLayoutConstants.FILL } };
		setLayout(new TableLayout(size));

		// Add title to the top row
		add(SlickerFactory.instance().createLabel("<html><h2>Select conversion parameters</h2>"), "0, 0");

		// And create a nice slider
		final NiceSlider slider = SlickerFactory.instance().createNiceIntegerSlider("Select cardinality threshold",
				model.getMinCardinality(), model.getMaxCardinality(), parameters.getMinCardinality(),
				Orientation.HORIZONTAL);
		
		// Update the parameters when a user clicks on things
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				parameters.setMinCardinality(slider.getSlider().getValue());
			}
		});
		add(slider, "0, 1");
	}

}
