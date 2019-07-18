package com.mobiquityinc.packer;

import com.mobiquityinc.packer.entities.Thing;
import com.mobiquityinc.packer.entities.ThingsCombination;
import com.mobiquityinc.packer.exception.APIException;
import com.mobiquityinc.packer.utils.PackerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


/**
 * Builder of packages that choose the bet combination of things that fit inside the package and are the best choice
 * of cost and weight.
 *
 * @author Hector Blanco
 */
class PackageBuilder {
	
	// LOGGER
	private static final Logger LOGGER = LoggerFactory.getLogger(PackageBuilder.class);
	
	//
	private HashMap<String, Double> weightMap;
	private HashMap<String, Double> costMap;
	private List<String> combinations;
	
	
	/**
	 * Default constructor, initialise the support maps and combination list.
	 * Not intended to be used outside of this library.
	 */
	PackageBuilder() {
		weightMap = new HashMap<>();
		costMap = new HashMap<>();
		combinations = new ArrayList<>();
	}
	
	
	/**
	 * Assemble a package choosing the most optimized combination of things based on their weight and cost and
	 * the total weight of the package.
	 *
	 * @param things The available things from which to choose which ones will be used to assemble the package
	 * @param packageWeight The max weight that the package can take
	 * @return a combination of the thing's indexes that will compose the package
	 * @throws APIException In case of any error during package assemble
	 */
	static Optional<String> assemblePackage(List<Thing> things, double packageWeight) throws APIException {
		return new PackageBuilder().resolveBetterPackage(things, packageWeight);
	}
	
	
	/**
	 * Assemble a package choosing the most optimized combination of things based on their weight and cost and
	 * the total weight of the package.
	 *
	 * @param things The available things from which to choose which ones will be used to assemble the package
	 * @param packageWeight The max weight that the package can take
	 * @return a combination of the thing's indexes that will compose the package
	 * @throws APIException In case of any error during package assemble
	 */
	Optional<String> resolveBetterPackage(List<Thing> things, double packageWeight) throws APIException {
		// keep a reference to the better package created until now
		String betterPackage = null;
		
		/*
		 * This is based on a solution to the problem of create a powerset of a list. We need all possible
		 * combinations of thing's indexes to choose from. The total number of possible combinations is 2 to the power
		 * of N, where N is the total number of things.
		 *
		 * Instead of create an array solution with 2^n elements, and iterate over it creating every combination,
		 * I will start from the input, iterating over it and creating the possible, and acceptable, combination
		 * during iteration. At any iteration (max 15) k we will calculate the combinations adding the new Thing to
		 * the previously made combinations, iterating over them. The total number of combinations possible at any
		 * iteration k should be 2^k (as a powerset of k elements).
		 *
		 * I'll use two support list to keep cost and weight of the combinations at easy access instead of calculate
		 * them every time we need.
		 */
		for(Thing aThing : things) {
			
			// current number of combinations
			int nOfCurrentCombinations = combinations.size();
			LOGGER.debug("nOfCurrentCombinations: {}", nOfCurrentCombinations);
			
			for(int i = 0; i < nOfCurrentCombinations; i++) {
				// create new combination by appending the thing's index at the end of any combination and calculating
				// it's cost and weight
				ThingsCombination newCombination = createThingCombination(combinations.get(i), aThing);
				LOGGER.debug("newCombination created: {}", newCombination);
				
				// If new combination's weight is less then the max package weight, it's a combination that should be
				// considered
				if(newCombination.getWeight() <= packageWeight) {
					combinations.add(newCombination.getCombination());
					weightMap.put(newCombination.getCombination(), newCombination.getWeight());
					costMap.put(newCombination.getCombination(), newCombination.getCost());
					
					// check if the new combination it's a better fit for the package that the last "better fit" founded
					betterPackage = chooseBetterPackageCombination(betterPackage, newCombination);
					LOGGER.debug("betterPackage: {}", betterPackage);
				}
			}
			
			// After creating all combinations with the new thing that we are evaluating,
			// this thing by itself is a combination that should be evaluated
			LOGGER.debug("Evaluating thing by itself as combination: {}", aThing);
			if(aThing.getWeight() <= packageWeight) {
				combinations.add(aThing.getIndex().toString());
				weightMap.put(aThing.getIndex().toString(), aThing.getWeight());
				costMap.put(aThing.getIndex().toString(), aThing.getCost());
				
				// check if the new thing alone it's a better fit for the package that the last "better fit" founded.
				betterPackage = chooseBetterPackageCombination(betterPackage, aThing);
			}
		}
		
		// It's possible that none of the things alone or in combination is a fit for the package,
		// so return an Empty optional
		return Optional.ofNullable(betterPackage);
	}
	
	
	/**
	 * Combine an already made combination with a new thing to create a new combination
	 *
	 * @param aCombination an already made combination of things
	 * @param aThing a new thing to combine with the other combination
	 * @return a new combination that is composed of the combination in input with the new thing
	 * @throws APIException if no combination in input is <code>NULL</code>, empty or blank
	 */
	ThingsCombination createThingCombination(String aCombination, Thing aThing) throws APIException {
		if(PackerUtils.isStringEmptyOrNull(aCombination)) {
			throw new APIException("Current combination is null or empty");
		}
		
		Double aCombinationWeight = Optional.ofNullable(weightMap.get(aCombination)).orElseThrow(() -> new APIException("Current combination has no weight"));
		Double aCombinationCost = Optional.ofNullable(costMap.get(aCombination)).orElseThrow(() -> new APIException("Current combination has no cost"));
		
		String newCombination = aCombination + "," + aThing.getIndex();
		Double newCombinationWeight = aCombinationWeight + aThing.getWeight();
		Double newCombinationCost = aCombinationCost + aThing.getCost();
		return new ThingsCombination(newCombination, newCombinationWeight, newCombinationCost);
	}
	
	
	/**
	 * Choose between a package and a combination of things which one is the best composition based on
	 * theirs weight and cost.
	 *
	 * @param aPackage a combination of things' indexes separated by a comma
	 * @param aCombination a ThingsCombination to compare with the package in input
	 * @return a string with the things' indexes which is the better choice to assemble an optimal package
	 */
	private String chooseBetterPackageCombination(String aPackage, ThingsCombination aCombination) {
		LOGGER.debug("choosing better combination between {} and {}", aPackage, aCombination);
		
		// no package combination can have a negative cost, so we will use a default of -1 if
		// aPackage is NULL or empty [costMap will not have the key]
		Double aPackageCost = Optional.ofNullable(costMap.get(aPackage)).orElse(-1D);
		LOGGER.debug("aPackageCost: {} ", aPackageCost);
		
		// if the new combination cost more than the already chosen package,
		// we should choose the new combination as the better choice
		if(aCombination.getCost() > aPackageCost) {
			LOGGER.debug("aCombination.cost [{}] > aPackage.cost [{}]", aCombination.getCost(), aPackageCost);
			return aCombination.getCombination();
		}
		
		// otherwise, if the new combination and the a current better combination cost the same
		// but the new combination weighs less, we should get the new combination as a better choice
		Double aPackageWeight = weightMap.get(aPackage);
		if(aCombination.getCost().equals(aPackageCost) && aCombination.getWeight() < aPackageWeight) {
			LOGGER.debug("aCombination.weight [{}] < aPackage.weight [{}]", aCombination.getWeight(), aPackageWeight);
			return aCombination.getCombination();
		}
		
		// otherwise, chosen package cost more or weighs less, and we should keep it as the better choice
		LOGGER.trace("aPackage is a better choice");
		return aPackage;
	}
	
	
	/**
	 * Choose the better package possibility between the already chosen package and a new thing, based on cost and weight.
	 *
	 * @param aPackage a combination of things that is already chosen as the better package possibility
	 * @param aThing    a new thing that could be a better choice than the current aPackage
	 * @return the combination of the things' indexes that is a better choice for compose a package
	 */
	String chooseBetterPackageCombination(String aPackage, Thing aThing) {
		ThingsCombination combination = new ThingsCombination(aThing.getIndex().toString(), aThing.getWeight(), aThing.getCost());
		return chooseBetterPackageCombination(aPackage, combination);
	}
	
	
	
	//
	// Getters with PACKAGE visibility to allow manipulation during tests
	//
	
	
	HashMap<String, Double> getWeightMap() {
		return weightMap;
	}
	
	
	HashMap<String, Double> getCostMap() {
		return costMap;
	}
	
	
	List<String> getCombinations() {
		return combinations;
	}
	
}
