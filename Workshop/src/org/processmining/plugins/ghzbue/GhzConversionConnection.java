package org.processmining.plugins.ghzbue;

import org.processmining.models.ghzbue.GhzModel;
import org.processmining.models.ghzbue.connections.AbstractGhzGraphConnection;
import org.processmining.models.ghzbue.graph.GhzGraph;


/**
 * Connects a workshop model and a workshop graph using the parameters used.
 * 
 * @author hverbeek
 *
 */
public class GhzConversionConnection extends AbstractGhzGraphConnection<GhzConversionConfiguration> {

	/**
	 * Creates the connection between the model, graph, and parameters.
	 * @param model The given workshop model.
	 * @param graph The given workshop graph.
	 * @param parameters The given conversion parameters.
	 */
	public GhzConversionConnection(GhzModel model, GhzGraph graph,
			GhzConversionConfiguration configurations) {
		super(model, graph, configurations);
		// TODO Auto-generated constructor stub
	}

}
