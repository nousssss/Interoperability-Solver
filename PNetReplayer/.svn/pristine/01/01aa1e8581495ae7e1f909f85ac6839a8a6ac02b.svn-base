/**
 * 
 */
package org.processmining.plugins.petrinet.manifestreplayer.transclassifier;


/**
 * @author aadrians
 * Feb 19, 2012
 *
 */
public class TransClass implements Comparable<TransClass>{
	private String id;
	
	@SuppressWarnings("unused")
	private TransClass(){};
	
	/**
	 * Default constructor
	 * @param id
	 */
	public TransClass(String id){
		this.id = id;
	}

	/**
	 * @return ID of the transition class
	 */
	public String getId() {
		return id;
	}

	/**
	 * comparison is based on UUID
	 */
	public int compareTo(TransClass o) {
		return getId().compareTo(o.getId());
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof TransClass){
			return getId().equals(((TransClass) o).getId());
		}
		return super.equals(o);
	}

	/**
	 * return the id
	 */
	public String toString() {
		return getId();
	}

}
