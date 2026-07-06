/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter;

/**
 * Immutable result of an {@link InsuranceEligibility} check.
 */
public final class EligibilityResult {

	public enum Status {
		/** Membership confirmed active by the authoritative source (NHIA). */
		ELIGIBLE,
		/** Membership confirmed inactive/expired by the authoritative source. */
		INELIGIBLE,
		/** Could not be confirmed either way; capture evidence manually. */
		UNVERIFIED,
		/** Input was unusable (null/blank/malformed); no check was attempted. */
		INVALID
	}

	private final Status status;

	private final String message;

	private final String source;

	private EligibilityResult(Status status, String message, String source) {
		this.status = status;
		this.message = message;
		this.source = source;
	}

	public static EligibilityResult of(Status status, String message, String source) {
		return new EligibilityResult(status, message, source);
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	/** Which eligibility checker produced this result (e.g. "mock", "nhia"). */
	public String getSource() {
		return source;
	}
}
