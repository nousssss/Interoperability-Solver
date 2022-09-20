package org.processmining.plugins.simpleio;

import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

@Plugin(name = "Import Simple Log from txt file",
		parameterLabels = { "Filename" },
		returnLabels = { "Event log" },
		returnTypes = { XLog.class })
@UIImportPlugin(description = "Simple log", extensions = { "txt" })
public class ImportSimpleLog extends AbstractFileImportPlugin {

	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("Simple log", "txt");
	}
	
	  /** 
	   * @param caseLine a string representing one case in the format
	   *        NUMx caseID activity1 activity2 ... activityN
	 * @param line 
	   * @return the sequence of activities represented by the 'aLine'
	   */
	   protected static void processCaseLine(String caseLine, XLog log, XFactory fact, int line){
	     //use a second Scanner to parse the content of each line 
	     Scanner scanner = new Scanner(caseLine);
	     scanner.useDelimiter(" ");
	     
	     Integer caseNum = 1;
	     String caseName = "case"+line;
	     LinkedList<String> activities = new LinkedList<String>();
	     
	     String firstActivityOrCaseCount = scanner.next();
	     if (Character.isDigit(firstActivityOrCaseCount.charAt(0)) && firstActivityOrCaseCount.endsWith("x")) {
		     // NUMx
	    	 caseNum = new Integer(firstActivityOrCaseCount.substring(0, firstActivityOrCaseCount.length()-1));
	     
		     // caseID
		     caseName = scanner.next();
	     } else {
	    	 activities.add(firstActivityOrCaseCount);
	     }
	    	 
	     // activity1 activity2 ... activityN
	     while ( scanner.hasNext() ){
	       activities.add(scanner.next());
	     }
	     //(no need for finally here, since String is source)
	     scanner.close();
	     
	     for (int i=0; i < caseNum; i++) {
	    	 String caseNameApp = caseName;
	    	 if (caseNum > 0) caseNameApp += "_"+i;
	    	 
		     XTrace t = fact.createTrace();
		     XConceptExtension.instance().assignName(t, caseNameApp);
		     for (String event : activities) {
		    	 String eventName = event;
		    	 String lifeCycle = "complete";
		    	 if (event.indexOf('+') != -1) {
		    		 eventName = event.substring(0,event.indexOf('+'));
		    		 lifeCycle = event.substring(event.indexOf('+')+1);
		    	 }
		    	 XEvent e = fact.createEvent();
		    	 XConceptExtension.instance().assignName(e, eventName);
		    	 XLifecycleExtension.instance().assignTransition(e, lifeCycle);
		    	 t.add(e);
		     }
		     
		     log.add(t);
	     }
	   }
	
	public XLog importFromFile(PluginContext context, File f) throws Exception {

		XFactory fact = XFactoryRegistry.instance().currentDefault();
		XLog log = fact.createLog();
		
	    Scanner scanner = new Scanner(f);
	    try {
	    	
	      int line = 0;
	      // get each line and parse the line to extract the case information
	      while ( scanner.hasNextLine() ){
	        processCaseLine( scanner.nextLine(), log, fact, line++ );
	      }
	    }
	    finally {
	      //ensure the underlying stream is always closed
	      scanner.close();
	    }
	    
	    return log;
	}

}
