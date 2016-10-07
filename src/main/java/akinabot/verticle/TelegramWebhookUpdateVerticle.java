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

import akinabot.Akinabot;
import akinabot.verticle.codec.UpdateCodec;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;

@Component
@Profile("webhook")
public class TelegramWebhookUpdateVerticle extends AbstractVerticle {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private Environment env;
	private TelegramBot bot;

	@Inject
	public TelegramWebhookUpdateVerticle(TelegramBot bot, Environment env) {
		this.bot = bot;
		this.env = env;
	}

	@Override
	public void start() throws Exception {
		EventBus eventBus = vertx.eventBus();
		HttpServer httpServer = vertx.createHttpServer();

		httpServer.requestHandler(request -> {
			log.debug("Incoming webhook update");
			
			request.bodyHandler(body -> {
				Update update = BotUtils.parseUpdate(body.toString());
				eventBus.publish(Akinabot.BUS_BOT_UPDATE, update,
						new DeliveryOptions().setCodecName(UpdateCodec.class.getName()));
			});

			request.response().end("success");
		});

		setWebhook();

		httpServer.listen(env.getProperty("server.port", int.class, 8080));
	}

	private void setWebhook() {
		vertx.executeBlocking(handler -> {
			bot.execute(new SetWebhook().url(env.getProperty("telegram.bot.webhook_url", "")));
		}, result -> {
		});
	}
}
