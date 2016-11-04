package akinabot;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageCodec;

@SpringBootApplication
public class Akinabot {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public static final String AKINATOR_API_URL = "http://api-en4.akinator.com";

	public static final String BUS_BOT_GREETINGS = "bot.greetings";
	public static final String BUS_BOT_IQUESTION = "bot.iquestion";
	public static final String BUS_BOT_QUESTION = "bot.question";
	public static final String BUS_BOT_RESULT = "bot.result";
	public static final String BUS_BOT_SORRY = "bot.sorry";
	public static final String BUS_BOT_UPDATE = "bot.update";
	public static final String BUS_BOT_SESSION_GET = "bot.session.get";
	public static final String BUS_BOT_SESSION_SET = "bot.session.set";

	public static final String BUTTON_PLAYAGAIN = "Play Again";
	public static final String BUTTON_PLAYNOW = "Play Now";
	public static final String BUTTON_QUIT = "Quit";
	public static final String BUTTON_START = "/start";

	public static void main(String[] args) {
		SpringApplication.run(Akinabot.class, args);
	}

	@Inject
	protected void deployVerticles(Vertx vertx, List<Verticle> verticles) {
		log.info("Deploying verticles");
		verticles.forEach(vertx::deployVerticle);
		log.info("Verticles deployed");
	}

	@Inject
	protected void configureMessageCodecs(EventBus eventBus, List<MessageCodec> codecs) {
		log.info("Registering codecs");
		codecs.forEach(eventBus::registerCodec);
		log.info("Codecs registered");
	}
}
