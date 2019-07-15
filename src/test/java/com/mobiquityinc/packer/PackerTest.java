package com.mobiquityinc.packer;

import com.mobiquityinc.packer.exception.APIException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Test class for @{@link Packer}
 *
 * @author Hector Blanco
 */
class PackerTest {
	
	@Test
	@DisplayName("pack: GIVEN null, empty or blank filepath THEN thrown APIException")
	void checkWhenFilePathIsNullEmptyBlank() {
		String expectedErrorMessage = "File path cannot be null or empty";
		
		APIException apiException;
		apiException = assertThrows(APIException.class, () -> Packer.pack(null));
		assertEquals(expectedErrorMessage, apiException.getMessage());
		
		apiException = assertThrows(APIException.class, () -> Packer.pack(""));
		assertEquals(expectedErrorMessage, apiException.getMessage());
		
		apiException = assertThrows(APIException.class, () -> Packer.pack("   "));
		assertEquals(expectedErrorMessage, apiException.getMessage());
	}
	
	
	@Test
	@DisplayName("pack: GIVEN input file that doesn't exists THEN thrown APIException")
	void checkWhenFileDoesNotExist() {
		String expectedErrorMessage = "File C:/input.txt not found";
		
		APIException apiException;
		apiException = assertThrows(APIException.class, () -> Packer.pack("C:/input.txt"));
		assertEquals(expectedErrorMessage, apiException.getMessage());
	}
	
}