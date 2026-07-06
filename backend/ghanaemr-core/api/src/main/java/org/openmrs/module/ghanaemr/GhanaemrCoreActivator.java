/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr;

import org.openmrs.module.BaseModuleActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the logic that is run every time this module is either started or shutdown.
 */
public class GhanaemrCoreActivator extends BaseModuleActivator {

	private static final Logger log = LoggerFactory.getLogger(GhanaemrCoreActivator.class);

	@Override
	public void started() {
		log.info("Ghana EMR Core module started");
	}

	@Override
	public void stopped() {
		log.info("Ghana EMR Core module stopped");
	}
}
