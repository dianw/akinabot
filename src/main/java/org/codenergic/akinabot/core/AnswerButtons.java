package org.codenergic.akinabot.core;

import java.util.Map;

public enum AnswerButtons implements AnswerButton {
	PLAY_AGAIN("Play Again⠀"),
	PLAY_NOW("Play Now⠀"),
	QUIT("Quit⠀"),
	START("/start");
	
	private String text;

	AnswerButtons(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public Map<String, String> getAdditionalProperties() {
		return null;
	}
}
