package org.codenergic.akinabot.akinator.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, setterVisibility = Visibility.NONE)
public class Identification {
	@JsonProperty
	private long channel;
	@JsonProperty
	private String session;
	@JsonProperty
	private String signature;

	public long getChannel() {
		return channel;
	}

	public String getSession() {
		return session;
	}

	public String getSignature() {
		return signature;
	}
}
