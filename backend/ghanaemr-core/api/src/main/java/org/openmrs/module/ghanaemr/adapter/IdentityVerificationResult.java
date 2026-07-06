/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter;

/**
 * Immutable result of an {@link IdentityVerifier} check.
 */
public final class IdentityVerificationResult {

	public enum Status {
		/** Identity confirmed against the authoritative source (NIA). */
		VERIFIED,
		/** Format acceptable but identity not confirmed (e.g. no online verification available). */
		UNVERIFIED,
		/** Input does not match the expected PIN format; nothing was verified. */
		INVALID_FORMAT
	}

	private final Status status;

	private final String message;

	private final String source;

	private IdentityVerificationResult(Status status, String message, String source) {
		this.status = status;
		this.message = message;
		this.source = source;
	}

	public static IdentityVerificationResult of(Status status, String message, String source) {
		return new IdentityVerificationResult(status, message, source);
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	/** Which verifier produced this result (e.g. "mock", "nia"). */
	public String getSource() {
		return source;
	}
}
