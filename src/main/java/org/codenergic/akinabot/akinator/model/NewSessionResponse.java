package org.codenergic.akinabot.akinator.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, setterVisibility = Visibility.NONE)
public class NewSessionResponse implements Completable {
	@JsonProperty
	private String completion;
	@JsonProperty
	private NewSessionParameters parameters;

	@Override
	public String getCompletion() {
		return completion;
	}

	public NewSessionParameters getParameters() {
		return parameters;
	}


	@JsonAutoDetect(fieldVisibility = Visibility.ANY, setterVisibility = Visibility.NONE)
	public static class NewSessionParameters {
		@JsonProperty
		private Identification identification;
		@JsonProperty("step_information")
		private StepInformation stepInformation;

		public Identification getIdentification() {
			return identification;
		}

		public StepInformation getStepInformation() {
			return stepInformation;
		}
	}
}
