package org.processmining.pnetreplayer.parameters;

import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;


public interface TransEvClassMappingParameter {

	public void setMapping(TransEvClassMapping mapping);
	public TransEvClassMapping getMapping();
}
