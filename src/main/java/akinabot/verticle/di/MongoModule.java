package akinabot.verticle.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

@Configuration
public class MongoModule {
	@Bean
	public MongoClient mongoClient(Vertx vertx, Environment env) {
		JsonObject config = new JsonObject()
				.put("host", env.getProperty("mongo.host"))
				.put("port", env.getProperty("mongo.port", int.class))
				.put("db_name", env.getProperty("mongo.database"))
				.put("username", env.getProperty("mongo.username"))
				.put("password", env.getProperty("mongo.password"));
		
		return MongoClient.createShared(vertx, config);
	}
}
