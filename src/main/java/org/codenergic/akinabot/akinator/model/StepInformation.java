package org.codenergic.akinabot.akinator.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, setterVisibility = Visibility.NONE)
public class StepInformation {
	@JsonProperty
	private String question;
	@JsonProperty
	private List<Answer> answers = new ArrayList<>();
	@JsonProperty
	private String step;
	@JsonProperty
	private String progression;
	@JsonProperty
	private String questionid;
	@JsonProperty
	private String infogain;
	@JsonProperty("status_minibase")
	private String statusMinibase;

	public String getQuestion() {
		return question;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public String getStep() {
		return step;
	}

	public String getProgression() {
		return progression;
	}

	public String getQuestionid() {
		return questionid;
	}

	public String getInfogain() {
		return infogain;
	}

	public String getStatusMinibase() {
		return statusMinibase;
	}
}
