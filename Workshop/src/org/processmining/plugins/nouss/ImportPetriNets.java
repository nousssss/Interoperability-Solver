package org.processmining.plugins.nouss;

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

import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.FullPnmlElementFactory;
import org.processmining.plugins.pnml.base.Pnml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;




public class ImportPetriNets {
	
    private static String  pnml_URI= null;  //the path to the XES file
	
    private static String importPnmlFile() {
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
			  	// idk what t does
			  	//String temp = path.replaceAll("\\\\", "/");

			 	//path= "file:"+path;
			  	System.out.println("--------------------------------\n\n");
			  
			  	System.out.println(path);
			  	pnml_URI = path;
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

	
	public Pnml importPnmlFromStream(InputStream input, String filename, long fileSizeInBytes) 
			throws XmlPullParserException, IOException {
		
		       // Instantiate the PNML elements factory
			FullPnmlElementFactory pnmlFactory = new FullPnmlElementFactory();
			   // the same for the XML parser factory
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			   // get an xml parser
			XmlPullParser xpp = factory.newPullParser();
			   // give the parser its input (our input stream -the file-)
			xpp.setInput(input, null);
			int eventType = xpp.getEventType();
			   // a pnml object
			Pnml pnml = new Pnml();
			
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
	
	
	
	public Object[] connectNet(Pnml pnml, PetrinetGraph net)
	 {  // Return the net and the marking.
		
		Marking marking = new Marking();
		Collection<Marking> finalMarkings = new HashSet<Marking>();
		GraphLayoutConnection layout = new GraphLayoutConnection(net);
		 // convert the pnml into a petrinet (with 
		pnml.convertToNet(net, marking, finalMarkings, layout);
		Object[] objects = new Object[2];
		objects[0] = net;
		objects[1] = marking;
		 // return the petrinet and its marking
		return objects;
		
	 }
	
	
	 public Object[] importFromStream(InputStream input, String filename, long fileSizeInBytes) throws
		XmlPullParserException, IOException 
	 {
		Pnml pnml = importPnmlFromStream(input, filename, fileSizeInBytes);
		if (pnml == null) 
		  {
		   // No PNML found in file. Fail.
		   return null;
		  }
		PetrinetGraph net = PetrinetFactory.newPetrinet(pnml.getLabel());
		return connectNet(pnml, net);
	  }
		
	 public Object[] importFromFile() throws Exception 
	  {
		    String filename = importPnmlFile();
			File file = new File(filename);
			return importFromStream(new FileInputStream(file), filename,file.length());
	  }
}