package org.processmining.models.graphbased;

import java.util.UUID;

public class LocalNodeID extends NodeID {

	private static final long serialVersionUID = -8053079419855495620L;
	private final UUID localNodeID;
	
	public LocalNodeID(){
		super();
		this.localNodeID = UUID.randomUUID();
	}
	
	public LocalNodeID(UUID id){
		super();
		this.localNodeID = id;
	}
	
	public UUID localNodeID(){
		return this.localNodeID;
	}
	
	public String toString(){
		return localNodeID.toString();
	}
	
}
