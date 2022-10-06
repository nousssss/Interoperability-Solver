package org.processmining.models.workshop.connections;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.workshop.WorkshopModel;

/**
 * Conencts an event log to a workshop model, provided the parameters used to
 * mine the model from the log.
 * 
 * @author hverbeek
 * 
 */
public abstract class AbstractWorkshopModelConnection<Parameters> extends AbstractConnection {
	/**
	 * Label for the log end of the connection.
	 */
	public final static String LOG = "Log";
	/**
	 * Label for the model end of the connection.
	 */
	public final static String MODEL = "Model";

	/**
	 * The parameters used to mine the model from the log.
	 */
	private Parameters parameters;

	/**
	 * Creates a connection from an event log to a workshop model, where the
	 * model is mined from the log using the given parameters.
	 * 
	 * @param log
	 *            The event log.
	 * @param model
	 *            The mined workshop model.
	 * @param parameters
	 *            The parameters used to mine the model from the log.
	 */
	public AbstractWorkshopModelConnection(XLog log, WorkshopModel model, Parameters parameters) {
		super("workshop model for log");
		put(LOG, log);
		put(MODEL, model);
		this.parameters = parameters;
	}

	/**
	 * Gets the parameters used to mine the workshop model from the event log.
	 * 
	 * @return The parameters used to derive the workshop model from the event
	 *         log.
	 */
	public Parameters getParameters() {
		return parameters;
	}
}
