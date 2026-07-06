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
import org.openmrs.module.ghanaemr.adapter.ClaimsSubmissionResult;
import org.openmrs.module.ghanaemr.adapter.ClaimsSubmitter;
import org.openmrs.module.ghanaemr.adapter.ClaimsSubmitterContractTest;

public class MockClaimsSubmitterTest extends ClaimsSubmitterContractTest {

	@Override
	protected ClaimsSubmitter newSubmitter() {
		return new MockClaimsSubmitter();
	}

	@Test
	public void submit_shouldBeUnsupportedUntilPhase4() {
		ClaimsSubmissionResult result = newSubmitter().submit("BATCH-2026-001");
		assertThat(result.getStatus(), equalTo(ClaimsSubmissionResult.Status.UNSUPPORTED));
	}
}
