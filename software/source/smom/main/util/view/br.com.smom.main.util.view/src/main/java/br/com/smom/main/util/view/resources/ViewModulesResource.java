/*
 * Copyright 2015 Smom - Software Module Management.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.smom.main.util.view.resources;

import br.com.smom.log.api.services.Log;
import br.com.smom.main.util.api.enums.Messages;
import br.com.smom.main.util.api.exceptions.UtilException;
import br.com.smom.main.util.api.model.to.ViewModuleListTO;
import br.com.smom.main.util.api.services.ServiceProvider;
import br.com.smom.main.util.api.services.ViewModules;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * http://localhost:8080/modules/main/util/resources/viewmodules
 *
 */
@Path("viewmodule")
public class ViewModulesResource {

    private final ViewModules viewModulesService = (ViewModules) ServiceProvider.getBundleService(ViewModules.class);
    private final Log logService = (Log) ServiceProvider.getBundleService(Log.class);

    @Path("/list/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ViewModuleListTO viewModulesListAll() {

        ViewModuleListTO viewModuleListTO = new ViewModuleListTO();

        try {
            if (logService != null) {
                logService.info(Messages.INFO_INITIALIZED_REQUEST_REST.getDescription() + " /modules/main/util/resources/viewmodule/list/all");
            }

            if (viewModulesService != null) {
                viewModuleListTO.setViewModuleEntityList(viewModulesService.getViewModuleListAll());
            }
        } catch (UtilException e) {
            if (logService != null) {
                logService.error(Messages.ERROR.toString(), e);
            }
        }

        logService.info(Messages.INFO_FINISH_REQUEST_REST.getDescription() + " /modules/main/util/resources/viewmodule/list/all");
        return viewModuleListTO;
    }

}
