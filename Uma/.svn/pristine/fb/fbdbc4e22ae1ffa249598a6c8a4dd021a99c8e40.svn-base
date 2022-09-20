package org.processmining.plugins.serialize;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.simpleio.AbstractFileImportPlugin;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public abstract class XStreamObjectImport extends AbstractFileImportPlugin {
	
	protected abstract Map<String, Class<? extends Object>> getAliasMap();

	protected Object importFromFile(PluginContext context, File f) throws Exception {
		
		XStream xstream = new XStream(new StaxDriver());
		// register aliases to objects
		for (Map.Entry<String, Class<? extends Object>> map : getAliasMap().entrySet()) {
			xstream.alias(map.getKey(), map.getValue());
		}
		
		FileInputStream input = new FileInputStream(f);
		
		Object obj = xstream.fromXML(input);

		input.close();

		return obj;
	}

}
