package org.processmining.helpers;

public class Triple<A, B, C> {
	private A a;
	private B b;
	private C c;

	public Triple(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public static <A,B,C> Triple<A,B,C> of(A a, B b, C c){
        return new Triple<A,B,C>(a,b,c);
    }

	public A getLeft() {
		return a;
	}

	public B getMiddle() {
		return b;
	}

	public C getRight() {
		return c;
	}

	public void setA(A a) {
		this.a = a;
	}
	
	public void setB(B b) {
		this.b = b;
	}
	
	public void setC(C c) {
		this.c = c;
	}
	
	@Override
	public int hashCode() {
		return a.hashCode() ^ b.hashCode() ^ c.hashCode();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Triple))
			return false;
		Triple pairo = (Triple) o;
		return this.a.equals(pairo.getLeft()) && this.b.equals(pairo.getMiddle()) && this.c.equals(pairo.getRight());
	}

	@Override
	public String toString() {
		return "[" + a.toString() + ", " + b.toString() + ", " + c.toString() + "]";
	}
}
