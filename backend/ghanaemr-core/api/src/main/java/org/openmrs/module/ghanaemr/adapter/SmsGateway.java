/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter;

/**
 * Adapter for outbound SMS, per master plan section 8. Real providers (Hubtel / Africa's Talking —
 * both serve Ghana) are Phase 4 (WP-072); until then {@code ConsoleSmsGateway} logs instead of
 * sending.
 * <p>
 * Contract (encoded in {@code SmsGatewayContractTest}):
 * <ul>
 * <li>Null or blank recipient/message throws {@link IllegalArgumentException} — callers must
 * validate first.</li>
 * <li>For valid input, never returns {@code null}; delivery problems are reported via
 * {@link SmsResult.Status#FAILED}, not exceptions.</li>
 * </ul>
 */
public interface SmsGateway {

	/**
	 * Sends (or records) an SMS.
	 *
	 * @param recipientPhone recipient in E.164 form, e.g. +233XXXXXXXXX; must not be null/blank
	 * @param message the message text; must not be null/blank
	 * @return a non-null send result
	 * @throws IllegalArgumentException if recipientPhone or message is null or blank
	 */
	SmsResult send(String recipientPhone, String message);
}
