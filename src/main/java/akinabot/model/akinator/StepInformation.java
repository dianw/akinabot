package akinabot.model.akinator;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StepInformation {
	private String question;
	private List<Answer> answers = new ArrayList<>();
	private String step;
	private String progression;
	private String questionid;
	private String infogain;
	private String statusMinibase;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getProgression() {
		return progression;
	}

	public void setProgression(String progression) {
		this.progression = progression;
	}

	public String getQuestionid() {
		return questionid;
	}

	public void setQuestionid(String questionid) {
		this.questionid = questionid;
	}

	public String getInfogain() {
		return infogain;
	}

	public void setInfogain(String infogain) {
		this.infogain = infogain;
	}

	@JsonProperty("status_minibase")
	public String getStatusMinibase() {
		return statusMinibase;
	}

	public void setStatusMinibase(String statusMinibase) {
		this.statusMinibase = statusMinibase;
	}
}
