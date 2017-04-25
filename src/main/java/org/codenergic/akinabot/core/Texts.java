package org.codenergic.akinabot.core;

public enum Texts {
	CANT_GUESS("I'm sorry, I can't guess your character."),
	DONATE("Want to help keep AkinaBot online?"),
	DONATE_PAYPAL("Please donate to our PayPal."),
	GREETINGS("Think about a real or fictional character, I will try to guess who it is."),
	PROBLEM("Sorry, we got a problem"),
	RESULT("Your character is "),
	TYPING("typing");

	private String text;

	Texts(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return "[" + name() + "]: " + text;
	}
}
