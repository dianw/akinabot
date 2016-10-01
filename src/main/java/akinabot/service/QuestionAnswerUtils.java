package akinabot.service;

import akinabot.Akinabot;
import akinabot.model.akinator.Answer;
import akinabot.model.akinator.StepInformation;

public final class QuestionAnswerUtils {
	private QuestionAnswerUtils() {
	}
	
	public static String createQuestion(StepInformation stepInformation) {
		StringBuilder builder = new StringBuilder();
		builder.append(Integer.valueOf(stepInformation.getStep()) + 1)
				.append(". ")
				.append(stepInformation.getQuestion());
		
		return builder.toString();
	}

	public static int answerOrdinal(String answer, StepInformation stepInformation) {
		int i = 0;
		for (Answer a : stepInformation.getAnswers()) {
			if (answer.equals(a.getAnswer())) break;
			i++;
		}

		return i >= stepInformation.getAnswers().size() ? -1 : i;
	}

	public static String buildOpenSessionUrl(int partner, String player) {
		return new StringBuilder(Akinabot.AKINATOR_API_URL)
				.append("/ws/new_session")
				.append("?partner=").append(partner)
				.append("&player=").append(player)
				.toString();
	}

	public static String buildAnswerUrl(String session, String signature, String step, int answer) {
		return new StringBuilder(Akinabot.AKINATOR_API_URL)
				.append("/ws/answer")
				.append("?session=").append(session)
				.append("&signature=").append(signature)
				.append("&step=").append(step)
				.append("&answer=").append(answer)
				.toString();
	}

	public static String buildResultUrl(String session, String signature, String step) {
		return new StringBuilder(Akinabot.AKINATOR_API_URL)
				.append("/ws/list?&size=2&max_pic_width=300&max_pic_height=300&pref_photos=VO-OK&mode_question=0")
				.append("&session=").append(session)
				.append("&signature=").append(signature)
				.append("&step=").append(step)
				.toString();
	}
}
