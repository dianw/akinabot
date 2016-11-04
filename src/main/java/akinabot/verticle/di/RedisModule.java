package akinabot.verticle.di;

import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.vertx.core.Vertx;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

@Configuration
public class RedisModule {
	@Bean
	public RedisClient redisClient(Vertx vertx, Environment env) {
		RelaxedPropertyResolver redisEnv = new RelaxedPropertyResolver(env, "redis.");
		
		RedisOptions redisOptions = new RedisOptions()
//				.setBinary(true)
				.setHost(redisEnv.getProperty("host"))
				.setPort(redisEnv.getProperty("port", int.class))
				.setAuth(redisEnv.getProperty("auth"));
		
		return RedisClient.create(vertx, redisOptions);
	}
}
