/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter;

/**
 * Adapter for national identity (Ghana Card / NIA) verification, per master plan section 8.
 * <p>
 * Contract (encoded in {@code IdentityVerifierContractTest}):
 * <ul>
 * <li>Never returns {@code null} and never throws for any input, including {@code null}.</li>
 * <li>Input that does not match the Ghana Card PIN format {@code ^GHA-\d{9}-\d$} yields
 * {@link IdentityVerificationResult.Status#INVALID_FORMAT} without any remote call.</li>
 * <li>Well-formed input yields {@code VERIFIED} or {@code UNVERIFIED}, never
 * {@code INVALID_FORMAT}.</li>
 * </ul>
 * The only implementation today is {@code MockIdentityVerifier} (no NIA API access exists yet —
 * D9). A real NIA-backed implementation is a Phase 5 opportunity and must pass the same contract
 * test.
 */
public interface IdentityVerifier {

	/**
	 * Verifies a Ghana Card PIN (format {@code GHA-#########-#}).
	 *
	 * @param ghanaCardPin the PIN to verify, may be null
	 * @return a non-null verification result
	 */
	IdentityVerificationResult verify(String ghanaCardPin);
}
