package com.mobiquityinc.packer.exception;


/**
 * Signals an error in the Thing's definition in the input file.
 *
 * @author Hector Blanco
 */
public class IncorrectThingDefinition extends APIException {
	
	/**
	 * {@inheritDoc}
	 */
	public IncorrectThingDefinition(String message) {
		super(message);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public IncorrectThingDefinition(String message, Throwable cause) {
		super(message, cause);
	}
	
}
