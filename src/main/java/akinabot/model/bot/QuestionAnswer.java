package akinabot.model.bot;

import com.pengrad.telegrambot.model.Message;

import akinabot.model.akinator.Identification;
import akinabot.model.akinator.ListResponse.ListParameters;
import akinabot.model.akinator.StepInformation;

public class QuestionAnswer {
	private Long chatId;
	private int qaStep = 0;
	private Identification identification;
	private Message answer;
	private QuestionAnswer question;
	private StepInformation stepInformation;
	private ListParameters result;

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public int getQaStep() {
		return qaStep;
	}

	public void setQaStep(int qaStep) {
		this.qaStep = qaStep;
	}

	public Identification getIdentification() {
		return identification;
	}

	public void setIdentification(Identification identification) {
		this.identification = identification;
	}

	public Message getAnswer() {
		return answer;
	}

	public void setAnswer(Message answer) {
		this.answer = answer;
	}

	public QuestionAnswer getQuestion() {
		return question;
	}

	public void setQuestion(QuestionAnswer question) {
		this.question = question;
	}

	public StepInformation getStepInformation() {
		return stepInformation;
	}

	public void setStepInformation(StepInformation stepInformation) {
		this.stepInformation = stepInformation;
	}

	public ListParameters getResult() {
		return result;
	}

	public void setResult(ListParameters result) {
		this.result = result;
	}

	public Integer getStep() {
		try {
			return Integer.valueOf(stepInformation.getStep());
		} catch (Exception e) {
			return -1;
		}
	}

	public Double getProgress() {
		try {
			return Double.valueOf(stepInformation.getProgression());
		} catch (Exception e) {
			return -1D;
		}
	}
}
