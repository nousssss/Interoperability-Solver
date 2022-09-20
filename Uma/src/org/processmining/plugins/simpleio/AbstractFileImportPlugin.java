package org.processmining.plugins.simpleio;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.processmining.framework.abstractplugins.ImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.PluginVariant;

public abstract class AbstractFileImportPlugin implements ImportPlugin {
	private File file = null;

	public File getFile() {
		return file;
	}

	@PluginVariant(requiredParameterLabels = { 0 })
	public Object importFile(PluginContext context, String filename) throws Exception {
		File file = new File(filename);
		return importFile(context, file);
	}

	@PluginVariant(requiredParameterLabels = { 0 })
	public Object importFile(PluginContext context, URI uri) throws Exception {
		File file = new File(uri);
		return importFile(context, file);
	}

	@PluginVariant(requiredParameterLabels = { 0 })
	public Object importFile(PluginContext context, URL url) throws Exception {
		return importFile(context, url.toURI());
	}

	@PluginVariant(requiredParameterLabels = { 0 })
	public Object importFile(PluginContext context, File f) throws Exception {
		file = f;
		return importFromFile(context, f);
	}

	protected abstract Object importFromFile(PluginContext context, File f) throws Exception;
}
