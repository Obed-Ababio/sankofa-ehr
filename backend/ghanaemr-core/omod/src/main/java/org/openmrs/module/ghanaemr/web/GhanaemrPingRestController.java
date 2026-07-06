/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. This software is also distributed
 * under the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 */
package org.openmrs.module.ghanaemr.web;

import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.ghanaemr.GhanaemrConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Scaffold health-check endpoint: GET /ws/rest/v1/ghanaemr/ping returns the module name and
 * version. Registered the same way openmrs-module-queue registers its custom REST controllers
 * (component-scanned Spring @Controller extending BaseRestController).
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/ghanaemr")
public class GhanaemrPingRestController extends BaseRestController {

	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	@ResponseBody
	public Object ping() {
		SimpleObject ret = new SimpleObject();
		ret.add("module", GhanaemrConstants.MODULE_ID);
		ret.add("name", GhanaemrConstants.MODULE_NAME);
		ret.add("version", resolveModuleVersion());
		ret.add("status", "ok");
		return ret;
	}

	private String resolveModuleVersion() {
		Module module = ModuleFactory.getModuleById(GhanaemrConstants.MODULE_ID);
		return module == null ? "unknown" : module.getVersion();
	}

	@Override
	public String getNamespace() {
		return "v1/ghanaemr";
	}
}
