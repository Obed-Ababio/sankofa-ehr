/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter.mock;

import org.openmrs.module.ghanaemr.adapter.SmsGateway;
import org.openmrs.module.ghanaemr.adapter.SmsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * {@link SmsGateway} that logs messages instead of sending them. Real providers (Hubtel /
 * Africa's Talking) arrive in Phase 4 (WP-072) and must pass the same contract test.
 */
@Component("ghanaemr.consoleSmsGateway")
public class ConsoleSmsGateway implements SmsGateway {

	private static final Logger log = LoggerFactory.getLogger(ConsoleSmsGateway.class);

	@Override
	public SmsResult send(String recipientPhone, String message) {
		if (recipientPhone == null || recipientPhone.trim().isEmpty()) {
			throw new IllegalArgumentException("recipientPhone must not be null or blank");
		}
		if (message == null || message.trim().isEmpty()) {
			throw new IllegalArgumentException("message must not be null or blank");
		}
		log.info("ConsoleSmsGateway (no SMS sent) — to: {}, message: {}", recipientPhone, message);
		return SmsResult.of(SmsResult.Status.LOGGED, "Logged to console; no SMS sent", recipientPhone);
	}
}
