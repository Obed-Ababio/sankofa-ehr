/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Contract every {@link IdentityVerifier} implementation must honor. Implementation test classes
 * extend this and supply their instance via {@link #newVerifier()}; future real adapters (e.g. an
 * NIA-backed verifier, Phase 5) reuse this class unchanged.
 */
public abstract class IdentityVerifierContractTest {

	protected abstract IdentityVerifier newVerifier();

	@Test
	public void verify_shouldReturnInvalidFormatForNull() {
		IdentityVerificationResult result = newVerifier().verify(null);
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(IdentityVerificationResult.Status.INVALID_FORMAT));
		assertThat(result.getMessage(), notNullValue());
	}

	@ParameterizedTest
	@ValueSource(strings = { "", " ", "GHA123456789", "GHA-12345678-1", "GHA-1234567890-1", "GHA-123456789-",
	        "GHA-123456789-12", "gha-123456789-1", "GH-123456789-1", " GHA-123456789-1", "GHA-123456789-1 ",
	        "GHA-12345678A-1" })
	public void verify_shouldReturnInvalidFormatForMalformedPins(String pin) {
		IdentityVerificationResult result = newVerifier().verify(pin);
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(IdentityVerificationResult.Status.INVALID_FORMAT));
		assertThat(result.getMessage(), notNullValue());
	}

	@ParameterizedTest
	@ValueSource(strings = { "GHA-123456789-1", "GHA-000000000-0", "GHA-987654321-9" })
	public void verify_shouldNeverReportInvalidFormatForWellFormedPins(String pin) {
		IdentityVerificationResult result = newVerifier().verify(pin);
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), anyOf(equalTo(IdentityVerificationResult.Status.VERIFIED),
		    equalTo(IdentityVerificationResult.Status.UNVERIFIED)));
		assertThat(result.getMessage(), notNullValue());
		assertThat(result.getSource(), notNullValue());
	}
}
