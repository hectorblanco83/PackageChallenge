package com.mobiquityinc.packer;

import com.mobiquityinc.packer.entities.Thing;
import com.mobiquityinc.packer.exception.APIException;
import com.mobiquityinc.packer.exception.IncorrectThingDefinition;
import com.mobiquityinc.packer.utils.PackerUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
		
		
		// Must have a method to get the LineIterator instead of just using FileUtils.lineIterator, otherwise we cannot
		// mock the LineIterator itself in jUnit5 with Mockito.
		// PowerMock does not have integration with jUnit5 yet and Mockito cannot mock static methods or constructors.
		try(LineIterator it = getFileLineIterator(input)) {
			
			// create all packages while reading the file
			// read all the file first and then calculate the packages will need
			// to keep all packages in memory, and we don't know how long could be the input file
			// and how many packages could be inside a file, we could run out of memory even if its only text files
			
			// Because we are creating the packages while the file is open, we must try to do all operations the
			// fast as we can, to release the file as soon as possible
			while(it.hasNext()) {
				String line = it.nextLine();
				LOGGER.debug("Read input line: {}", line);
				
				
				// split.size != 2 thrown error
				String[] split = line.split(":");
				if(split.length != 2) {
					throw new APIException("Incorrect input format in input line, expected format [packageWeight : thingsList]");
				}
				
				// In case of NumberFormatException here, we will stop and throw an exception
				double packageWeight = extractPackageWeightFromInput(split[0]);
				
				// we will read a maximum of 15 things, matching the input (index, weight, cost)
				List<Thing> things = new ArrayList<>();
				Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
				Matcher matcher = pattern.matcher(line);
				while(matcher.find()) {
					if(things.size() >= 15) {
						throw new APIException(String.format("Package %s has more than 15 things to be chosen.", packageWeight));
					}
					String thingDefinition = matcher.group(1);
					LOGGER.debug("Founded thing definition: {}", thingDefinition);
					
					Thing thing = extractThingFromInput(thingDefinition);
					LOGGER.debug("Thing converted: {}", thing);
					
					things.add(thing);
				}
				
				sb.append(pkgSeparator).append(PackageBuilder.assemblePackage(things, packageWeight).orElse("-"));
				pkgSeparator = System.lineSeparator();
			}
			
		} catch(IOException e) {
			throw new APIException("Error reading input file", e);
		}
		
		return sb.toString();
	}
	
	
	double extractPackageWeightFromInput(String definition) throws APIException {
		try {
			double packageWeight = Double.parseDouble(definition.trim());
			LOGGER.debug("Package max weight: {}", packageWeight);
			if(packageWeight < 0) {
				throw new APIException("Package weight is negative: \"" + definition.trim() + "\"");
			}
			return packageWeight;
		} catch(NumberFormatException nfe) {
			throw new APIException(String.format("Incorrect format for package weight: \"%s\"", definition.trim()), nfe);
		}
	}
	
	
	/**
	 * Create a Thing object based on thing's definition in input. A thing definition is expected in the format
	 * {index, weight, cost} (without the brackets), where index is an integer, weight and cost are doubles and cost has
	 * the {@link PackerOpts#currencySymbol currency} symbol after it (For example 3.50€).
	 *
	 * @param definition the thing's string definition
	 * @return the converted Thing object
	 * @throws IncorrectThingDefinition If the thing's definition is incomplete
	 *                                  or one of the attributes are not in the expected format
	 */
	Thing extractThingFromInput(String definition) throws IncorrectThingDefinition {
		LOGGER.debug("Thing definition to convert: {}", definition);
		// we could use PackerOpts to define the Thing attributes' separator
		String[] atributes = definition.split(",");
		if(atributes.length != 3) {
			String error = String.format("Incorrect format for thing in input: %s", definition);
			throw new IncorrectThingDefinition(error);
		}
		
		int index;
		try {
			index = Integer.parseInt(atributes[0]);
			if(index < 0) {
				throw new IncorrectThingDefinition("Thing's index is negative: \"" + atributes[0].trim() + "\"");
			}
		} catch(NumberFormatException nfe) {
			throw new IncorrectThingDefinition("Incorrect format for thing's index: \"" + atributes[0].trim() + "\"", nfe);
		}
		
		
		double weight;
		try {
			weight = Double.valueOf(atributes[1]);
			if(weight < 0) {
				throw new IncorrectThingDefinition("Thing's weight is negative: \"" + atributes[1].trim() + "\"");
			}
		} catch(NumberFormatException nfe) {
			throw new IncorrectThingDefinition("Incorrect format for thing's weight: \"" + atributes[1].trim() + "\"", nfe);
		}
		
		double cost;
		try {
			String costWhithoutCurrency = atributes[2].replace(opts.getCurrencySymbol(), "");
			cost = Double.valueOf(costWhithoutCurrency);
			if(cost < 0) {
				throw new IncorrectThingDefinition("Thing's cost is negative: \"" + atributes[2].trim() + "\"");
			}
		} catch(NumberFormatException nfe) {
			throw new IncorrectThingDefinition("Incorrect format for thing's cost: \"" + atributes[2].trim() + "\"", nfe);
		}
		
		return new Thing(index, weight, cost);
	}
	
	
	/**
	 * Utility method to get a LineIterator over the input file.
	 *
	 * @param input the file to open for input, must not be {@code null}
	 * @return an Iterator of the lines in the file, never {@code null}
	 * @throws IOException in case of an I/O error
	 */
	LineIterator getFileLineIterator(File input) throws IOException {
		return FileUtils.lineIterator(input, StandardCharsets.UTF_8.name());
	}
	
}
