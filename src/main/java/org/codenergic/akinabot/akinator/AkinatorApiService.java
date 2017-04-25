package org.codenergic.akinabot.akinator;

import org.codenergic.akinabot.akinator.model.Identification;
import org.codenergic.akinabot.akinator.model.ListResponse.ListParameters;
import org.codenergic.akinabot.akinator.model.NewSessionResponse.NewSessionParameters;
import org.codenergic.akinabot.akinator.model.StepInformation;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface AkinatorApiService {
	void getResult(Long chatId, Identification identification, StepInformation stepInformation,
			Handler<AsyncResult<ListParameters>> handler);

	void openSessionAndGetQuestion(Handler<AsyncResult<NewSessionParameters>> handler);

	void sendAnswerAndGetQuestion(Identification identification, StepInformation stepInformation, String message,
			Handler<AsyncResult<StepInformation>> handler);
}