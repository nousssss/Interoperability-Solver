package org.processmining.models.workshop.sjjleemans;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.workshop.sjjleemans.ProcessTree.model.ProcessTreeModel;

public class AbstractProcessTreeModelConnection<Parameters> extends AbstractConnection {
	public final static String LOG = "Log";
	public final static String MODEL = "Model";
	
	private Parameters parameters;
	
	public AbstractProcessTreeModelConnection(XLog log, ProcessTreeModel model, Parameters parameters) {
		super("super model for log");
		put(LOG, log);
		put(MODEL, model);
		this.parameters = parameters;
	}
	
	public Parameters getParameters() {
		return parameters;
	}
}
