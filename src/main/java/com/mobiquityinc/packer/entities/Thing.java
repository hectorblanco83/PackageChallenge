package com.mobiquityinc.packer.entities;

import java.util.Objects;


/**
 * The things read from the input file and that goes inside the packages.
 *
 * @author Hector Blanco
 */
public class Thing {
	
	private Integer index;
	private Double weight;
	private Double cost;
	
	
	public Thing(Integer index, Double weight, Double cost) {
		this.index = index;
		this.weight = weight;
		this.cost = cost;
	}
	
	
	public Integer getIndex() {
		return index;
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
		Thing other = (Thing) o;
		
		// things are equals if they have the same index, weight and cost
		return getIndex().equals(other.getIndex())
				&& getWeight().equals(other.getWeight())
				&& getCost().equals(other.getCost());
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(getIndex(), getWeight(), getCost());
	}
	
	
	@Override
	public String toString() {
		return "[" + index + ", " + weight + ", " + cost + "]";
	}
}
