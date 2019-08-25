package org.codenergic.akinabot.core;

import java.util.stream.Stream;

public enum ChatProvider {
	FB_MESSENGER("graph.facebook.com"), LINE("api.line.me"), TELEGRAM("api.telegram.org");

	private final String apiHost;

	ChatProvider(String apiHost) {
		this.apiHost = apiHost;
	}

	public String getApiHost() {
		return apiHost;
	}

	static ChatProvider getProviderByHost(String host) {
		return Stream.of(values())
				.filter(p -> p.apiHost.equals(host))
				.findFirst()
				.orElse(null);
	}
}
