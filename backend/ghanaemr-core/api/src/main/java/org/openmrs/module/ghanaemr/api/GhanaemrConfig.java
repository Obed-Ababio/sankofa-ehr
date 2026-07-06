/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.api;

import org.openmrs.api.AdministrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Central accessor for ghanaemr.* configuration flags, read from OpenMRS global properties with
 * hard-coded defaults. This is the config-flags pattern the rest of the module builds on: features
 * that are not ready (or need per-deployment toggling) hide behind a flag that defaults to "off".
 * WP-012 (Ghana Card checksum) is the first consumer.
 */
@Component("ghanaemr.GhanaemrConfig")
public class GhanaemrConfig {

	/**
	 * Flag gating Ghana Card checksum validation. Default "off": only the regex format check runs
	 * until the NIA checksum algorithm is confirmed and implemented in WP-012.
	 */
	public static final String GP_GHANACARD_CHECKSUM = "ghanaemr.ghanacard.checksum";

	public static final String GHANACARD_CHECKSUM_DEFAULT = "off";

	private final AdministrationService administrationService;

	@Autowired
	public GhanaemrConfig(@Qualifier("adminService") AdministrationService administrationService) {
		this.administrationService = administrationService;
	}

	/**
	 * Returns the value of a global property, falling back to {@code defaultValue} when the
	 * property is unset or blank.
	 */
	public String getProperty(String key, String defaultValue) {
		String value = administrationService.getGlobalProperty(key);
		if (value == null || value.trim().isEmpty()) {
			return defaultValue;
		}
		return value.trim();
	}

	/**
	 * A flag is enabled when its global property is "on" or "true" (case-insensitive). Anything
	 * else — including unset — means disabled.
	 */
	public boolean isFlagEnabled(String key) {
		String value = getProperty(key, "off");
		return "on".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
	}

	/** Whether Ghana Card checksum validation is enabled (WP-012; default off). */
	public boolean isGhanaCardChecksumEnabled() {
		return isFlagEnabled(GP_GHANACARD_CHECKSUM);
	}
}
