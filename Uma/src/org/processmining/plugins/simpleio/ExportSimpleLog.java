package org.processmining.plugins.simpleio;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;


@Plugin(name = "Simple Event Log Export", returnLabels = {}, returnTypes = {}, parameterLabels = { "Log", "File" }, userAccessible = true)
@UIExportPlugin(description = "Simple Event Log Files", extension = "txt")
public class ExportSimpleLog {
  
  @PluginVariant(variantLabel = "Simple Event Log Export", requiredParameterLabels = { 0, 1 })
  public void exportPetriNetToEPNMLFile(UIPluginContext context, XLog log, File file) throws IOException {
    exportLogToSimpleLog(context, log, file);
  }

  protected void exportLogToSimpleLog(UIPluginContext context, XLog log, File file) throws IOException {

    boolean interrupted = false;
    
    LinkedList<String[]> traces = new LinkedList<String[]>();
    
    int traceNum = 0;
    for (XTrace trace : log) {
      LinkedList<String> eventTrace = new LinkedList<String>();
      try {
        for (XEvent event : trace) {
          String eventName = "";
          String eventType = "";
          for (Entry<String, XAttribute> att : event.getAttributes().entrySet()) {
            if (att.getKey().equals("concept:name")) eventName = att.getValue().toString();
            if (att.getKey().equals("lifecycle:transition")) eventType = att.getValue().toString();
          }
          String eventString = eventName+"+"+eventType;
          eventTrace.add(eventString);
        }
        String[] eventTrace2 = new String[eventTrace.size()];
        eventTrace.toArray(eventTrace2);
        traces.add(eventTrace2);
        
      } catch (AssertionError e) {
        System.out.print(" failed to read ");
      }
      
      traceNum++;

      //System.out.print(traceNum+" ");
      //if (traceNum % 40 == 0) System.out.println();
      
      if (context.getProgress().isCancelled()) {
        interrupted = true;
        break;
      }
    }
    
    hub.top.uma.view.ViewGeneration2.writeTraces(file.getAbsolutePath(), traces);
  }
}
