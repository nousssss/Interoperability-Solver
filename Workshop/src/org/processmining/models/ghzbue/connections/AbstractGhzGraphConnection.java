package org.processmining.models.ghzbue.connections;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.ghzbue.GhzModel;
import org.processmining.models.ghzbue.graph.GhzGraph;

/**
 * Connects a workshop model with a workshop graph, provided the parameters
 * which were used to derive the graph from the model.
 * 
 * @author hverbeek
 * 
 */

public class AbstractGhzGraphConnection <Configurations> extends AbstractConnection {

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
	private Configurations configurations;
	
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
	protected AbstractGhzGraphConnection(GhzModel model, GhzGraph graph, Configurations configurations) {
		super("Ghz Graph for Ghz Model");
		put(MODEL, model);
		put(GRAPH, graph);
		this.configurations = configurations;
	}

	/**
	 * Gets the parameters used to derive the workflow graph from the workflow
	 * model.
	 * 
	 * @return The parameters used to derive the workflow graph from the
	 *         workflow model.
	 */
	public Configurations getConfigurations() {
		return configurations;
	}

}
