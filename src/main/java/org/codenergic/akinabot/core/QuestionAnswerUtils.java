package org.codenergic.akinabot.core;

import org.codenergic.akinatorj.model.Answer;
import org.codenergic.akinatorj.model.StepInformation;

public final class QuestionAnswerUtils {
	private QuestionAnswerUtils() {
	}

	public static String createQuestion(StepInformation stepInformation) {
		return (Integer.parseInt(stepInformation.getStep()) + 1)
				+ ". "
				+ stepInformation.getQuestion();
	}

	public static int answerOrdinal(String answer, StepInformation stepInformation) {
		int i = 0;
		for (Answer a : stepInformation.getAnswers()) {
			if (answer.equals(a.getAnswer())) {
				return i;
			}
			i++;
		}
		return -100;
	}
}
