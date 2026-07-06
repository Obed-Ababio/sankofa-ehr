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
 * Contract every {@link SmsGateway} implementation must honor. Implementation test classes extend
 * this and supply their instance via {@link #newGateway()}; real providers (Hubtel / Africa's
 * Talking, Phase 4) reuse this class unchanged.
 */
public abstract class SmsGatewayContractTest {

	protected static final String VALID_RECIPIENT = "+233201234567";

	protected abstract SmsGateway newGateway();

	@Test
	public void send_shouldRejectNullRecipient() {
		assertThrows(IllegalArgumentException.class, () -> newGateway().send(null, "hello"));
	}

	@Test
	public void send_shouldRejectBlankRecipient() {
		assertThrows(IllegalArgumentException.class, () -> newGateway().send("  ", "hello"));
	}

	@Test
	public void send_shouldRejectNullMessage() {
		assertThrows(IllegalArgumentException.class, () -> newGateway().send(VALID_RECIPIENT, null));
	}

	@Test
	public void send_shouldRejectBlankMessage() {
		assertThrows(IllegalArgumentException.class, () -> newGateway().send(VALID_RECIPIENT, " "));
	}

	@Test
	public void send_shouldReturnCompleteResultForValidInput() {
		SmsResult result = newGateway().send(VALID_RECIPIENT, "Your appointment is tomorrow at 09:00");
		assertThat(result, notNullValue());
		assertThat(result.getStatus(), notNullValue());
		assertThat(result.getMessage(), notNullValue());
		assertThat(result.getRecipientPhone(), equalTo(VALID_RECIPIENT));
	}
}
