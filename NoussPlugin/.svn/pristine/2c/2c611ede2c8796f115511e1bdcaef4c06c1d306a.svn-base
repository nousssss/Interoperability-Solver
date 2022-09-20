package org.processmining.models.workshop.sjjleemans;

public class Triple<A,B,C> {
	private final A a;
	private final B b;
	private final C c;
	
	public Triple(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public A getA() { return a; }
	public B getB() { return b; }
	public C getC() { return c; }
	
	@Override
	public int hashCode() { return a.hashCode() ^ b.hashCode() ^ c.hashCode(); }
	
	@Override
	  public boolean equals(Object o) {
	    if (o == null) return false;
	    if (!(o instanceof Triple)) return false;
	    Triple pairo = (Triple) o;
	    return this.a.equals(pairo.getA()) &&
	           this.b.equals(pairo.getB()) &&
	           this.c.equals(pairo.getC());
	  }
}
