package com.mobiquityinc.packer.utils;

/**
 * Utilities methods for the library.
 *
 * @author Hector Blanco
 */
public class PackerUtils {
	
	/**
	 * Return <code>true</code> if, and only if, the string in input is null, empty or all of it's characters
	 * are whitespaces. Return <code>false</code> otherwise.
	 *
	 * @param str the string to check
	 * @return a boolean that indicates if the string in input is empty or null
	 */
	public static boolean isStringEmptyOrNull(String str) {
		// We could achieve the same with Apache Commons StringUtils#isEmpty() but I prefer not
		// to introduce another dependency for just a check against empty/blank strings
		return str == null || str.length() == 0 || str.chars().allMatch(Character::isWhitespace);
	}
	
}
