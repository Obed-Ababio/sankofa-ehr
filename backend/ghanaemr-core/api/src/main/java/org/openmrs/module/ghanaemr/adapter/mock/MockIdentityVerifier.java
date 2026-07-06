/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter.mock;

import java.util.regex.Pattern;

import org.openmrs.module.ghanaemr.adapter.IdentityVerificationResult;
import org.openmrs.module.ghanaemr.adapter.IdentityVerifier;
import org.springframework.stereotype.Component;

/**
 * Mock {@link IdentityVerifier}: validates the Ghana Card PIN format only and always answers
 * {@link IdentityVerificationResult.Status#UNVERIFIED} for well-formed PINs, because no NIA
 * verification API access exists yet (master plan D9/section 8).
 */
@Component("ghanaemr.mockIdentityVerifier")
public class MockIdentityVerifier implements IdentityVerifier {

	public static final String SOURCE = "mock";

	/** Ghana Card PIN format per master plan section 6.1: GHA-#########-#. */
	public static final Pattern GHANA_CARD_PIN_PATTERN = Pattern.compile("^GHA-\\d{9}-\\d$");

	// TODO(WP-012): implement the NIA check-digit (checksum) validation once the algorithm is
	// confirmed against NIA documentation, gated behind the ghanaemr.ghanacard.checksum config
	// flag (see GhanaemrConfig). Format-only until then.

	@Override
	public IdentityVerificationResult verify(String ghanaCardPin) {
		if (ghanaCardPin == null || !GHANA_CARD_PIN_PATTERN.matcher(ghanaCardPin).matches()) {
			return IdentityVerificationResult.of(IdentityVerificationResult.Status.INVALID_FORMAT,
			    "Ghana Card PIN must match GHA-#########-#", SOURCE);
		}
		return IdentityVerificationResult.of(IdentityVerificationResult.Status.UNVERIFIED,
		    "Format valid; NIA online verification not available — identity unverified", SOURCE);
	}
}
