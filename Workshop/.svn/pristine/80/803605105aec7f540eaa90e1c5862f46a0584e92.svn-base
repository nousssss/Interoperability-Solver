package org.processmining.models.ghzbue.connections;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.ghzbue.GhzModel;

/**
 * Conencts an event log to a workshop model, provided the parameters used to
 * mine the model from the log.
 * 
 * @author hverbeek
 * 
 */
public abstract class AbstractGhzModelConnection <Configuration> extends AbstractConnection{
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
	private Configuration configuration;

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
	public AbstractGhzModelConnection(XLog log, GhzModel model, Configuration configuration) {
		super("Ghz model for log");
		put(LOG, log);
		put(MODEL, model);
		this.configuration = configuration;
	}
	
	/**
	 * Gets the parameters used to mine the workshop model from the event log.
	 * 
	 * @return The parameters used to derive the workshop model from the event
	 *         log.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}
}
