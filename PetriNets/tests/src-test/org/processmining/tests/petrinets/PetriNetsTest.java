package org.processmining.tests.petrinets;

import org.junit.Test;
import org.processmining.contexts.cli.CLI;
import org.processmining.contexts.test.PromTest;

public class PetriNetsTest extends PromTest {

  @Test
  public void testPetriNets1() throws Throwable {
    String args[] = new String[] {"-l"};
    CLI.main(args);
  }

  @Test
  public void testPetriNets2() throws Throwable {
    String testFileRoot = System.getProperty("test.testFileRoot", ".");
    String args[] = new String[] {"-f", testFileRoot+"/PetriNets_Example.txt"};
    
    CLI.main(args);
  }
  
  @Test
  public void testPetriNets_Configurable_IO() throws Throwable {
    String testFileRoot = System.getProperty("test.testFileRoot", ".");
    String args[] = new String[] {"-f", testFileRoot+"/ConfigurablePN_io.txt"};
    
    CLI.main(args);
    

  }
  
  public static void main(String[] args) {
    junit.textui.TestRunner.run(PetriNetsTest.class);
  }
  
}
