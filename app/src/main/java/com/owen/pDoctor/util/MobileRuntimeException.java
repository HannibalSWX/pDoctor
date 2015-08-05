package com.owen.pDoctor.util;

public class MobileRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -2303393439745844216L;

	public MobileRuntimeException() {
	}

	public MobileRuntimeException(String p_message) {
		super(p_message);
	}

	public MobileRuntimeException(Throwable p_throwable) {
		super(p_throwable);
	}

	public MobileRuntimeException(String p_message, Throwable p_throwable) {
		super(p_message, p_throwable);
	}
}
