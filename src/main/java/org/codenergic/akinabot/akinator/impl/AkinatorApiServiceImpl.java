package org.codenergic.akinabot.akinator.impl;

import org.codenergic.akinabot.akinator.AkinatorApiService;
import org.codenergic.akinabot.akinator.AkinatorException;
import org.codenergic.akinabot.akinator.InvalidAnswerException;
import org.codenergic.akinabot.akinator.ResultNotOkException;
import org.codenergic.akinabot.akinator.model.Answer;
import org.codenergic.akinabot.akinator.model.AnswerResponse;
import org.codenergic.akinabot.akinator.model.Identification;
import org.codenergic.akinabot.akinator.model.ListResponse;
import org.codenergic.akinabot.akinator.model.ListResponse.ListParameters;
import org.codenergic.akinabot.akinator.model.NewSessionResponse;
import org.codenergic.akinabot.akinator.model.NewSessionResponse.NewSessionParameters;
import org.codenergic.akinabot.akinator.model.StepInformation;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.Json;

public class AkinatorApiServiceImpl implements AkinatorApiService {
	private static final String API_URL = "api-en4.akinator.com";
	private static final String API_USERNAME = "Akinabot";
	private static final String STATUS_OK = "OK";
	private HttpClient httpClient;

	public AkinatorApiServiceImpl(Vertx vertx) {
		this.httpClient = vertx.createHttpClient(new HttpClientOptions().setDefaultHost(API_URL));
	}

	@Override
	public void getResult(Long chatId, Identification identification, StepInformation stepInformation,
			Handler<AsyncResult<ListParameters>> handler) {
		Future<ListParameters> future = Future.<ListParameters>future().setHandler(handler);
		String requestUri = buildResultUrl(identification.getSession(), identification.getSignature(), stepInformation.getStep());
		httpClient.get(requestUri, e -> {
			registerFailHandler(e, future);
			e.bodyHandler(buffer -> {
				ListResponse listResponse = Json.decodeValue(buffer.toString(), ListResponse.class);
				if (STATUS_OK.equals(listResponse.getCompletion()))
					future.complete(listResponse.getParameters());
				else
					future.fail(new ResultNotOkException(buffer.toString()));
			});
		}).end();
	}

	@Override
	public void openSessionAndGetQuestion(Handler<AsyncResult<NewSessionParameters>> handler) {
		Future<NewSessionParameters> future = Future.<NewSessionParameters>future().setHandler(handler);
		String requestUri = buildOpenSessionUrl(1, API_USERNAME);
		httpClient.get(requestUri, e -> {
			registerFailHandler(e, future);
			e.bodyHandler(buffer -> {
				NewSessionResponse sessionResponse = Json.decodeValue(buffer.toString(), NewSessionResponse.class);
				if (STATUS_OK.equals(sessionResponse.getCompletion()))
					future.complete(sessionResponse.getParameters());
				else
					future.fail(new ResultNotOkException(buffer.toString()));
			});
		}).end();
	}

	@Override
	public void sendAnswerAndGetQuestion(Identification identification, StepInformation stepInformation, String message,
			Handler<AsyncResult<StepInformation>> handler) {
		Future<StepInformation> future = Future.<StepInformation>future().setHandler(handler);
		int answerOrdinal = answerOrdinal(message, stepInformation);
		if (answerOrdinal < 0) {
			future.fail(new InvalidAnswerException("Answer ordinal is not valid"));
			return;
		}
		String requestUri = buildAnswerUrl(identification.getSession(), identification.getSignature(),
				stepInformation.getStep(), answerOrdinal);
		httpClient.get(requestUri, e -> {
			registerFailHandler(e, future);
			e.bodyHandler(buffer -> {
				AnswerResponse answerResponse = Json.decodeValue(buffer.toString(), AnswerResponse.class);
				if (STATUS_OK.equals(answerResponse.getCompletion()))
					future.complete(answerResponse.getParameters());
				else
					future.fail(new ResultNotOkException(buffer.toString()));
			});
		}).end();
	}

	private int answerOrdinal(String answer, StepInformation stepInformation) {
		int i = 0;
		for (Answer a : stepInformation.getAnswers()) {
			if (answer.equals(a.getAnswer())) 
				break;
			i++;
		}
		return i >= stepInformation.getAnswers().size() ? -1 : i;
	}

	private String buildAnswerUrl(String session, String signature, String step, int answer) {
		return new StringBuilder()
				.append("/ws/answer")
				.append("?session=").append(session)
				.append("&signature=").append(signature)
				.append("&step=").append(step)
				.append("&answer=").append(answer)
				.toString();
	}

	private String buildOpenSessionUrl(int partner, String player) {
		return new StringBuilder()
				.append("/ws/new_session")
				.append("?partner=").append(partner)
				.append("&player=").append(player)
				.toString();
	}

	private String buildResultUrl(String session, String signature, String step) {
		return new StringBuilder()
				.append("/ws/list?&size=2&max_pic_width=300&max_pic_height=300&pref_photos=VO-OK&mode_question=0")
				.append("&session=").append(session)
				.append("&signature=").append(signature)
				.append("&step=").append(step)
				.toString();
	}

	/**
	 * 
	 * @param <T>
	 * @param e
	 * @param future
	 * @return true if fails
	 */
	private <T> void registerFailHandler(HttpClientResponse e, Handler<AsyncResult<T>> handler) {
		Future<T> future = Future.<T>future().setHandler(handler);
		e.exceptionHandler(future::fail);
		if (e.statusCode() != 200) {
			future.fail(new AkinatorException(e.statusMessage()));
		}
	}
}
