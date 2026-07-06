/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Contract every {@link InsuranceEligibility} implementation must honor. Implementation test
 * classes extend this and supply their instance via {@link #newChecker()}; a real NHIA-backed
 * adapter (Phase 4+) reuses this class unchanged.
 */
public abstract class InsuranceEligibilityContractTest {

	protected abstract InsuranceEligibility newChecker();

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { "   " })
	public void checkEligibility_shouldReturnInvalidForNullOrBlank(String membershipNumber) {
		EligibilityResult result = newChecker().checkEligibility(membershipNumber);
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), equalTo(EligibilityResult.Status.INVALID));
		assertThat(result.getMessage(), notNullValue());
	}

	@Test
	public void checkEligibility_shouldReturnCompleteResultForMembershipNumber() {
		EligibilityResult result = newChecker().checkEligibility("12345678");
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), notNullValue());
		assertThat(result.getMessage(), notNullValue());
		assertThat(result.getSource(), notNullValue());
	}
}
