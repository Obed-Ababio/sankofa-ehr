/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.api.AdministrationService;

@ExtendWith(MockitoExtension.class)
public class GhanaemrConfigTest {

	@Mock
	private AdministrationService administrationService;

	private GhanaemrConfig config;

	@BeforeEach
	public void setUp() {
		config = new GhanaemrConfig(administrationService);
	}

	@Test
	public void getProperty_shouldReturnDefaultWhenGlobalPropertyUnset() {
		when(administrationService.getGlobalProperty("ghanaemr.someFlag")).thenReturn(null);
		assertThat(config.getProperty("ghanaemr.someFlag", "fallback"), equalTo("fallback"));
	}

	@Test
	public void getProperty_shouldReturnDefaultWhenGlobalPropertyBlank() {
		when(administrationService.getGlobalProperty("ghanaemr.someFlag")).thenReturn("   ");
		assertThat(config.getProperty("ghanaemr.someFlag", "fallback"), equalTo("fallback"));
	}

	@Test
	public void getProperty_shouldReturnTrimmedValueWhenSet() {
		when(administrationService.getGlobalProperty("ghanaemr.someFlag")).thenReturn("  value  ");
		assertThat(config.getProperty("ghanaemr.someFlag", "fallback"), equalTo("value"));
	}

	@Test
	public void isFlagEnabled_shouldBeFalseWhenUnset() {
		when(administrationService.getGlobalProperty("ghanaemr.someFlag")).thenReturn(null);
		assertThat(config.isFlagEnabled("ghanaemr.someFlag"), equalTo(false));
	}

	@Test
	public void isFlagEnabled_shouldBeTrueForOnAnyCase() {
		when(administrationService.getGlobalProperty("ghanaemr.someFlag")).thenReturn("On");
		assertThat(config.isFlagEnabled("ghanaemr.someFlag"), equalTo(true));
	}

	@Test
	public void isFlagEnabled_shouldBeTrueForTrue() {
		when(administrationService.getGlobalProperty("ghanaemr.someFlag")).thenReturn("true");
		assertThat(config.isFlagEnabled("ghanaemr.someFlag"), equalTo(true));
	}

	@Test
	public void isFlagEnabled_shouldBeFalseForOffOrGarbage() {
		when(administrationService.getGlobalProperty("ghanaemr.someFlag")).thenReturn("off");
		assertThat(config.isFlagEnabled("ghanaemr.someFlag"), equalTo(false));
		when(administrationService.getGlobalProperty("ghanaemr.someFlag")).thenReturn("banana");
		assertThat(config.isFlagEnabled("ghanaemr.someFlag"), equalTo(false));
	}

	@Test
	public void isGhanaCardChecksumEnabled_shouldDefaultOff() {
		when(administrationService.getGlobalProperty(GhanaemrConfig.GP_GHANACARD_CHECKSUM)).thenReturn(null);
		assertThat(config.isGhanaCardChecksumEnabled(), equalTo(false));
	}

	@Test
	public void isGhanaCardChecksumEnabled_shouldFollowGlobalProperty() {
		when(administrationService.getGlobalProperty(GhanaemrConfig.GP_GHANACARD_CHECKSUM)).thenReturn("on");
		assertThat(config.isGhanaCardChecksumEnabled(), equalTo(true));
	}
}
