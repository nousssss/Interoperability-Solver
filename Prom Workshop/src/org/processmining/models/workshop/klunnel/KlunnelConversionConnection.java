package org.processmining.models.workshop.klunnel;

import org.processmining.models.workshop.WorkshopModel;
import org.processmining.models.workshop.connections.AbstractWorkshopGraphConnection;
import org.processmining.models.workshop.graph.WorkshopGraph;

public class KlunnelConversionConnection extends AbstractWorkshopGraphConnection<KlunnelConversionParameters> {
	public KlunnelConversionConnection(WorkshopModel model, WorkshopGraph graph,
			KlunnelConversionParameters parameters) {
		super(model, graph, parameters);
	}

}
