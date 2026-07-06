/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.adapter.mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.openmrs.module.ghanaemr.adapter.SmsGateway;
import org.openmrs.module.ghanaemr.adapter.SmsGatewayContractTest;
import org.openmrs.module.ghanaemr.adapter.SmsResult;

public class ConsoleSmsGatewayTest extends SmsGatewayContractTest {

	@Override
	protected SmsGateway newGateway() {
		return new ConsoleSmsGateway();
	}

	@Test
	public void send_shouldOnlyLogAndReportLoggedStatus() {
		SmsResult result = newGateway().send(VALID_RECIPIENT, "test message");
		assertThat(result.getStatus(), equalTo(SmsResult.Status.LOGGED));
	}
}
