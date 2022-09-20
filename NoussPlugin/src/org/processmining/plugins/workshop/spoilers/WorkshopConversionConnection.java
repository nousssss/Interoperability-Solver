package org.processmining.plugins.workshop.spoilers;

import org.processmining.models.workshop.WorkshopModel;
import org.processmining.models.workshop.connections.AbstractWorkshopGraphConnection;
import org.processmining.models.workshop.graph.WorkshopGraph;

/**
 * Connects a workshop model and a workshop graph using the parameters used.
 * 
 * @author hverbeek
 *
 */
public class WorkshopConversionConnection extends AbstractWorkshopGraphConnection<WorkshopConversionParameters> {

	/**
	 * Creates the connection between the model, graph, and parameters.
	 * @param model The given workshop model.
	 * @param graph The given workshop graph.
	 * @param parameters The given conversion parameters.
	 */
	public WorkshopConversionConnection(WorkshopModel model, WorkshopGraph graph,
			WorkshopConversionParameters parameters) {
		super(model, graph, parameters);
	}

}
