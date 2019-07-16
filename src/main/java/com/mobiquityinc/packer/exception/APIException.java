package com.mobiquityinc.packer.exception;


/**
 * Main exception within this library.
 *
 * @author Hector Blanco
 */
public class APIException extends Exception {
	
	/**
	 * {@inheritDoc}
	 */
	public APIException(String message) {
		super(message);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public APIException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
