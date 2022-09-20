package org.processmining.modelrepair.plugins.data;

import hub.top.petrinet.Transition;

/**
 * Simplified explicit representation of alignment moves for model repair.
 * 
 * @author dfahland
 */
public class Move {
    public Transition t;
    public String     e;
    public boolean isSkipStep = false;
    
    public Move(String e) {
      this.e = e;
      this.t = null;
    }
    
    public Move(Transition t) {
      this.e = null;
      this.t = t;
    }
    
    @Override
    public String toString() {
      return "["+(t != null ? t.toString() : ">>")+","+e+"]";
    }
    
    public boolean isLogMove() {
    	return t == null;
    }

}