package com.mobiquityinc.packer;

import com.mobiquityinc.packer.entities.Thing;
import com.mobiquityinc.packer.entities.ThingsCombination;
import com.mobiquityinc.packer.exception.APIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test class for @{@link PackageBuilder}.
 *
 * @author Hector Blanco
 */
class PackageBuilderTest {
	
	// the builder to test
	private PackageBuilder builder;
	
	
	@BeforeEach
	void setUp() {
		builder = new PackageBuilder();
	}
	
	
	/**
	 * Build a package in the builder for the tests
	 */
	private String configurePackage() {
		String aPackage = "1,2";
		builder.getCombinations().add(aPackage);
		builder.getWeightMap().put(aPackage, 50D);
		builder.getCostMap().put(aPackage, 10D);
		return aPackage;
	}
	
	
	@Test
	@DisplayName("chooseBetterPackageCombination: GIVEN a package that weighs less and cost more than a thing " +
			"THEN return the package as a better package composition")
	void betterPackageThanThingWhenPackageCostMoreAndWeighsLess() {
		
		// Given an already chosen package possibility [1,2 / 50Kg / 10€]
		String alreadyChosenPackage = configurePackage();
		
		// Given a thing that weighs more and cost less than the already chosen package
		Thing thing = new Thing(3, 60D, 5D);
		
		// When check which one is the best package possibility
		String betterPackage = builder.chooseBetterPackageCombination(alreadyChosenPackage, thing);
		
		// then the better package should be the already chosen combination
		assertEquals("1,2", betterPackage);
	}
	
	
	@Test
	@DisplayName("chooseBetterPackageCombination: GIVEN a package that weighs less then a thing and cost the same " +
			"THEN return the package as a better package composition")
	void betterPackageThanThingWhenPackageCostEqualsAndWeighsLess() {
		
		// Given an already chosen package possibility [1,2 / 50Kg / 10€]
		String alreadyChosenPackage = configurePackage();
		
		// Given a thing that weighs more than the package and cost the same
		Thing thing = new Thing(3, 60D, 10D);
		
		// When check which one is the best package possibility
		String betterPackage = builder.chooseBetterPackageCombination(alreadyChosenPackage, thing);
		
		// then the better package should be the already chosen combination
		assertEquals("1,2", betterPackage);
	}
	
	
	@Test
	@DisplayName("chooseBetterPackageCombination: GIVEN a package that weighs more then a thing and cost the same " +
			"THEN return the thing as a better package composition")
	void betterPackageThanThingWhenPackageCostEqualsAndWeighsMore() {
		
		// Given an already chosen package possibility [1,2 / 50Kg / 10€]
		String alreadyChosenPackage = configurePackage();
		
		// Given a thing that weighs more and cost less than the already chosen package
		Thing thing = new Thing(3, 40D, 10D);
		
		// When check which one is the best package possibility
		String betterPackage = builder.chooseBetterPackageCombination(alreadyChosenPackage, thing);
		
		// then the better package should be the already chosen combination
		assertEquals("3", betterPackage);
	}
	
	
	@Test
	@DisplayName("createThingCombination: GIVEN a combination of things and a new thing " +
			"THEN return a new combination of them both with it's cost and weight as the sum of the previous two")
	void createThingCombination() {
		// Given a combination of things
		String aCombination = configurePackage();
		
		// Given a new thing
		Thing aNewThing = new Thing(3, 10D, 15D);
		
		try {
			// When create a new combination
			ThingsCombination newCombination = builder.createThingCombination(aCombination, aNewThing);
			
			// Then new combination should be [ 1,2,3 / 60Kg / 25€ ]
			assertEquals("1,2,3", newCombination.getCombination());
			assertEquals(Double.valueOf(60), newCombination.getWeight());
			assertEquals(Double.valueOf(25), newCombination.getCost());
		} catch(APIException e) {
			fail("ApiException not expected here");
		}
	}
	
	
	@DisplayName("createThingCombination: GIVEN a new thing without any previous combination created " +
			"THEN expect APIException on thing's combination")
	@ParameterizedTest
	@NullAndEmptySource
	void createThingCombinationWhenNoPreviousCombinationExists(String aCombination) {
		// Given Null and Empty combinations
		// String aCombination
		
		// Given a new thing
		Thing aNewThing = new Thing(3, 10D, 15D);
		
		// When create a new combination
		APIException apiException = assertThrows(APIException.class,
				() -> builder.createThingCombination(aCombination, aNewThing));
		
		// Then error should be thrown by thing's combination
		assertEquals("Current combination is null or empty", apiException.getMessage());
	}
	
	
	@DisplayName("createThingCombination: GIVEN a new thing without any previous combination created " +
			"THEN expect APIException on thing's combination")
	@Test
	void createThingCombinationWhenPreviousCombinationLacksCostOrWeight() {
		// Given a combination without weight
		String aCombination = "1,2";
		
		// Given a new thing
		Thing aNewThing = new Thing(3, 10D, 15D);
		
		// When create a new combination
		APIException apiException = assertThrows(APIException.class,
				() -> builder.createThingCombination(aCombination, aNewThing));
		
		// Then error should be thrown by thing's combination
		assertEquals("Current combination has no weight", apiException.getMessage());
		
		
		
		// Given a combination with weight but without cost
		builder.getWeightMap().put(aCombination, 10D);
		
		// When create a new combination
		apiException = assertThrows(APIException.class,
				() -> builder.createThingCombination(aCombination, aNewThing));
		
		// Then error should be thrown by thing's combination
		assertEquals("Current combination has no cost", apiException.getMessage());
		
	}
	
	
	@Test
	@DisplayName("GIVEN a list of things with only one thing which weighs less than the package " +
			"THEN chose this thing as the better things combination for the package")
	void resolveBetterPackageWithOnlyOneThingThatSuits() {
		// given
		List<Thing> things = new ArrayList<>();
		things.add(new Thing(1, 10D, 10D));
		things.add(new Thing(2, 15D, 500D));
		
		// given
		double packageMaxWeight = 10D;
		
		try {
			Optional<String> betterPackage = builder.resolveBetterPackage(things, packageMaxWeight);
			assertEquals("1", betterPackage.orElseGet(() -> fail("Should had chosen the combination [1]")));
		} catch(APIException e) {
			fail("No exception expected in this test");
		}
	}
	
	
	@Test
	@DisplayName("GIVEN a list of things with the better combination resting last on the list " +
			"THEN chose the last two things as the better things combination for the package")
	void resolveBetterPackageWithMultiplePossibilities() {
		// given
		List<Thing> things = new ArrayList<>();
		things.add(new Thing(1, 10D, 10D));
		things.add(new Thing(2, 15D, 500D));
		things.add(new Thing(3, 5D, 20D));
		things.add(new Thing(4, 5D, 20D));
		
		// given
		double packageMaxWeight = 10D;
		
		try {
			Optional<String> betterPackage = builder.resolveBetterPackage(things, packageMaxWeight);
			assertEquals("3,4", betterPackage.orElseGet(() -> fail("Should had chosen the combination [3,4]")));
		} catch(APIException e) {
			fail("No exception expected in this test");
		}
	}
	
	
	@Test
	@DisplayName("GIVEN a list of things with no choice that suits the package weight " +
			"THEN return an Optional.empty")
	void resolveBetterPackageWithNoPossibilities() {
		// given
		List<Thing> things = new ArrayList<>();
		things.add(new Thing(1, 10D, 10D));
		things.add(new Thing(2, 15D, 500D));
		things.add(new Thing(3, 5D, 20D));
		things.add(new Thing(4, 5D, 20D));
		
		// given
		double packageMaxWeight = 1D;
		
		try {
			Optional<String> betterPackage = builder.resolveBetterPackage(things, packageMaxWeight);
			assertEquals(Optional.empty(), betterPackage);
		} catch(APIException e) {
			fail("No exception expected in this test");
		}
	}
	
	
}