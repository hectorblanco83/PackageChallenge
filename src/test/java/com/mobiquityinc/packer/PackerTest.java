package com.mobiquityinc.packer;

import com.mobiquityinc.packer.exception.APIException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test class for @{@link Packer}
 *
 * @author Hector Blanco
 */
@ExtendWith(MockitoExtension.class)
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
	
	
	@Test
	@DisplayName("extractPackageWeightFromInput: GIVEN a valid package weight definition in string " +
			"THEN converts correctly to Double value")
	void extractPackageWeightFromInput() {
		// Given a definition of a package's max weigth
		String packageWeightDefinition = "10";
		
		
		try {
			// when extracting the weight from the string
			double packageWeight = new Packer(PackerOpts.defaultOptions()).extractPackageWeightFromInput(packageWeightDefinition);
			
			// then the weight must be the same as a Double.parse(definition)
			assertEquals(10, packageWeight);
		} catch(APIException e) {
			fail("No Exception expected here");
		}
	}
	
	
	@Test
	@DisplayName("extractPackageWeightFromInput: GIVEN a package weight definition in string that's not a number or a negative value " +
			"THEN throws APIException")
	void extractPackageWeightWithIncorrectValue() {
		Packer packer = new Packer(PackerOpts.defaultOptions());
		
		// Given a definition of a negative package's max weigth
		String negativeWeightDefinition = "-1";
		
		// when
		APIException exception = assertThrows(APIException.class,
				() -> packer.extractPackageWeightFromInput(negativeWeightDefinition));
		
		// then packer should've thrown an exception
		assertEquals("Package weight is negative: \"-1\"", exception.getMessage());
		
		
		// Given a definition of a package's max weigth that's not a number
		String alphanumericWeightDefinition = "1B";
		
		// when
		exception = assertThrows(APIException.class,
				() -> packer.extractPackageWeightFromInput(alphanumericWeightDefinition));
		
		// then packer should've thrown an exception
		assertEquals("Incorrect format for package weight: \"1B\"", exception.getMessage());
	}
	
	
	@Test
	@DisplayName("GIVEN an IO Exception reading the input file THEN throws APIException")
	void ioErrorReadingFile() {
		try {
			// Prepare for mocking file reading
			Packer packer = Mockito.spy(new Packer(PackerOpts.defaultOptions()));
			Mockito.doThrow(new IOException("Test")).when(packer).getFileLineIterator(Mockito.any(File.class));
			
			// Given a file path
			String filePath = "./src/test/resources/assignment_input.txt";
			
			// When packer tries to read this file
			APIException exception = assertThrows(APIException.class, () -> packer.createAllPackages(filePath));
			
			// Then it should thrown back an APIException
			assertEquals("Error reading input file", exception.getMessage());
		} catch(Exception e) {
			fail("No exception expected here");
		}
	}
}