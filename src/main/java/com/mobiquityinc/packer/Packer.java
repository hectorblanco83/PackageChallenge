package com.mobiquityinc.packer;

import com.mobiquityinc.packer.exception.APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main class of the library and responsible to calculate the packages based on input.
 * Users of this library should use this class and it's static methods.
 *
 * @author Hector Blanco
 */
public class Packer {
	
	// LOGGER
	private static final Logger LOGGER = LoggerFactory.getLogger(Packer.class);
	
	
	/**
	 * Reads the input file e calculate the packages, returning a String with the thing's indexes that will be
	 * in the package, separated by commas. Each "row" in the String (separated by System.lineSeparator) indicates
	 * a "package" in the input file
	 *
	 * @param filePath path of the input file
	 * @return The string that represents the optimized package
	 * @throws APIException in case of any error during package creation
	 */
	public static String pack(String filePath) throws APIException {
		LOGGER.debug("Input file path is: {}", filePath);
		return "TODO";
	}
	
}
