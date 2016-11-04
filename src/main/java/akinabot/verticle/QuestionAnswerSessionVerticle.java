package akinabot.verticle;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import akinabot.Akinabot;
import akinabot.model.bot.QuestionAnswer;
import akinabot.verticle.codec.FSTCodec;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.redis.RedisClient;

@Component
public class QuestionAnswerSessionVerticle extends AbstractVerticle {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private RedisClient redisClient;
	private FSTCodec fstCodec;

	public QuestionAnswerSessionVerticle(RedisClient redisClient, FSTCodec fstCodec) {
		this.redisClient = redisClient;
		this.fstCodec = fstCodec;
	}

	@Override
	public void start() throws Exception {
		EventBus eventBus = vertx.eventBus();
		eventBus.consumer(Akinabot.BUS_BOT_SESSION_GET, this::getQnas);
		eventBus.consumer(Akinabot.BUS_BOT_SESSION_SET, this::setQnas);
	}

	@SuppressWarnings("unchecked")
	private void getQnas(Message<String> message) {
		Future<Buffer> f1 = Future.future();

		log.debug("Getting session data from redis with id: {}", message.body());
		redisClient.getBinary(message.body(), f1.completer());

		f1.setHandler(result -> {
			Buffer buffer = result.result();

			if (buffer == null) {
				log.debug("Binary retrieved, returning empty session data");

				message.reply(new ArrayList<QuestionAnswer>(), new DeliveryOptions().setCodecName(fstCodec.name()));
				return;
			}

			log.debug("Binary retrieved, size: ", buffer.length());

			vertx.<List<QuestionAnswer>>executeBlocking(b -> {
				List<QuestionAnswer> qnas = (List<QuestionAnswer>) fstCodec.decodeFromWire(0, buffer);
				b.complete(qnas);
			}, qnas -> {
				message.reply(qnas.result(), new DeliveryOptions().setCodecName(fstCodec.name()));
			});
		});
	}

	private void setQnas(Message<List<QuestionAnswer>> message) {
		Future<Buffer> f1 = Future.future();
		vertx.executeBlocking(h -> {
			Buffer buffer = Buffer.buffer();
			fstCodec.encodeToWire(buffer, message.body());

			h.complete(buffer);
		}, f1.completer());

		f1.setHandler(buffer -> {
			redisClient.setBinary(message.headers().get("chatId"), buffer.result(), s -> {});
		});
	}
}
