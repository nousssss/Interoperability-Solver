package org.processmining.plugins.interoperability;

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



public class ImportLog {
	//private static String  xes_URI= null;  //the path to the XES file
	
// Imports an XES file (gets the path to the file)
    private static String importXesFile() {	
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
			 // 	xes_URI = path;
			  	return path;
		      }
		}
		
		catch (Exception e)
		{
			
			 JOptionPane.showMessageDialog (null, "An Error has occured", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}
    
// Parses an XES file to an event log	
	public static XLog readLogFromFile() throws Exception 
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