package akinabot.verticle;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.BaseResponse;

import akinabot.Akinabot;
import akinabot.verticle.codec.FSTCodec;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;

@Component
@Profile("webhook")
public class TelegramWebhookUpdateVerticle extends AbstractVerticle {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private Environment env;
	private TelegramBot telegramBot;

	@Inject
	public TelegramWebhookUpdateVerticle(TelegramBot telegramBot, Environment env) {
		this.telegramBot = telegramBot;
		this.env = env;
	}

	@Override
	public void start() throws Exception {
		EventBus eventBus = vertx.eventBus();
		HttpServer httpServer = vertx.createHttpServer();

		httpServer.requestHandler(request -> {
			log.debug("Incoming webhook update");
			
			if (!"/bot".equals(request.uri()) || "/ping".equals(request.uri())) {
				request.response().end("success");

				return;
			}

			request.bodyHandler(body -> {
				String bodyString = body.toString();
				log.debug("Webhook body: {}", bodyString);

				Update update = BotUtils.parseUpdate(bodyString);
				log.debug("Update ID: {}", update.updateId());

				eventBus.publish(Akinabot.BUS_BOT_UPDATE, update,
						new DeliveryOptions().setCodecName(FSTCodec.class.getName()));
			});

			request.response().end("success");
		});

		setWebhook();

		httpServer.listen(env.getProperty("server.port", int.class, 8080));
	}

	private void setWebhook() {
		vertx.<BaseResponse>executeBlocking(handler -> {
			log.info("Setting webhook url");
			handler.complete(telegramBot.execute(new SetWebhook().url(env.getProperty("telegram.bot.webhook_url", ""))));
		}, result -> {
			BaseResponse response = result.result();
			if (response.isOk()) {
				log.info("Setting webhook url completed");
			} else {
				log.error("Failed setting webhook: {}", response.description());
			}
		});
	}
}
