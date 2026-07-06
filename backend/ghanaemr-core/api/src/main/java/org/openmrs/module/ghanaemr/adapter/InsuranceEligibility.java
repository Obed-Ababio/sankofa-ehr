/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter;

/**
 * Adapter for NHIS insurance-membership eligibility checks (NHIA), per master plan section 8.
 * <p>
 * Contract (encoded in {@code InsuranceEligibilityContractTest}):
 * <ul>
 * <li>Never returns {@code null} and never throws for any input, including {@code null}.</li>
 * <li>Null or blank membership numbers yield {@link EligibilityResult.Status#INVALID}.</li>
 * <li>Every result carries a non-null human-readable message suitable for display at
 * check-in.</li>
 * </ul>
 * No NHIA API access exists today, so the only implementation is {@code MockInsuranceEligibility},
 * which always answers "unverified — capture manually". A real NHIA-backed implementation (Phase
 * 4+) must pass the same contract test.
 */
public interface InsuranceEligibility {

	/**
	 * Checks NHIS membership eligibility.
	 *
	 * @param nhisMembershipNumber the NHIS membership number, may be null
	 * @return a non-null eligibility result
	 */
	EligibilityResult checkEligibility(String nhisMembershipNumber);
}
