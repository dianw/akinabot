package org.codenergic.akinabot.core;

import java.io.IOException;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.GlobalSerializerConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

@Configuration
public class HazelcastConfig {
	@Bean
	public FSTConfiguration fstConfiguration() {
		return FSTConfiguration.createDefaultConfiguration()
				.setForceSerializable(true);
	}

	@Bean
	public Config hazelcastConfiguration(FSTConfiguration fstConfiguration) {
		return new Config()
				.setSerializationConfig(new SerializationConfig()
						.setGlobalSerializerConfig(new GlobalSerializerConfig()
								.setOverrideJavaSerialization(true)
								.setImplementation(new FSTSerializer(fstConfiguration))));
	}

	private static class FSTSerializer implements StreamSerializer<Object> {
		private final FSTConfiguration fstConfiguration;

		FSTSerializer(FSTConfiguration fstConfiguration) {
			this.fstConfiguration = fstConfiguration;
		}

		@Override
		public void destroy() {
			// do nothing
		}

		@Override
		public int getTypeId() {
			return 10;
		}

		@Override
		public Object read(ObjectDataInput in) throws IOException {
			return fstConfiguration.asObject(in.readByteArray());
		}

		@Override
		public void write(ObjectDataOutput out, Object object) throws IOException {
			out.writeByteArray(fstConfiguration.asByteArray(object));
		}
	}
}
