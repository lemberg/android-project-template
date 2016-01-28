package com.ls.exceptions;

public class JsonDeserializationException extends Exception {

	private static final long serialVersionUID = -6359572067062276586L;

	public JsonDeserializationException() {
	}

	public JsonDeserializationException(String detailMessage) {
		super(detailMessage);
	}

	public JsonDeserializationException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public JsonDeserializationException(Throwable throwable) {
		super(throwable);
	}
}
