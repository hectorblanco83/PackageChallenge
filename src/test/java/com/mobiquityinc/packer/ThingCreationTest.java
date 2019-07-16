package com.mobiquityinc.packer;


import com.mobiquityinc.packer.entities.Thing;
import com.mobiquityinc.packer.exception.APIException;
import com.mobiquityinc.packer.exception.IncorrectThingDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests for creation of {@link com.mobiquityinc.packer.entities.Thing} objects on {@link Packer} algorithm
 *
 * @author Hector Blanco
 */
class ThingCreationTest {
	
	@Test
	@DisplayName("GIVEN things' string definition THEN convert to a Thing object")
	void checkThingCreation() {
		try {
			Thing expectedThing = new Thing(2, 88.62, 98D);
			
			// given
			String thingDefinition = "2,88.62,€98";
			
			//when
			Thing thing = new Packer(PackerOpts.defaultOptions()).extractThingFromInput(thingDefinition);
			
			// then
			assertEquals(expectedThing, thing);
		} catch(APIException e) {
			fail("Unexpected APIException: " + e.getLocalizedMessage());
		}
	}
	
	
	@Test
	@DisplayName("GIVEN thign definition with anything else then numbers THEN expect IncorrectThingDefinition")
	void checkWrongThingCreation() {
		IncorrectThingDefinition nfException;
		
		// given
		String wrongCost = "2,88.62,€98A";
		String wrongWeight = "2,88.62A,€98";
		String wrongIndex = "2A,88.62,€98";
		Packer packer = new Packer(PackerOpts.defaultOptions());
		
		//when
		nfException = assertThrows(IncorrectThingDefinition.class, () -> packer.extractThingFromInput(wrongCost));
		//then
		assertEquals("Incorrect format for thing's cost: \"€98A\"", nfException.getMessage());
		
		//when
		nfException = assertThrows(IncorrectThingDefinition.class, () -> packer.extractThingFromInput(wrongWeight));
		//then
		assertEquals("Incorrect format for thing's weight: \"88.62A\"", nfException.getMessage());
		
		//when
		nfException = assertThrows(IncorrectThingDefinition.class, () -> packer.extractThingFromInput(wrongIndex));
		//then
		assertEquals("Incorrect format for thing's index: \"2A\"", nfException.getMessage());
	}
	
	
	@Test
	@DisplayName("GIVEN incorrect currency symbol THEN expect IncorrectThingDefinition on cost parsing")
	void checkThingCreationWithWrongCurrency() {
		// given
		String thingDefinition = "2,88.62,€98";
		Packer dollarPacker = new Packer(new PackerOpts.Builder().with(opts -> opts.currencySymbol = "$").build());
		
		// when
		IncorrectThingDefinition nfException = assertThrows(IncorrectThingDefinition.class, () -> dollarPacker.extractThingFromInput(thingDefinition));
		
		// then
		assertEquals("Incorrect format for thing's cost: \"€98\"", nfException.getMessage());
	}
	
	
	@Test
	@DisplayName("GIVEN incomplete thing's definition THEN throws IncorrectThingDefinition")
	void checkThingCreationWithoutOneAtribute() {
		// given
		String thingDefinition = "2,88.62";
		Packer packer = new Packer(PackerOpts.defaultOptions());
		
		// when
		IncorrectThingDefinition nfException = assertThrows(IncorrectThingDefinition.class, () -> packer.extractThingFromInput(thingDefinition));
		
		// then
		assertEquals("Incorrect format for thing in input: 2,88.62", nfException.getMessage());
	}
	
	
	@Test
	@DisplayName("GIVEN thing definition with negative values THEN throws IncorrectThingDefinition")
	void checkThingCreationWithoutNegativeValues() {
		IncorrectThingDefinition nfException;
		
		// given
		String wrongCost = "2, 88.62, €-98";
		String wrongWeight = "2, -88.62, €98";
		String wrongIndex = "-2, 88.62, €98";
		Packer packer = new Packer(PackerOpts.defaultOptions());
		
		//when
		nfException = assertThrows(IncorrectThingDefinition.class, () -> packer.extractThingFromInput(wrongCost));
		//then
		assertEquals("Thing's cost is negative: \"€-98\"", nfException.getMessage());
		
		//when
		nfException = assertThrows(IncorrectThingDefinition.class, () -> packer.extractThingFromInput(wrongWeight));
		//then
		assertEquals("Thing's weight is negative: \"-88.62\"", nfException.getMessage());
		
		//when
		nfException = assertThrows(IncorrectThingDefinition.class, () -> packer.extractThingFromInput(wrongIndex));
		//then
		assertEquals("Thing's index is negative: \"-2\"", nfException.getMessage());
	}
	
}
