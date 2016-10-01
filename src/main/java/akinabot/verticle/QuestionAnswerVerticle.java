package akinabot.verticle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

import akinabot.Akinabot;
import akinabot.model.akinator.ListResponse.ListParameters;
import akinabot.model.akinator.NewSessionResponse.NewSessionParameters;
import akinabot.model.akinator.StepInformation;
import akinabot.model.bot.QuestionAnswer;
import akinabot.service.AkinatorApiService;
import akinabot.service.AkinatorApiService.InvalidAnswerException;
import akinabot.service.AkinatorApiService.ResultNotOkException;
import akinabot.verticle.codec.QuestionAnswerCodec;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.shareddata.LocalMap;

public class QuestionAnswerVerticle extends AbstractVerticle {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private AkinatorApiService akinatorApiService;
	private EventBus eventBus;
	private ConcurrentHashMap<Long, List<QuestionAnswer>> sessions = new ConcurrentHashMap<>();
	private LocalMap<Long, Buffer> mapSessions;
	private QuestionAnswerCodec qnaCodec = new QuestionAnswerCodec();
	
	@Override
	public void start() throws Exception {
		this.akinatorApiService = new AkinatorApiService(vertx.createHttpClient());
		this.eventBus = vertx.eventBus();
		this.mapSessions = vertx.sharedData().getLocalMap("sessions");

		vertx.eventBus().consumer(Akinabot.BUS_BOT_UPDATE, this::onUpdate);
	}

	private void onUpdate(io.vertx.core.eventbus.Message<Update> msg) {
		final Update update = msg.body();
		final Message message = update.message() != null ? update.message() : update.editedMessage();

		final Long chatId = message.chat().id();
		
		log.debug("[{}] Incoming update", chatId);

		sessions.putIfAbsent(chatId, new ArrayList<QuestionAnswer>());
		final List<QuestionAnswer> qnas = sessions.get(chatId);

		final QuestionAnswer qna = new QuestionAnswer();
		qna.setChatId(chatId);
		qna.setAnswer(message);

		// session is empty, send greetings
		if (qnas.isEmpty()) {
			qnas.add(qna);
			log.info("[{}] NEW SESSION", chatId);

			eventBus.send(Akinabot.BUS_BOT_GREETINGS, qna);
			return;
		}

		if (message.text() != null) {
			if (Akinabot.BUTTON_START.equals(message.text()) || Akinabot.BUTTON_PLAYAGAIN.equals(message.text())
					|| Akinabot.BUTTON_PLAYNOW.equals(message.text())) {
				qnas.clear();
				qnas.add(qna);

				log.info("[{}] START SESSION", chatId);
			}

			if (Akinabot.BUTTON_QUIT.equals(message.text())) {
				qnas.clear();
				qnas.add(qna);

				log.info("[{}] QUIT SESSION", chatId);

				eventBus.send(Akinabot.BUS_BOT_GREETINGS, qna);
				return;
			}
		}

		// first question
		if (qnas.size() == 1) {
			Future<NewSessionParameters> future = Future.future();
			future.setHandler(h -> {
				if (isResultNotOk(h)) {
					eventBus.send(Akinabot.BUS_BOT_SORRY, qna);
					return;
				}

				NewSessionParameters p = h.result();
				qna.setIdentification(p.getIdentification());
				qna.setStepInformation(p.getStepInformation());

				handleQuestionAnswer(qnas, qna);
			});

			akinatorApiService.sendOpenSession(future.completer());
		} else {
			QuestionAnswer previousQna = qnas.get(qnas.size() - 1);
			qna.setIdentification(previousQna.getIdentification());
			qna.setQuestion(previousQna);

			Future<StepInformation> future = Future.future();
			future.setHandler(h -> {
				if (isResultNotOk(h)) {
					eventBus.send(Akinabot.BUS_BOT_SORRY, qna);
					return;
				}

				if (h.failed() && h.cause() instanceof InvalidAnswerException) {
					eventBus.send(Akinabot.BUS_BOT_IQUESTION, qna);
					return;
				}

				qna.setStepInformation(h.result());
				handleQuestionAnswer(qnas, qna);
			});

			akinatorApiService.sendAnswer(qna.getIdentification(), qna.getQuestion().getStepInformation(), message, future.completer());
		}
	}

	private void handleQuestionAnswer(List<QuestionAnswer> qnas, QuestionAnswer qna) {
		if (qna.getQuestion() != null) {
			StepInformation stepInformation = qna.getQuestion().getStepInformation();
			log.debug("[{}] Q/A: {} {}", qna.getChatId(), stepInformation.getQuestion(), qna.getAnswer().text());
		}

		qnas.add(qna);
		
		// complete, send answer
		if (qna.getProgress() >= 90D || qna.getStep() >= 30) {
			Future<ListParameters> future = Future.future();
			future.setHandler(h -> {
				qna.setResult(h.result());
				eventBus.send(Akinabot.BUS_BOT_RESULT, qna);
				qnas.clear();
			});

			akinatorApiService.getResult(qna.getChatId(), qna.getIdentification(), qna.getStepInformation(), future.completer());
		} else {
			eventBus.send(Akinabot.BUS_BOT_QUESTION, qna);
		}
	}

	private boolean isResultNotOk(AsyncResult<?> result) {
		return result.failed() && result.cause() instanceof ResultNotOkException;
	}
}
