package org.processmining.plugins.interoperability;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.FullPnmlElementFactory;
import org.processmining.plugins.pnml.base.Pnml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;




public class ImportPetriNets {
	
    private static String  pnml_URI= null;  //the path to the PNML file
	
 // Imports a PNML file (gets the path to the file)
    private static String importPnmlFile() {
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
		    fc.setFileFilter(new FileNameExtensionFilter("PNML Documents", "pnml"));
		    	// apply filters
		    fc.setAcceptAllFileFilterUsed(true);
		    	// what we click
		  	int clicked = fc.showOpenDialog(null);
		  	// if the open button is clicked :
		 	if (clicked == JFileChooser.APPROVE_OPTION) 
		 	 {
		 	       // get the path to the file
			  	String path= fc.getSelectedFile().getAbsolutePath();
			  	pnml_URI = path;
			  	return path;
		     }
		 }
		
		catch (Exception e)
		{
			
			 JOptionPane.showMessageDialog (null, "An Error has occured", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

// import and parse a pnml object from the imported file	
	private static Pnml importPnmlFromStream(PluginContext context,InputStream input, String filename) 
			throws XmlPullParserException, IOException {
		
		       // Instantiate the PNML elements factory
			FullPnmlElementFactory pnmlFactory = new FullPnmlElementFactory();
			   // the same for the XML parser factory
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			   // generate a parser from the factory
			XmlPullParser xpp = factory.newPullParser();
			   // give the parser its input (our input stream -the file-)
			xpp.setInput(input, null);
			int eventType = xpp.getEventType();
			   // a pnml object
			Pnml pnml = new Pnml();
               // shared stuff zone - one thread at a time -
			synchronized (pnmlFactory)
			 {
				pnml.setFactory(pnmlFactory);
				// Skip to the good part :p aka a start tag.	
				while (eventType != XmlPullParser.START_TAG) 
				  {
				    eventType = xpp.next();
				  }
				
				// Check if the found tag is valid before importing the corresponding the pnml element
				if (xpp.getName().equals(Pnml.TAG)) // it does
				  {
				     pnml.importElement(xpp, pnml);
				  } 
				else // it doesn't
				  { 
				     pnml.log(Pnml.TAG, xpp.getLineNumber(), "Expected pnml");
				  }
				
				if (pnml.hasErrors()) 
				  {
				    return null;
				  }
				return pnml;
			}
	}
	
	
// Convert the parsed pnml object into a petrinet
	private static Object[] connectNet(PluginContext context, Pnml pnml, PetrinetGraph net)
	 {  
		
		// Create fresh marking(s) and layout.
		Marking marking = new Marking();
		Collection<Marking> finalMarkings = new HashSet<Marking>();
		GraphLayoutConnection layout = new GraphLayoutConnection(net);
		 // convert the pnml into a petrinet
		//Initialize the Petri net, marking(s), and layout from the PNML element.
		pnml.convertToNet(net, marking, finalMarkings, layout); // HOTFIX
		
		 // Add a connection from the Petri net to the marking(s) and layout.
		context.addConnection(new InitialMarkingConnection(net, marking));
		for (Marking finalMarking : finalMarkings) {
			context.addConnection(new FinalMarkingConnection(net, finalMarking));
		}
		context.addConnection(layout);
		
	
		 // Set the label of the Petri net.	
		context.getFutureResult(0).setLabel(net.getLabel());
		
		 // set the label of the marking.
		// context.getFutureResult(1).setLabel("Marking of " + net.getLabel());

		
		 // Return the net and the marking. 
		Object[] objects = new Object[2];
		objects[0] = net;
		objects[1] = marking;
		return objects;
		
	 }
	
	
// Reads a petri net from a pnml file	
	 public static Object[] readPNFromFile(PluginContext context) throws XmlPullParserException, IOException , Exception 
	  {
		      // get the path to the file
		    String filename = importPnmlFile();
			File file = new File(filename);
			FileInputStream input = new FileInputStream(file);
			  // get the pnml object from the imported file
			Pnml pnml = importPnmlFromStream(context,input, filename);
			if (pnml == null) 
			  {
			   // No PNML found in file. Fail.
			   return null;
			  }
			  // make a petrinet out of it
			PetrinetGraph net = PetrinetFactory.newPetrinet(pnml.getLabel());
			
			return connectNet(context,pnml, net);
			
	  }
}