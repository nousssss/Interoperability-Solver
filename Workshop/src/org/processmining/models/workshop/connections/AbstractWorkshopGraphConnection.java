package org.processmining.models.workshop.connections;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.workshop.WorkshopModel;
import org.processmining.models.workshop.graph.WorkshopGraph;

/**
 * Connects a workshop model with a workshop graph, provided the parameters
 * which were used to derive the graph from the model.
 * 
 * @author hverbeek
 * 
 */
public abstract class AbstractWorkshopGraphConnection<Parameters> extends AbstractConnection {
	/**
	 * Label for the model end of the connection.
	 */
	public final static String MODEL = "Model";
	/**
	 * Label for the graph end of the connection.
	 */
	public final static String GRAPH = "Graph";

	/**
	 * The parameters used to derive the graph from the model.
	 */
	private Parameters parameters;

	/**
	 * Creates a connection from a workshop model to a workflow graph, where the
	 * graph is derived from the model using the given parameters.
	 * 
	 * @param model
	 *            The workshop model.
	 * @param graph
	 *            The derived workshop graph.
	 * @param parameters
	 *            The parameters used to derive the graph from the model.
	 */
	public AbstractWorkshopGraphConnection(WorkshopModel model, WorkshopGraph graph, Parameters parameters) {
		super("Workshop Graph for Model");
		put(MODEL, model);
		put(GRAPH, graph);
		this.parameters = parameters;
	}

	/**
	 * Gets the parameters used to derive the workflow graph from the workflow
	 * model.
	 * 
	 * @return The parameters used to derive the workflow graph from the
	 *         workflow model.
	 */
	public Parameters getParameters() {
		return parameters;
	}
}
