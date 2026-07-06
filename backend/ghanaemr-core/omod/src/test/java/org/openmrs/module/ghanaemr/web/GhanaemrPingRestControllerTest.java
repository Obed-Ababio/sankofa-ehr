/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.ghanaemr.GhanaemrConstants;
import org.openmrs.module.webservices.rest.SimpleObject;

/**
 * Unit test for the ping controller. Full REST-framework integration testing is disproportionate
 * for a scaffold; live verification ("module loads on the platform" + a real GET of
 * /ws/rest/v1/ghanaemr/ping) happens at deployment (see WP-020 progress log).
 */
public class GhanaemrPingRestControllerTest {

	private GhanaemrPingRestController controller;

	private MockedStatic<ModuleFactory> moduleFactory;

	@BeforeEach
	public void setUp() {
		controller = new GhanaemrPingRestController();
		moduleFactory = mockStatic(ModuleFactory.class);
	}

	@AfterEach
	public void tearDown() {
		moduleFactory.close();
	}

	@Test
	public void ping_shouldReturnModuleNameAndVersion() {
		Module module = mock(Module.class);
		when(module.getVersion()).thenReturn("1.0.0-SNAPSHOT");
		moduleFactory.when(() -> ModuleFactory.getModuleById(GhanaemrConstants.MODULE_ID)).thenReturn(module);

		SimpleObject result = (SimpleObject) controller.ping();

		assertThat(result.get("module"), equalTo(GhanaemrConstants.MODULE_ID));
		assertThat(result.get("name"), equalTo(GhanaemrConstants.MODULE_NAME));
		assertThat(result.get("version"), equalTo("1.0.0-SNAPSHOT"));
		assertThat(result.get("status"), equalTo("ok"));
	}

	@Test
	public void ping_shouldReportUnknownVersionWhenModuleNotRegistered() {
		moduleFactory.when(() -> ModuleFactory.getModuleById(GhanaemrConstants.MODULE_ID)).thenReturn(null);

		SimpleObject result = (SimpleObject) controller.ping();

		assertThat(result.get("version"), equalTo("unknown"));
		assertThat(result.get("status"), equalTo("ok"));
	}

	@Test
	public void getNamespace_shouldBeGhanaemrV1() {
		assertThat(controller.getNamespace(), equalTo("v1/ghanaemr"));
	}
}
