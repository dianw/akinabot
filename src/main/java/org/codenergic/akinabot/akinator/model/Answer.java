package org.codenergic.akinabot.akinator.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, setterVisibility = Visibility.NONE)
public class Answer {
	@JsonProperty
	private int ordinal;
	@JsonProperty("answer")
	private String answers;

	public int getOrdinal() {
		return ordinal;
	}

	public String getAnswer() {
		return answers;
	}
}