package org.codenergic.akinabot.akinator;

@SuppressWarnings("serial")
public class InvalidAnswerException extends AkinatorException {
	public InvalidAnswerException(String message) {
		super(message);
	}
}
