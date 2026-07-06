/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter;

/**
 * Immutable result of a {@link ClaimsSubmitter} submission attempt.
 */
public final class ClaimsSubmissionResult {

	public enum Status {
		/** Submitted to the counterparty (API implementations, Phase 4+). */
		SUBMITTED,
		/** Exported to a file for manual upload (file-export implementation, WP-071). */
		EXPORTED,
		/** The counterparty rejected the batch. */
		REJECTED,
		/** Submission is not supported by this implementation. */
		UNSUPPORTED
	}

	private final Status status;

	private final String message;

	private final String claimsBatchReference;

	private ClaimsSubmissionResult(Status status, String message, String claimsBatchReference) {
		this.status = status;
		this.message = message;
		this.claimsBatchReference = claimsBatchReference;
	}

	public static ClaimsSubmissionResult of(Status status, String message, String claimsBatchReference) {
		return new ClaimsSubmissionResult(status, message, claimsBatchReference);
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public String getClaimsBatchReference() {
		return claimsBatchReference;
	}
}
