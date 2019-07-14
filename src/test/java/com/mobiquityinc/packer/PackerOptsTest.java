package com.mobiquityinc.packer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Tests for {@link PackerOpts} and it's builder
 */
class PackerOptsTest {
	
	@Test
	@DisplayName("defaultOptions: GIVEN default options THEN the currency symbol is €")
	void checkDefaultOptions() {
		String expectedSymbol = "€";
		assertEquals(expectedSymbol, PackerOpts.defaultOptions().getCurrencySymbol());
	}
	
	@Test
	@DisplayName("PackerOpts#Builder: GIVEN builder with custom currency THEN options must have the custom currency")
	void checkCustomOptions() {
		// given
		Consumer<PackerOpts.Builder> builderConsumer = obj -> obj.currencySymbol = "$";
		
		// when
		PackerOpts opts = new PackerOpts.Builder().with(builderConsumer).build();
		
		// then
		String expectedSymbol = "$";
		assertEquals(expectedSymbol, opts.getCurrencySymbol());
	}
	
	@Test
	@DisplayName("PackerOpts#Builder: GIVEN builder with multiple assignments THEN only the last assignment should be considered")
	void checkMultiplesAssignments() {
		// given
		Consumer<PackerOpts.Builder> builderConsumer = obj -> {
			obj.currencySymbol = "$";
			obj.currencySymbol = "R$";
		};
		
		// when
		PackerOpts opts = new PackerOpts.Builder().with(builderConsumer).build();
		
		// then
		String expectedSymbol = "R$";
		assertEquals(expectedSymbol, opts.getCurrencySymbol());
	}
	
}