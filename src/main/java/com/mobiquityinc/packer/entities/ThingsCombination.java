package com.mobiquityinc.packer.entities;

import java.util.Objects;


/**
 * A combination of Things that could be sent in a package.
 *
 * @author Hector Blanco
 */
public class ThingsCombination {
	
	private String combination;
	private Double weight;
	private Double cost;
	
	
	public ThingsCombination(String combination, Double weight, Double cost) {
		this.combination = combination;
		this.weight = weight;
		this.cost = cost;
	}
	
	
	public String getCombination() {
		return combination;
	}
	
	
	public Double getWeight() {
		return weight;
	}
	
	
	public Double getCost() {
		return cost;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		ThingsCombination other = (ThingsCombination) o;
		
		// things are equals if they have the same combination, weight and cost
		return getCombination().equals(other.getCombination())
				&& getWeight().equals(other.getWeight())
				&& getCost().equals(other.getCost());
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(getCombination(), getWeight(), getCost());
	}
	
	
	@Override
	public String toString() {
		return "[" + combination + ", " + weight + ", " + cost + "]";
	}
}
