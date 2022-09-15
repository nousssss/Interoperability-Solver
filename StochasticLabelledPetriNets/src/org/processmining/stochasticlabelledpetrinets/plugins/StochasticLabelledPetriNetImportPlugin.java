package org.processmining.stochasticlabelledpetrinets.plugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSimpleWeights;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSimpleWeightsImpl;

@Plugin(name = "Stochastic labelled Petri net", parameterLabels = { "Filename" }, returnLabels = {
		"Stochastic labelled Petri net" }, returnTypes = { StochasticLabelledPetriNetSimpleWeights.class })
@UIImportPlugin(description = "Stochastic labelled Petri net files", extensions = { "slpn" })
public class StochasticLabelledPetriNetImportPlugin extends AbstractImportPlugin {
	public StochasticLabelledPetriNetSimpleWeights importFromStream(PluginContext context, InputStream input, String filename,
			long fileSizeInBytes) throws Exception {
		return read(input);
	}

	public static StochasticLabelledPetriNetSimpleWeightsImpl read(InputStream input) throws NumberFormatException, IOException {

		StochasticLabelledPetriNetSimpleWeightsImpl result = new StochasticLabelledPetriNetSimpleWeightsImpl();

		BufferedReader r = new BufferedReader(new InputStreamReader(input));

		int numberOfPlaces = Integer.parseInt(getNextLine(r));
		for (int place = 0; place < numberOfPlaces; place++) {
			result.addPlace();

			int inInitialMarking = Integer.parseInt(getNextLine(r));
			if (inInitialMarking > 0) {
				result.addPlaceToInitialMarking(place, inInitialMarking);
			}
		}

		int numberOfTransitions = Integer.parseInt(getNextLine(r));
		for (int transition = 0; transition < numberOfTransitions; transition++) {
			String line = getNextLine(r);
			double weight = Double.valueOf(getNextLine(r));
			if (line.startsWith("silent")) {
				result.addTransition(weight);
			} else if (line.startsWith("label ")) {
				result.addTransition(line.substring(6), weight);
			} else {
				throw new RuntimeException("invalid transition");
			}

			//incoming places
			{
				int numberOfIncomingPlaces = Integer.parseInt(getNextLine(r));
				for (int p = 0; p < numberOfIncomingPlaces; p++) {
					int place = Integer.parseInt(getNextLine(r));
					result.addPlaceTransitionArc(place, transition);
				}
			}

			//outgoing places
			{
				int numberOfOutgoingPlaces = Integer.parseInt(getNextLine(r));
				for (int p = 0; p < numberOfOutgoingPlaces; p++) {
					int place = Integer.parseInt(getNextLine(r));
					result.addTransitionPlaceArc(transition, place);
				}
			}
		}
		
		r.close();

		return result;
	}

	public static String getNextLine(BufferedReader r) throws IOException {
		String line = r.readLine();
		while (line != null && line.startsWith("#")) {
			line = r.readLine();
		}
		return line;
	}
}