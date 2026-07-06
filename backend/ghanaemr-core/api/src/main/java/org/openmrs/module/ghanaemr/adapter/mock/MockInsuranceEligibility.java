/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter.mock;

import org.openmrs.module.ghanaemr.adapter.EligibilityResult;
import org.openmrs.module.ghanaemr.adapter.InsuranceEligibility;
import org.springframework.stereotype.Component;

/**
 * Mock {@link InsuranceEligibility}: no NHIA membership API access exists, so every check answers
 * "unverified — capture manually" (master plan section 8). Front-desk staff record eligibility
 * evidence (card sighting) by hand until a real NHIA integration lands.
 */
@Component("ghanaemr.mockInsuranceEligibility")
public class MockInsuranceEligibility implements InsuranceEligibility {

	public static final String SOURCE = "mock";

	@Override
	public EligibilityResult checkEligibility(String nhisMembershipNumber) {
		if (nhisMembershipNumber == null || nhisMembershipNumber.trim().isEmpty()) {
			return EligibilityResult.of(EligibilityResult.Status.INVALID, "No NHIS membership number provided", SOURCE);
		}
		return EligibilityResult.of(EligibilityResult.Status.UNVERIFIED, "Unverified — capture manually", SOURCE);
	}
}
