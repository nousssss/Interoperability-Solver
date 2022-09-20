package org.processmining.plugins.serialize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.processmining.contexts.uitopia.UIPluginContext;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public abstract class XStreamObjectExport {
	
	public abstract String getFileExtension();
	
	private final String getExtendedFileName(File file) {
		String fileName = file.getAbsolutePath();
		
	    String extendedFileName;
	    int extIndex = fileName.lastIndexOf('.');
	    if (extIndex >= 0) {
	      String ext = fileName.substring(extIndex+1);
	      if (!ext.equals(getFileExtension()))
	        extendedFileName = fileName+"."+getFileExtension();
	      else
	        extendedFileName = fileName;
	    } else {
	      extendedFileName = fileName+"."+getFileExtension();
	    }
	    return extendedFileName;
	}
	
	public void exportXStreamObjectToFile(UIPluginContext context, XStreamObject obj, File file) throws IOException {
		
		XStream xstream = new XStream(new StaxDriver());
		xstream.alias(obj.getXStreamAlias(), obj.getClass());
		
	    // Create file 
	    FileWriter fstream = new FileWriter(getExtendedFileName(file));
	    BufferedWriter out = new BufferedWriter(fstream);

	    // write object to XML
	    xstream.toXML(obj, out);

	    //Close the output stream
	    out.close();
	}

}
