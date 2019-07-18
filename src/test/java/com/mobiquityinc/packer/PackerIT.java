package com.mobiquityinc.packer;

import com.mobiquityinc.packer.exception.APIException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Integration tests for {@link Packer} class
 *
 * @author Hector Blanco
 */
class PackerIT {
	
	private static final String INPUT_ASSIGNMENT_PATH = "./src/test/resources/assignment_input.txt";
	private static final String INPUT_NEGATIVE_VALUES = "./src/test/resources/input_negative_values.txt";
	private static final String INPUT_MAX_THINGS = "./src/test/resources/input_max_things.txt";
	private static final String INPUT_OVER_MAX_THINGS = "./src/test/resources/input_over_max_things.txt";
	private static final String INPUT_WITHOUT_WEIGHT = "./src/test/resources/input_without_weight.txt";
	private static final String INPUT_WITHOUT_THINGS_LIST = "./src/test/resources/input_without_things_list.txt";
	private static final String INPUT_WITHOUT_THINGS_LIST_2 = "./src/test/resources/input_without_things_list_2.txt";
	
	
	@Test
	@DisplayName("GIVEN assignment input THEN returns assignment expected output")
	void testAssignmentInput() {
		try {
			String expected =
					"4"+ System.lineSeparator() +
					"-" + System.lineSeparator() +
					"2,7" + System.lineSeparator() +
					"8,9";
			
			String packages = Packer.pack(INPUT_ASSIGNMENT_PATH);
			Assertions.assertEquals(expected, packages);
		} catch(APIException e) {
			Assertions.fail("Unexpected exception: " + e.getLocalizedMessage());
		}
	}
	
	
	@Test
	@DisplayName("GIVEN input with negative values THEN thrown APIException")
	void inputWithNegativeWeight() {
		// When reading a file with negative value of package weight
		APIException apiException = assertThrows(APIException.class, () -> Packer.pack(INPUT_NEGATIVE_VALUES));
		
		// Then packer should thrown an APIException
		assertEquals("Package weight is negative: \"-81\"", apiException.getMessage());
	}
	
	
	@Test
	@DisplayName("GIVEN input with maximum number of things THEN expected correct calculation")
	void inputMaxNumberOfThings() {
		try {
			String expected = "2,7";
			String packages = Packer.pack(INPUT_MAX_THINGS);
			Assertions.assertEquals(expected, packages);
		} catch(APIException e) {
			Assertions.fail("Unexpected exception: " + e.getLocalizedMessage());
		}
	}
	
	
	@Test
	@DisplayName("GIVEN input with more than maximum number of things THEN thrown APIException")
	void inputOverMaxNumberOfThings() {
		// When reading a file with more than 15 things
		APIException apiException = assertThrows(APIException.class, () -> Packer.pack(INPUT_OVER_MAX_THINGS));
		
		// Then packer should thrown an APIException
		assertEquals("Package 16.0 has more than 15 things to be chosen.", apiException.getMessage());
	}
	
	
	@Test
	@DisplayName("GIVEN input without package weight definition THEN thrown APIException")
	void inputWithoutPackageWeight() {
		// When reading a file without things definition
		APIException apiException = assertThrows(APIException.class, () -> Packer.pack(INPUT_WITHOUT_WEIGHT));
		
		// Then packer should thrown an APIException
		assertEquals("Incorrect input format in input line, expected format [packageWeight : thingsList]", apiException.getMessage());
	}
	
	
	@Test
	@DisplayName("GIVEN input without things list definition THEN thrown APIException")
	void inputWithoutThingsList() {
		// When reading a file without things definition
		APIException apiException = assertThrows(APIException.class, () -> Packer.pack(INPUT_WITHOUT_THINGS_LIST));
		
		// Then packer should thrown an APIException
		assertEquals("Incorrect input format in input line, expected format [packageWeight : thingsList]", apiException.getMessage());
		
		
		// When reading a file without things definition but with the ":" character
		apiException = assertThrows(APIException.class, () -> Packer.pack(INPUT_WITHOUT_THINGS_LIST_2));
		
		// Then packer should thrown an APIException
		assertEquals("Incorrect input format in input line, expected format [packageWeight : thingsList]", apiException.getMessage());
	}
	
	
}
