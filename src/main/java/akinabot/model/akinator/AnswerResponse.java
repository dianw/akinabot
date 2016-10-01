package akinabot.model.akinator;

public class AnswerResponse {
	private String completion;
	private StepInformation parameters;

	public String getCompletion() {
		return completion;
	}

	public void setCompletion(String completion) {
		this.completion = completion;
	}

	public StepInformation getParameters() {
		return parameters;
	}

	public void setParameters(StepInformation parameters) {
		this.parameters = parameters;
	}
}
