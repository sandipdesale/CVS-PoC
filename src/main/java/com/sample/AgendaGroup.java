package com.sample;

/**
 * @author Tejaswi Alluri
 */
public enum AgendaGroup {

	WORK_ITEM_QUALIFICATION("work-item-qualification"),

	CERTIFICATION("certification"),

	STORE_QUALIFICATION("store-qualification"),

	ASSIGNMENT("assignment");

	String value;

	private AgendaGroup(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
