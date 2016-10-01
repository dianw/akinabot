package akinabot.service;

import com.pengrad.telegrambot.model.Message;

import akinabot.model.akinator.AnswerResponse;
import akinabot.model.akinator.Identification;
import akinabot.model.akinator.ListResponse;
import akinabot.model.akinator.ListResponse.ListParameters;
import akinabot.model.akinator.NewSessionResponse;
import akinabot.model.akinator.NewSessionResponse.NewSessionParameters;
import akinabot.model.akinator.StepInformation;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.Json;

public class AkinatorApiService {
	private HttpClient httpClient;

	public AkinatorApiService(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public void sendOpenSession(Handler<AsyncResult<NewSessionParameters>> handler) {
		String requestUri = QuestionAnswerUtils.buildOpenSessionUrl(1, "Akinabot");
		httpClient.getAbs(requestUri, response -> {
			response.exceptionHandler(h -> {
				handler.handle(Future.failedFuture(h));
			});

			response.bodyHandler(buffer -> {
				NewSessionResponse sessionResponse = Json.decodeValue(buffer.toString(), NewSessionResponse.class);
				if ("OK".equals(sessionResponse.getCompletion())) {
					handler.handle(Future.succeededFuture(sessionResponse.getParameters()));
				} else {
					handler.handle(Future.failedFuture(new ResultNotOkException()));
				}
			});
		}).end();
	}

	public void sendAnswer(Identification identification, StepInformation stepInformation, Message message,
			Handler<AsyncResult<StepInformation>> handler) {
		int answerOrdinal = QuestionAnswerUtils.answerOrdinal(message.text(), stepInformation);
		if (answerOrdinal < 0) {
			handler.handle(Future.failedFuture(new InvalidAnswerException()));
			return;
		}

		String requestUri = QuestionAnswerUtils.buildAnswerUrl(identification.getSession(),
				identification.getSignature(), stepInformation.getStep(), answerOrdinal);

		httpClient.getAbs(requestUri, response -> {
			response.exceptionHandler(h -> handler.handle(Future.failedFuture(h)));

			response.bodyHandler(buffer -> {
				AnswerResponse answerResponse = Json.decodeValue(buffer.toString(), AnswerResponse.class);
				if ("OK".equals(answerResponse.getCompletion())) {
					handler.handle(Future.succeededFuture(answerResponse.getParameters()));
				} else {
					handler.handle(Future.failedFuture(new ResultNotOkException()));
				}
			});
		}).end();
	}

	public void getResult(Long chatId, Identification identification, StepInformation stepInformation,
			Handler<AsyncResult<ListParameters>> handler) {
		String requestUri = QuestionAnswerUtils.buildResultUrl(identification.getSession(),
				identification.getSignature(), stepInformation.getStep());

		httpClient.getAbs(requestUri, response -> {
			response.exceptionHandler(h -> handler.handle(Future.failedFuture(h)));
			
			response.bodyHandler(buffer -> {
				ListResponse listResponse = Json.decodeValue(buffer.toString(), ListResponse.class);

				if ("OK".equals(listResponse.getCompletion())) {
					handler.handle(Future.succeededFuture(listResponse.getParameters()));
				} else {
					handler.handle(Future.failedFuture(new ResultNotOkException()));
				}
			});
		}).end();
	}

	@SuppressWarnings("serial")
	public static class ResultNotOkException extends RuntimeException {
	}
	
	@SuppressWarnings("serial")
	public static class InvalidAnswerException extends RuntimeException {
	}
}
