package org.processmining.models.workshop.fbetancor.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.workshop.fbetancor.constructors.Output;
import org.processmining.models.workshop.fbetancor.constructors.OutputDefinition;
import org.processmining.models.workshop.fbetancor.dataqualityaspects.DomainConsistency.ValuesByCompression;
import org.processmining.models.workshop.fbetancor.dataqualityaspects.coverage.CoverageWarn;
import org.processmining.models.workshop.fbetancor.dataqualityaspects.duplicationFree.DuplicatedAttributes;
import org.processmining.models.workshop.fbetancor.dataqualityaspects.duplicationFree.DuplicatedEvents;
import org.processmining.models.workshop.fbetancor.dataqualityaspects.format.Format;
import org.processmining.models.workshop.fbetancor.dataqualityaspects.timeliness.EventOpportunityTimestamp;
import org.processmining.models.workshop.fbetancor.interfaces.CentralRegistryInterface;
import org.processmining.models.workshop.fbetancor.interfaces.QualityCheck;

/**
 * DataQualityPlugin. This class takes on the input (XES Event Log) and goes
 * through all the elements of the log. Different quality checks are being
 * called upon and the data is being stored in the OutputDefinition.class.
 * 
 * @author R. Verhulst
 */
public class DataQualityPlugin {
	@Plugin(name = "Event Log Quality Check For Business Processes", 
			parameterLabels = { "Log" }, 
			returnLabels = {"Data Quality Score Card"}, 
			returnTypes = {OutputDefinition.class}, 
			help = "Event Data Quality Check on an XES formatted event log.", 
			userAccessible = true)
	@UITopiaVariant(affiliation = "FING", 
			author = "Francisco Betancor - Federico Perez", 
			email = "fbetancorp@gmail.com")
	public OutputDefinition runDefault(PluginContext context, XLog eventlog) {
		/*
		 * Shows a progress bar.
		 */
		context.getProgress().setMinimum(0);
		context.getProgress().setMaximum(eventlog.size());
		context.getProgress().isCancelled();

		/*
		 * Create a list where the different quality aspects are being stored
		 * in.
		 */
		List<QualityCheck> qualityAspects = new ArrayList<QualityCheck>();

		/*
		 * Initialize all the Data Quality Checks by creating objects referring
		 * to the correct classes.
		 */
		ValuesByCompression valuesByCompressionCheck = new ValuesByCompression();
		DuplicatedEvents duplicatedEvents = new DuplicatedEvents();
		DuplicatedAttributes duplicatedAttributes = new DuplicatedAttributes();
		Format format = new Format();
		CoverageWarn coverageWarn = new CoverageWarn();
		EventOpportunityTimestamp eventOpportunityTimestamp = new EventOpportunityTimestamp();
		/*
		 * Add all the quality checks to the list of Quality Aspects.
		 */
		qualityAspects.add(valuesByCompressionCheck);
		qualityAspects.add(duplicatedEvents);
		qualityAspects.add(duplicatedAttributes);
		qualityAspects.add(format);
		qualityAspects.add(coverageWarn);
		qualityAspects.add(eventOpportunityTimestamp);
		for (QualityCheck element : qualityAspects) {
			element.initialize();
		}

		CentralRegistryInterface central = new CentralRegistryInterface();
		central.initialize();
		central.fill(eventlog);

		for (QualityCheck element : qualityAspects) {
			element.checkQuality(eventlog);
		}

		/*
		 * Loop through all the traces.
		 */
		for (int i = 0; i < eventlog.size(); i++) {
			XTrace currentTrace = eventlog.get(i);
			central.fill(eventlog, currentTrace);

			for (QualityCheck element : qualityAspects) {
				element.checkQuality(eventlog, currentTrace);
			}

			/*
			 * Loop through all the Trace-Attributes.
			 */
			XAttributeMap attributeMapTrace = currentTrace.getAttributes();
			Set<String> attTraceMapKeys = attributeMapTrace.keySet();

			for (String attKey : attTraceMapKeys) {
				XAttribute att = attributeMapTrace.get(attKey);
				central.fill(eventlog, currentTrace, att);

				for (QualityCheck element : qualityAspects) {
					element.checkQuality(eventlog, currentTrace, att);
				}
			}

			/*
			 * Loop through all Events.
			 */
			for (int j = 0; j < currentTrace.size(); j++) {
				XEvent currentEvent = currentTrace.get(j);
				central.fill(eventlog, currentTrace, currentEvent);

				for (QualityCheck element : qualityAspects) {
					element.checkQuality(eventlog, currentTrace, currentEvent);
				}

				/*
				 * Loop through all Event-Attribute values.
				 */
				XAttributeMap attributeMapEvent = currentEvent.getAttributes();
				Set<String> attEventMapKeys = attributeMapEvent.keySet();

				/*
				 * Loop through all attribute values. (Cell-Values)
				 */
				for (String attKey : attEventMapKeys) {
					XAttribute att = attributeMapEvent.get(attKey);
					central.fill(eventlog, currentTrace, currentEvent, att);

					for (QualityCheck element : qualityAspects) {
						element.checkQuality(eventlog, currentTrace, currentEvent, att);
					}
				}
			}

			// Increase the Progress Bar.
			context.getProgress().inc();
		}

		/*
		 * Create a list where all the scores are being kept in. This is needed
		 * for the calculation of the overall score.
		 */
		List<String> scoreList = new ArrayList<String>();

		/*
		 * Create a list where all the output is being stored in.
		 */
		List<Output> outputList = new ArrayList<Output>();

		for (QualityCheck element : qualityAspects) {
			element.checkClear(central);
			outputList.add(element.getResult());

			/*
			 * Avoid calling on .getResult() twice, since it can influence the
			 * calculation of the scores.
			 */
			scoreList.add(outputList.get(outputList.size() - 1).getScore());
		}

		ScoreCalculator tScore = new ScoreCalculator();
		String outputScore = tScore.getTotalScore(scoreList);
		String eventlogName = "" + eventlog.getAttributes().get("concept:name");
		OutputDefinition output = new OutputDefinition(eventlogName, outputList, outputScore);

		return output;
	}

}