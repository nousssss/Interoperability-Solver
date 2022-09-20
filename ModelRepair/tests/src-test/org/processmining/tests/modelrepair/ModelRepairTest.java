package org.processmining.tests.modelrepair;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.junit.Assert;
import org.junit.Test;
import org.processmining.contexts.cli.CLI;
import org.processmining.contexts.test.PromTest;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.modelrepair.parameters.RepairConfiguration;
import org.processmining.modelrepair.plugins.Uma_RepairModel_Plugin;
import org.processmining.modelrepair.plugins.align.PNLogReplayer;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

import junit.framework.JUnit4TestAdapter;

public class ModelRepairTest extends PromTest {

  @Test
  public void testModelRepair1() throws Throwable {
    String args[] = new String[] {"-l"};
    CLI.main(args);
  }
  
	/**
	 * Test model repair with standard construction of transition to event class
	 * mapping.
	 * 
	 * @param context
	 * @return
	 * @throws Throwable
	 */
	@Plugin(name = "test_repair_model", parameterLabels = {}, //
	returnLabels = { "test result" }, returnTypes = { String.class }, userAccessible = false)
	public static String testUma_repair_model(PluginContext context) throws Throwable {
		String testFileRoot = System.getProperty("test.testFileRoot", PromTest.defaultTestDir);

		Object net_and_marking[] = NetImport.loadNet(context, testFileRoot + "/model_repair_test1_base.pnml");
		PetrinetGraph net = (PetrinetGraph) net_and_marking[0];
		Marking initMarking = (Marking) net_and_marking[1];
		Marking finalMarking = (Marking) PNLogReplayer.constructFinalMarking(context, net)[1];

		XLog log = XESImport.readXLog(testFileRoot + "/model_repair_test1_log.xes.gz");
		XEventClassifier classifier = PNLogReplayer.getDefaultClassifier(log);
		TransEvClassMapping current_map = PNLogReplayer.getEventClassMapping(context, net, log, classifier);

		Uma_RepairModel_Plugin repair = new Uma_RepairModel_Plugin();
		RepairConfiguration config = new RepairConfiguration();
		Object[] result = repair.run_repairModel(context, log, net, initMarking, finalMarking, config, current_map);

		Assert.assertNotNull("Repair returned no result", result);
		Assert.assertNotNull("Repair returned no result", result[0]);
		Assert.assertNotNull("Repair returned no result", result[1]);

		return "success";
	}
	
	/**
	 * Test model repair with construction of transition to event class mapping
	 * with the non-default event classifier in the log.
	 * 
	 * @param context
	 * @return
	 * @throws Throwable
	 */
	@Plugin(name = "test_repair_model2", parameterLabels = {}, //
	returnLabels = { "test result" }, returnTypes = { String.class }, userAccessible = false)
	public static String testUma_repair_model2(PluginContext context) throws Throwable {
		String testFileRoot = System.getProperty("test.testFileRoot", PromTest.defaultTestDir);

		Object net_and_marking[] = NetImport.loadNet(context, testFileRoot + "/model_repair_test1_base.pnml");
		PetrinetGraph net = (PetrinetGraph) net_and_marking[0];
		Marking initMarking = (Marking) net_and_marking[1];
		Marking finalMarking = (Marking) PNLogReplayer.constructFinalMarking(context, net)[1];

		XLog log = XESImport.readXLog(testFileRoot + "/model_repair_test1_log.xes.gz");
		
		XLogInfo summary = XLogInfoFactory.createLogInfo(log);
		XEventClassifier eventNameClassifier = null;
		for (XEventClassifier cl : summary.getEventClassifiers()) {
			if (cl instanceof XEventNameClassifier) {
				eventNameClassifier = cl; break;
			}
		}
		
		TransEvClassMapping current_map = PNLogReplayer.getEventClassMapping(context, net, log, eventNameClassifier);

		Uma_RepairModel_Plugin repair = new Uma_RepairModel_Plugin();
		RepairConfiguration config = new RepairConfiguration();
		Object[] result = repair.run_repairModel(context, log, net, initMarking, finalMarking, config, current_map);

		Assert.assertNotNull("Repair returned no result", result);
		Assert.assertNotNull("Repair returned no result", result[0]);
		Assert.assertNotNull("Repair returned no result", result[1]);

		return "success";
	}
	
	@Plugin(name = "test_repair_helper_build_final_marking", parameterLabels = {"Petri net"}, //
	returnLabels = { "final marking" }, returnTypes = { Marking.class }, userAccessible = false)
	public static Marking testModelRepair_helper_construct_finalMarking(PluginContext context, PetrinetGraph net) throws Throwable {
		Marking finalMarking = (Marking) PNLogReplayer.constructFinalMarking(context, net)[1];
		return finalMarking;
	}

	
	// automatically loading test with ProM Test Suite runner from ant requires a test adapter 
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ModelRepairTest.class);
    }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(ModelRepairTest.class);
  }
  
}
