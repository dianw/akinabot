package akinabot.model.akinator;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewSessionResponse {
	private String completion;
	private NewSessionParameters parameters;

	public String getCompletion() {
		return completion;
	}

	public void setCompletion(String completion) {
		this.completion = completion;
	}

	public NewSessionParameters getParameters() {
		return parameters;
	}

	public void setParameters(NewSessionParameters parameters) {
		this.parameters = parameters;
	}

	public static class NewSessionParameters {
		private Identification identification;
		private StepInformation stepInformation;

		public Identification getIdentification() {
			return identification;
		}

		public void setIdentification(Identification identification) {
			this.identification = identification;
		}

		@JsonProperty("step_information")
		public StepInformation getStepInformation() {
			return stepInformation;
		}

		public void setStepInformation(StepInformation stepInformation) {
			this.stepInformation = stepInformation;
		}
	}
}
