package akinabot.verticle;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import akinabot.Akinabot;
import akinabot.verticle.codec.FSTCodec;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

@Component
@Profile("longpoll")
public class TelegramUpdateVerticle extends AbstractVerticle {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final int DEFAULT_UPDATE_INTERVAL = 500;
	private static final int DEFAULT_UPDATE__LIMIT = 100;

	private int updateInterval;
	private int updateLimit;
	private final AtomicInteger offset = new AtomicInteger();

	private EventBus eventBus;
	private TelegramBot telegramBot;

	@Inject
	public TelegramUpdateVerticle(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}
	
	@Override
	public void start() throws Exception {
		this.eventBus = vertx.eventBus();

		this.updateInterval = config().getInteger("bot.update_interval", DEFAULT_UPDATE_INTERVAL);
		this.updateLimit = config().getInteger("bot.update_limit", DEFAULT_UPDATE__LIMIT);
		
		// get updates on server periodically
		vertx.setPeriodic(updateInterval, s -> {
			vertx.executeBlocking(this::fetchUpdates, this::onUpdates);
		});
	}

	private void onUpdates(AsyncResult<List<Update>> result) {
		if (result.failed()) {
			log.error("Error fetching updates", result.cause());
			return;
		}

		List<Update> updates = result.result();
		updates.forEach(update -> {
			eventBus.publish(Akinabot.BUS_BOT_UPDATE, update,
					new DeliveryOptions().setCodecName(FSTCodec.class.getName()));
		});
	}

	private void fetchUpdates(Future<List<Update>> future) {
		log.trace("Fetch update from server");
		GetUpdatesResponse response = telegramBot.execute(new GetUpdates().offset(offset.get()).limit(updateLimit));

		if (!response.isOk()) {
			future.fail(new RuntimeException("Error code: " + response.errorCode()));
		}

		List<Update> updates = response.updates();
		if (!updates.isEmpty()) {
			offset.set(updates.get(updates.size() - 1).updateId());
			offset.getAndIncrement();
		}

		log.trace("Found {} update(s)", updates.size());

		future.complete(updates);
	}
}
