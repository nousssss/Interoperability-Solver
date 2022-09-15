/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2008 TU/e Eindhoven * and is licensed under the * LGPL
 * License, Version 1.0 * by Eindhoven University of Technology * Department of
 * Information Systems * http://www.processmining.org * *
 ***********************************************************/
package org.processmining.plugins.etconformance;

import org.processmining.plugins.etconformance.data.PrefixAutomaton;

/**
 * Results of the ETConformance analysis.
 * 
 * @author Jorge Munoz-Gama (jmunoz)
 */
public class ETCResults {

	/** Log Name */
	String logName;
	/** Model Name */
	String modelName;
	/** Number of Traces of the Log */
	long nTraces;
	/** Number of Non Fitting Traces of the Log */
	long nNonFitTraces;
	/** Number of Non Deterministic Traces of the Log */
	long nNonDetTraces;
	public long getnNonDetTraces() {
		return nNonDetTraces;
	}


	public void setnNonDetTraces(long nNonDetTraces) {
		this.nNonDetTraces = nNonDetTraces;
	}


	/** Number of Tasks */
	int nTasks;
	/** Average size of the Traces */
	double aveSizeTraces;
	/** Number of IN states in the automaton */
	long inStates;
	/** Number of ESCAPING states with 0 occurrences in the automaton */
	long esc0States;
	/** Number of ESCAPING states with 0> occurrences in the automaton */
	long escGStates;
	/** Number of OUTER states in the automaton */
	long outerStates;
	/** Number of NON FIT states in the automaton */
	long nonFitStates;
	/** Number of NON DET in the automaton */
	long nonDetStates;
	/** Number of Total states in the automaton */
	long totalStates;
	/** ETCPrecision Metric */
	double etcp;	
	/** ETCPrecision metric Numerator */
	long etcpNumerator;
	/** ETCPrecision metric Denominator */
	long etcpDenominator;
	/** Confidence Interval Upper Bound */
	double upperBound;
	/** Confidence Interval Lower Bound */
	double lowerBound;
	/** Threshold parameter value for defining the escaping states */
	private double escTh;
	/** Confidence interval would be computed or not */
	private boolean confidence;
	/** Number of new traces to define the Confidence interval */
	private int kConfidence;
	/** MDT log would be build or not */
	private boolean mdt;
	/** Severity of each imprecision would be computed or not */
	private boolean severity;
	/** Threshold parameter value for defining the Severity */
	private double severityTh;
	/** Prefix Automaton would be output or not*/
	private boolean automaton;
	/** Extended Prefix Automaton of the system */
	private PrefixAutomaton pA;
	/** Lazy Invisibles heuristic used */
	private boolean lazyInv;
	/** Random Indeterminism Solving heuristic used */
	private boolean randomIndet;
	
	/**
	 * Create a new Result object and set all the fields to default values.
	 */
	public ETCResults(){
		//Default values of Settings
		escTh = 0.00;
		confidence = false;
		kConfidence = 1;
		mdt = false;
		severity = false;
		severityTh = escTh * 2;
		automaton = false;
		nTraces = 0;
		nNonFitTraces = 0;
		inStates = 0;
		esc0States = 0;
		escGStates = 0;
		outerStates = 0;
		nonFitStates = 0;
		totalStates = 0;
		lazyInv = true;
		randomIndet = false;
	}

	
	// GETTERS AND SETTERS
	
	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public long getNTraces() {
		return nTraces;
	}

	public void setNTraces(long nTraces) {
		this.nTraces = nTraces;
	}	
	
	public long getnNonFitTraces() {
		return nNonFitTraces;
	}

	public void setnNonFitTraces(long nNonFitTraces) {
		this.nNonFitTraces = nNonFitTraces;
	}

	public int getNTasks() {
		return nTasks;
	}

	public void setNTasks(int nTasks) {
		this.nTasks = nTasks;
	}

	public double getAveSizeTraces() {
		return aveSizeTraces;
	}

	public void setAveSizeTraces(double aveSizeTraces) {
		this.aveSizeTraces = aveSizeTraces;
	}
	
	public long getInStates() {
		return inStates;
	}

	public void setInStates(long inStates) {
		this.inStates = inStates;
	}

	public long getEsc0States() {
		return esc0States;
	}

	public void setEsc0States(long escStates) {
		this.esc0States = escStates;
	}
	
	public long getEscGStates() {
		return escGStates;
	}

	public void setEscGStates(long escStates) {
		this.escGStates = escStates;
	}

	public long getOuterStates() {
		return outerStates;
	}

	public void setOuterStates(long outerStates) {
		this.outerStates = outerStates;
	}

	public long getNonFitStates() {
		return nonFitStates;
	}

	public void setNonFitStates(long nonFitStates) {
		this.nonFitStates = nonFitStates;
	}

	public long getTotalStates() {
		return totalStates;
	}

	public void setTotalStates(long totalStates) {
		this.totalStates = totalStates;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public long getEtcpNumerator() {
		return etcpNumerator;
	}

	public void setEtcpNumerator(long etcpNumerator) {
		this.etcpNumerator = etcpNumerator;
	}

	public long getEtcpDenominator() {
		return etcpDenominator;
	}

	public void setEtcpDenominator(long etcpDenominator) {
		this.etcpDenominator = etcpDenominator;
	}

	public PrefixAutomaton getPA() {
		return pA;
	}

	public void setPA(PrefixAutomaton pA) {
		this.pA = pA;
	}

	public double getEtcp() {
		return etcp;
	}

	public void setEtcp(double etcp) {
		this.etcp = etcp;
	}

	public double getEscTh() {
		return escTh;
	}

	public void setEscTh(double escTh) {
		this.escTh = escTh;
	}

	public boolean isConfidence() {
		return confidence;
	}

	public void setConfidence(boolean confidence) {
		this.confidence = confidence;
	}

	public int getkConfidence() {
		return kConfidence;
	}

	public void setkConfidence(int kConfidence) {
		this.kConfidence = kConfidence;
	}

	public boolean isMdt() {
		return mdt;
	}

	public void setMdt(boolean mdt) {
		this.mdt = mdt;
	}

	public boolean isSeverity() {
		return severity;
	}

	public void setSeverity(boolean severity) {
		this.severity = severity;
	}

	public double getSeverityTh() {
		return severityTh;
	}

	public void setSeverityTh(double severityTh) {
		this.severityTh = severityTh;
	}

	public boolean isAutomaton() {
		return automaton;
	}

	public void setAutomaton(boolean automaton) {
		this.automaton = automaton;
	}

	public boolean isLazyInv() {
		return lazyInv;
	}

	public void setLazyInv(boolean lazyInv) {
		this.lazyInv = lazyInv;
	}


	public boolean isRandomIndet() {
		return randomIndet;
	}


	public void setRandomIndet(boolean randomIndet) {
		this.randomIndet = randomIndet;
	}


	public long getNonDetStates() {
		return nonDetStates;
	}


	public void setNonDetStates(long nonDetStates) {
		this.nonDetStates = nonDetStates;
	}
	
	
	
}
