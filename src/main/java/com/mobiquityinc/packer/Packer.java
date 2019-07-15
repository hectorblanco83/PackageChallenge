package com.mobiquityinc.packer;

import com.mobiquityinc.packer.exception.APIException;
import com.mobiquityinc.packer.utils.PackerUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


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
		return new Packer(PackerOpts.defaultOptions()).createAllPackages(filePath);
	}
	
	
	/**
	 * Reads the input file e calculate the packages, returning a String with the thing's indexes that will be
	 * in the package, separated by commas. Each "row" in the String (separated by System.lineSeparator) indicates
	 * a "package" in the input file
	 *
	 * @param filePath path of the input file
	 * @param options  the {@link PackerOpts options} to this packer
	 * @return The string that represents the optimized package
	 * @throws APIException in case of any error during package creation
	 */
	public static String pack(String filePath, PackerOpts options) throws APIException {
		return new Packer(options).createAllPackages(filePath);
	}
	
	
	// this packer's options
	private PackerOpts opts;
	
	
	/**
	 * Default constructor, package visibility to disable directly
	 * usage but let it remain visible fot tests
	 *
	 * @param opts The options that this Packer should use
	 */
	Packer(PackerOpts opts) {
		this.opts = opts;
	}
	
	
	/**
	 * Read the input file, and based on the "things" inside it, calculate the optimal package.
	 */
	String createAllPackages(String filePath) throws APIException {
		LOGGER.debug("Input file path is: {}", filePath);
		
		// check filepath
		if(PackerUtils.isStringEmptyOrNull(filePath)) {
			throw new APIException("File path cannot be null or empty");
		}
		
		// find input file and thrown an error if the file doesn't exists
		File input = new File(filePath);
		if(!input.exists()) {
			throw new APIException(String.format("File %s not found", filePath));
		}
		
		// build the response, could put all strings in a list and join through stream at the end of the method,
		// but I will build a string while iterating over the file to not have to iterate over a list that can be
		// very long if the input file has a lot of packages
		StringBuilder sb = new StringBuilder();
		
		// create a line separator, empty at the first cycle and after the first we will assign the System.lineSeparator
		// to this separator and append this separator to the stingBuilder before a new package
		// creating something like sb.append("").append(package).append("\n").append(package)
		String pkgSeparator = "";
		
		
		// read file line by line
		try(LineIterator it = FileUtils.lineIterator(input)) {
			
			// create all packages while reading the file
			// read all the file first and then calculate the packages will need
			// to keep all packages in memory, and we don't know how long could be the input file
			// and how many packages could be inside a file, we could run out of memory even if its only text files
			
			// Because we are creating the packages while the file is open, we must try to do all operations the
			// fast as we can, to release the file as soon as possible
			while(it.hasNext()) {
				// TODO:
				String line = it.nextLine();
				LOGGER.debug("Read input line: {}", line);
			}
			
		} catch(IOException e) {
			throw new APIException("Error reading input file", e);
		}
		
		return sb.toString();
	}
	
	
	
}
