package com.jmx.wls.utils;

public class WlsMBeanException extends Exception {

	/**
	 * Encapsulates a problem when connecting to the Weblogic JMX Server
	 */
	private static final long serialVersionUID = 1L;

	public WlsMBeanException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WlsMBeanException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WlsMBeanException(String message, Throwable cause) {
		super(message, cause);
	}

	public WlsMBeanException(String message) {
		super(message);
	}

	public WlsMBeanException(Throwable cause) {
		super(cause);
	}

}
