package akinabot.verticle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import akinabot.Akinabot;
import akinabot.model.akinator.Elements;
import akinabot.model.akinator.Elements.Element;
import akinabot.model.akinator.StepInformation;
import akinabot.model.bot.QuestionAnswer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

@Component
@Profile("persistence")
public class MessagePersistenceVerticle extends AbstractVerticle {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private JsonObject mongoConfig;
	private MongoClient mongoClient;

	@Inject
	public MessagePersistenceVerticle(@Named("mongoConfig") JsonObject mongoConfig) {
		this.mongoConfig = mongoConfig;
	}
	
	@Override
	public void start() throws Exception {
		mongoClient = MongoClient.createShared(vertx, mongoConfig);
		
		EventBus eventBus = vertx.eventBus();
		eventBus.consumer(Akinabot.BUS_BOT_QUESTION, this::saveQna);
		eventBus.consumer(Akinabot.BUS_BOT_RESULT, this::saveResult);
	}

	private void saveQna(Message<QuestionAnswer> msg) {
		QuestionAnswer qa = msg.body();
		QuestionAnswer prevQa = qa.getQuestion();
		StepInformation stepInformation = qa.getStepInformation();
		
		JsonObject qaDocument = new JsonObject()
				.put("chatId", qa.getChatId());
		
		if (prevQa != null)
			qaDocument
				.put("session", qa.getIdentification().getSession())
				.put("question", stepInformation.getQuestion())
				.put("answer", qa.getAnswer().text());
		
		mongoClient.save("qa", qaDocument, result -> {
			if (result.succeeded()) {
				log.debug("Data persisted {}", result.result());
			} else {
				log.debug("Error while persisting data", result.cause());
			}
		});
	}

	private void saveResult(Message<QuestionAnswer> msg) {
		QuestionAnswer qa = msg.body();
		List<Elements> results = qa.getResult().getElements();
		
		JsonObject qaDocument = new JsonObject()
				.put("chatId", qa.getChatId())
				.put("session", qa.getIdentification().getSession());
		
		JsonArray qaDocumentResults = new JsonArray();
		results.forEach(result -> {
			Element character = result.getElement();
			qaDocumentResults.add(new JsonObject()
					.put("name", character.getName())
					.put("image", character.getAbsolutePicturePath())
					.put("description", character.getDescription()));
		});
		
		qaDocument.put("results", qaDocumentResults);
		
		mongoClient.save("result", qaDocument, result -> {
			if (result.succeeded()) {
				log.debug("Data persisted {}", result.result());
			} else {
				log.debug("Error while persisting data", result.cause());
			}
		});
	}
}
