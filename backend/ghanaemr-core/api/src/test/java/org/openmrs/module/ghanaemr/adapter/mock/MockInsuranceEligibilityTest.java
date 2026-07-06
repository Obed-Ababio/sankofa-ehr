/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter.mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.openmrs.module.ghanaemr.adapter.EligibilityResult;
import org.openmrs.module.ghanaemr.adapter.InsuranceEligibility;
import org.openmrs.module.ghanaemr.adapter.InsuranceEligibilityContractTest;

public class MockInsuranceEligibilityTest extends InsuranceEligibilityContractTest {

	@Override
	protected InsuranceEligibility newChecker() {
		return new MockInsuranceEligibility();
	}

	@Test
	public void checkEligibility_shouldAnswerUnverifiedCaptureManually() {
		EligibilityResult result = newChecker().checkEligibility("12345678");
		assertThat(result.getStatus(), equalTo(EligibilityResult.Status.UNVERIFIED));
		assertThat(result.getMessage(), containsStringIgnoringCase("capture manually"));
		assertThat(result.getSource(), equalTo(MockInsuranceEligibility.SOURCE));
	}
}
