/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter.mock;

import org.openmrs.module.ghanaemr.adapter.ClaimsSubmissionResult;
import org.openmrs.module.ghanaemr.adapter.ClaimsSubmitter;
import org.springframework.stereotype.Component;

/**
 * Placeholder {@link ClaimsSubmitter}: NHIS claims submission is Phase 4 (WP-070 research,
 * WP-071 file-export implementation). Until then every submission is refused as
 * {@link ClaimsSubmissionResult.Status#UNSUPPORTED}.
 */
@Component("ghanaemr.mockClaimsSubmitter")
public class MockClaimsSubmitter implements ClaimsSubmitter {

	@Override
	public ClaimsSubmissionResult submit(String claimsBatchReference) {
		if (claimsBatchReference == null || claimsBatchReference.trim().isEmpty()) {
			throw new IllegalArgumentException("claimsBatchReference must not be null or blank");
		}
		return ClaimsSubmissionResult.of(ClaimsSubmissionResult.Status.UNSUPPORTED,
		    "Claims submission not implemented until Phase 4 (WP-070/WP-071)", claimsBatchReference);
	}
}
