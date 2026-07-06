/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter;

/**
 * Adapter for NHIS claims submission, per master plan section 8. This is a Phase 4 placeholder:
 * WP-070 researches the real submission format, WP-071 implements a file-export submitter (and
 * reshapes the batch parameter into a real claims model); an API implementation slot stays
 * reserved.
 * <p>
 * Contract (encoded in {@code ClaimsSubmitterContractTest}):
 * <ul>
 * <li>Null or blank batch references throw {@link IllegalArgumentException}.</li>
 * <li>For valid input, never returns {@code null}; every result carries a status and message.</li>
 * </ul>
 */
public interface ClaimsSubmitter {

	/**
	 * Submits a claims batch identified by reference.
	 *
	 * @param claimsBatchReference identifier of the claims batch to submit; must not be null/blank
	 * @return a non-null submission result
	 * @throws IllegalArgumentException if claimsBatchReference is null or blank
	 */
	ClaimsSubmissionResult submit(String claimsBatchReference);
}
