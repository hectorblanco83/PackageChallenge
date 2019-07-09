package com.mobiquityinc.packer;

import com.mobiquityinc.packer.exception.APIException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test class for @{@link Packer}
 *
 * @author Hector Blanco
 */
class PackerTest {
	
	@Test
	@DisplayName("First test method just for evaluate CI execution. SHOULD BE REMOVED!!")
	void assureCircleCIRunning() {
		try {
			assertEquals("TODO", Packer.pack(""));
		} catch(APIException e) {
			fail("No exception of type APIException was expected here");
		}
	}
	
}