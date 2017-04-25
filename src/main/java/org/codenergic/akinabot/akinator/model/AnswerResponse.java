package org.codenergic.akinabot.akinator.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, setterVisibility = Visibility.NONE)
public class AnswerResponse implements Completable {
	@JsonProperty
	private String completion;
	@JsonProperty
	private StepInformation parameters;

	@Override
	public String getCompletion() {
		return completion;
	}

	public StepInformation getParameters() {
		return parameters;
	}
}
