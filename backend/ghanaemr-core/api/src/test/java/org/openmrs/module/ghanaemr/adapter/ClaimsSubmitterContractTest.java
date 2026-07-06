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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Contract every {@link ClaimsSubmitter} implementation must honor. Implementation test classes
 * extend this and supply their instance via {@link #newSubmitter()}; the Phase 4 file-export and
 * any later API implementations (WP-070/071) reuse this class unchanged.
 */
public abstract class ClaimsSubmitterContractTest {

	protected abstract ClaimsSubmitter newSubmitter();

	@Test
	public void submit_shouldRejectNullBatchReference() {
		assertThrows(IllegalArgumentException.class, () -> newSubmitter().submit(null));
	}

	@Test
	public void submit_shouldRejectBlankBatchReference() {
		assertThrows(IllegalArgumentException.class, () -> newSubmitter().submit("  "));
	}

	@Test
	public void submit_shouldReturnCompleteResultForValidBatchReference() {
		ClaimsSubmissionResult result = newSubmitter().submit("BATCH-2026-001");
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), notNullValue());
		assertThat(result.getMessage(), notNullValue());
		assertThat(result.getClaimsBatchReference(), equalTo("BATCH-2026-001"));
	}
}
