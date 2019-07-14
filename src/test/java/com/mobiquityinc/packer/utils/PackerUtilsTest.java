package com.mobiquityinc.packer.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Tests for {@link PackerUtils}
 *
 * @author Hector Blanco
 */
class PackerUtilsTest {
	
	@Test
	@DisplayName("isStringEmptyOrNull: GIVEN null String THEN returns true")
	void checkNull() {
		assertTrue(PackerUtils.isStringEmptyOrNull(null));
	}
	
	@Test
	@DisplayName("isStringEmptyOrNull: GIVEN empty String THEN returns true")
	void checkEmpty() {
		assertTrue(PackerUtils.isStringEmptyOrNull(""));
	}
	
	@Test
	@DisplayName("isStringEmptyOrNull: GIVEN string with only whitespaces THEN returns true")
	void checkWhite() {
		assertTrue(PackerUtils.isStringEmptyOrNull("   "));
	}
	
	@Test
	@DisplayName("isStringEmptyOrNull: GIVEN string with at least one character that is not whitespace THEN returns false")
	void checkNotWhite() {
		assertFalse(PackerUtils.isStringEmptyOrNull(" a  b  "));
	}
	
}