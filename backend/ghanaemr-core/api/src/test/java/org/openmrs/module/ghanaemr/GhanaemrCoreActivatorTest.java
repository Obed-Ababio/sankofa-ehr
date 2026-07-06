/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class GhanaemrCoreActivatorTest {

	@Test
	public void startedAndStopped_shouldLogWithoutError() {
		GhanaemrCoreActivator activator = new GhanaemrCoreActivator();
		assertDoesNotThrow(activator::started);
		assertDoesNotThrow(activator::stopped);
	}
}
