package org.processmining.plugins.nouss;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.deckfour.xes.in.XesXmlParser ;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;



public class ImportPlugin {
	private static String  xes_URI= null;  //the path to the XES file
	
    private static String importXesFile() {
		int accepted=0;
		
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			  // Instantiate a file chooser
			JFileChooser fc = new JFileChooser();
			  // Name the "open" button
			fc.setApproveButtonText("Ouvrir");
			  // Selection mode (files only)
		 	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		 	  // Filtering the importable files
		    fc.setFileFilter(new FileNameExtensionFilter("XML Documents", "xes"));
		    	// apply filters
		    fc.setAcceptAllFileFilterUsed(true);
		    	// what we click
		  	int clicked = fc.showOpenDialog(null);
		  	// if the open button is clicked :
		 	if (clicked == JFileChooser.APPROVE_OPTION) 
		 	 {
		 	       // get the path to the file
			  	String path= fc.getSelectedFile().getAbsolutePath();
			  	
			  	xes_URI = path;
			  	accepted=1;
			  	return path;
		        }

		}
		
		catch (Exception e)
		{
			
			 JOptionPane.showMessageDialog (null, "An Error has occured", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}
    
	@Plugin
    (
		name = "Plugin stage", 
		parameterLabels = {}, 
		returnLabels = {"an event log"}, 
		returnTypes = {XLog.class}, 
		userAccessible = true, 
		help = "Parses an XES file to an event log"
     )
   @UITopiaVariant
     (
		 affiliation = "CDTA", 
		 author = "BACHIRI Ines", 
		 email = "ji_bachiri@esi.dz"
     ) 
	
	
	public static XLog readLogFromFile(UIPluginContext context) throws Exception 
	{   
		String fileName = importXesFile();
		File initialFile = new File(fileName);
		InputStream inputStream = new FileInputStream(initialFile);
		
		XesXmlParser parser = new XesXmlParser();
		
		List<XLog> parsedLogs = parser.parse(inputStream);
		if (parsedLogs.size() > 0) 
		  {
		      return parsedLogs.get(0);
		  }
		return null;
	}
	
}