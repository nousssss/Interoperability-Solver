package org.processmining.tests.uma;

import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.processmining.contexts.cli.CLI;
import org.processmining.contexts.test.PromTest;

public class UmaTest extends PromTest {

	@Test
	public void testUma1() throws Throwable {
		String args[] = new String[] { "-l" };
		CLI.main(args);
	}

	/*
	 * @Test public void testUma_simplifyModel() throws Throwable { String
	 * testFileRoot = System.getProperty("test.testFileRoot",
	 * PromTest.defaultTestDir); String args[] = new String[] {"-f",
	 * testFileRoot+"/Uma_SimplifyModel.txt"};
	 * 
	 * CLI.main(args); }
	 * 
	 * 
	 * @Test public void testUma_simplifyModel2() throws Throwable { String
	 * testFileRoot = System.getProperty("test.testFileRoot", defaultTestDir);
	 * String args[] = new String[] {"-f",
	 * testFileRoot+"/Uma_SimplifyModel2.txt"};
	 * 
	 * CLI.main(args); }
	 * 
	 * @Test public void testUma_buildUnfolding() throws Throwable { String
	 * testFileRoot = System.getProperty("test.testFileRoot", defaultTestDir);
	 * String args[] = new String[] {"-f",
	 * testFileRoot+"/Uma_BuildUnfolding.txt"};
	 * 
	 * CLI.main(args); }
	 * 
	 * 
	 * @Test public void testUma_checkSoundnessFC() throws Throwable { String
	 * testFileRoot = System.getProperty("test.testFileRoot", defaultTestDir);
	 * String args[] = new String[] {"-f",
	 * testFileRoot+"/Uma_CheckSoundnessFC.txt"};
	 * 
	 * CLI.main(args); }
	 */

	@Test
	public void testUma_buildUnfolding_invalid() throws Throwable {
		String testFileRoot = System.getProperty("test.testFileRoot", PromTest.defaultTestDir);
		String args[] = new String[] { "-f", testFileRoot + "/Uma_BuildUnfolding_invalid.txt" };

		try {
			CLI.main(args);
		} catch (Throwable e) {
			if (e instanceof java.util.concurrent.CancellationException) {
				return; // expect cancellation of execution because of invalid model
			} else {
				throw e;
			}
		}

		// TODO: expected failure until framework update
		Assert.assertTrue("Cancellation of plugin execution because of invalid model", false);
	}

//	@Plugin(name = "test_filter_log", parameterLabels = {}, //
//	returnLabels = { "test result" }, returnTypes = { String.class }, userAccessible = false)
//	public static String testUma_filter_log(PluginContext context) throws Throwable {
//		String testFileRoot = System.getProperty("test.testFileRoot", PromTest.defaultTestDir);
//
//		XLog log = XESImport.readXLog(testFileRoot + "/log_filter_regression1.xes.gz");
//
//		AttributeLogFilterPlugin filter = new AttributeLogFilterPlugin();
//
//		AttributeLogFilter logFilter = new AttributeLogFilter(log);
//		logFilter.attribute_filterOn = AttributeLogFilter.TRACE_ATTRIBUTE;
//		logFilter.attribute_key = "concept:name";
//		logFilter.attribute_values = new HashSet<String>();
//		logFilter.attribute_values.add("1");
//		logFilter.attribute_include = true;
//		XLog filtered = filter.filterLog(context, log, logFilter);
//
//		Assert.assertNotNull("Filtered log not null", filtered);
//		Assert.assertEquals("Filtered log has 1 case", 1, filtered.size());
//
//		return "success";
//	}
	
	// automatically loading test with ProM Test Suite runner from ant requires a test adapter 
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(UmaTest.class);
    }

	public static void main(String[] args) {
		junit.textui.TestRunner.run(UmaTest.class);
	}

}
