package com.sample;

public class Artifact {

	private Object fact;

	private String factName;

	private Artifact(Object fact, String factName) {
		super();
		this.fact = fact;
		this.factName = factName;
	}

	public Object getFact() {
		return fact;
	}

	public String getFactName() {
		return factName;
	}

	public static Artifact newArtifact(Object fact, String factName) {
		return new Artifact(fact, factName);
	}

}
