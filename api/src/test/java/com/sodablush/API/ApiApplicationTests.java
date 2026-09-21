package com.sodablush.api;

import org.junit.jupiter.api.Test;

/**
 * El contexto completo necesita Neon + un issuer Auth0 real.
 * Los tests de negocio estan en service/*Test y HealthSecurityTest.
 */
class ApiApplicationTests {

	@Test
	void applicationClassExists() {
		org.junit.jupiter.api.Assertions.assertNotNull(ApiApplication.class);
	}

}
