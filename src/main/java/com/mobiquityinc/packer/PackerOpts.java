package com.mobiquityinc.packer;

import java.util.function.Consumer;


/**
 * A builder for options for the package creation.
 *
 * @author Hector Blanco
 */
public class PackerOpts {
	
	/*
	 * A class to allow modifications on the Packer and in the way it works, can be used for future grow of the
	 * library itself.
	 *
	 * The first need that comes to my mind is i18n, with this class an user of this library could set
	 * different currencies in the input file other than Euro
	 */
	
	// currency symbol that will be in the input file
	private String currencySymbol;
	
	
	/**
	 * Default constructor, private to force Builder.build
	 */
	private PackerOpts(Builder builder) {
		this.currencySymbol = builder.currencySymbol;
	}
	
	
	/**
	 * @return the currency symbol of this options
	 */
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	
	
	/**
	 * Utility method to get the default options for the Packer
	 *
	 * @return a Packer with the default options.
	 */
	public static PackerOpts defaultOptions() {
		//
		// Good case to demonstrate how to use this builder in a "Grails-like" way:
		// with(obj -> {
		// 		obj.attr1 = "this"
		// 		obj.attr2 = "that"
		// });
		//
		return new PackerOpts.Builder().with(opts -> opts.currencySymbol = "€").build();
	}
	
	
	/**
	 * Builder class
	 */
	public static class Builder {
		
		// sonar will complaint about that, but for readability of the with() method, I'll let this public
		public String currencySymbol;
		
		
		/**
		 * A consumer to avoid every getter and setter of this builder
		 *
		 * @param builderFunction the
		 * @return
		 */
		public Builder with(Consumer<Builder> builderFunction) {
			builderFunction.accept(this);
			return this;
		}
		
		
		public PackerOpts build() {
			return new PackerOpts(this);
		}
	}
	
}
