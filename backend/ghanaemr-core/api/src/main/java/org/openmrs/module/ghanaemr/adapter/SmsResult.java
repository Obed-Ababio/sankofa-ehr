/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter;

/**
 * Immutable result of an {@link SmsGateway} send attempt.
 */
public final class SmsResult {

	public enum Status {
		/** Provider confirmed delivery hand-off. */
		SENT,
		/** Accepted by the provider for asynchronous delivery. */
		QUEUED,
		/** Not actually sent; written to the log only (console/mock gateway). */
		LOGGED,
		/** The provider rejected or failed the send. */
		FAILED
	}

	private final Status status;

	private final String message;

	private final String recipientPhone;

	private SmsResult(Status status, String message, String recipientPhone) {
		this.status = status;
		this.message = message;
		this.recipientPhone = recipientPhone;
	}

	public static SmsResult of(Status status, String message, String recipientPhone) {
		return new SmsResult(status, message, recipientPhone);
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public String getRecipientPhone() {
		return recipientPhone;
	}
}
