/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter.mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.openmrs.module.ghanaemr.adapter.IdentityVerificationResult;
import org.openmrs.module.ghanaemr.adapter.IdentityVerifier;
import org.openmrs.module.ghanaemr.adapter.IdentityVerifierContractTest;

public class MockIdentityVerifierTest extends IdentityVerifierContractTest {

	@Override
	protected IdentityVerifier newVerifier() {
		return new MockIdentityVerifier();
	}

	@Test
	public void verify_shouldAnswerUnverifiedForWellFormedPinBecauseNoNiaAccessExists() {
		IdentityVerificationResult result = newVerifier().verify("GHA-123456789-1");
		assertThat(result.getStatus(), equalTo(IdentityVerificationResult.Status.UNVERIFIED));
		assertThat(result.getSource(), equalTo(MockIdentityVerifier.SOURCE));
	}

	@Test
	public void verify_shouldNotValidateChecksumYet() {
		// Any well-formed check digit passes: checksum validation is deferred to WP-012.
		for (int digit = 0; digit <= 9; digit++) {
			IdentityVerificationResult result = newVerifier().verify("GHA-123456789-" + digit);
			assertThat(result.getStatus(), equalTo(IdentityVerificationResult.Status.UNVERIFIED));
		}
	}
}
