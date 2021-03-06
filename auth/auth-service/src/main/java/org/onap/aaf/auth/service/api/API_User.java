/**
 * ============LICENSE_START====================================================
 * org.onap.aaf
 * ===========================================================================
 * Copyright (c) 2018 AT&T Intellectual Property. All rights reserved.
 * ===========================================================================
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
 * ============LICENSE_END====================================================
 *
 */

package org.onap.aaf.auth.service.api;

import static org.onap.aaf.auth.layer.Result.OK;
import static org.onap.aaf.auth.rserv.HttpMethods.GET;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.onap.aaf.auth.env.AuthzTrans;
import org.onap.aaf.auth.layer.Result;
import org.onap.aaf.auth.service.AAF_Service;
import org.onap.aaf.auth.service.Code;
import org.onap.aaf.auth.service.facade.AuthzFacade;
import org.onap.aaf.auth.service.mapper.Mapper.API;
import org.onap.aaf.cadi.config.Config;

/**
 * User Role APIs
 * @author Jonathan
 *
 */
public class API_User {
    /**
     * Normal Init level APIs
     *
     * @param authzAPI
     * @param facade
     * @throws Exception
     */
    public static void init(final AAF_Service authzAPI, AuthzFacade facade) throws Exception {
        /**
         * get all Users who have Permission X
         */
        authzAPI.route(GET,"/authz/users/perm/:type/:instance/:action",API.USERS,new Code(facade,"Get Users By Permission", true) {
            @Override
            public void handle(AuthzTrans trans, HttpServletRequest req, HttpServletResponse resp) throws Exception {
//                trans.checkpoint(pathParam(req,"type") + " "
//                        + pathParam(req,"instance") + " "
//                        + pathParam(req,"action"));
//
                Result<Void> r = context.getUsersByPermission(trans, resp,
                        pathParam(req, ":type"),
                        URLDecoder.decode(pathParam(req, ":instance"),Config.UTF_8),
                        pathParam(req, ":action"));
                switch(r.status) {
                    case OK:
                        resp.setStatus(HttpStatus.OK_200);
                        break;
                    default:
                        context.error(trans,resp,r);
                }
            }
        });


        /**
         * get all Users who have Role X
         */
        authzAPI.route(GET,"/authz/users/role/:role",API.USERS,new Code(facade,"Get Users By Role", true) {
            @Override
            public void handle(AuthzTrans trans, HttpServletRequest req, HttpServletResponse resp) throws Exception {
                Result<Void> r = context.getUsersByRole(trans, resp, pathParam(req, ":role"));
                switch(r.status) {
                    case OK:
                        resp.setStatus(HttpStatus.OK_200);
                        break;
                    default:
                        context.error(trans,resp,r);
                }
            }
        });

        /**
         * Get User Role if exists
         * @deprecated
         */
        authzAPI.route(GET,"/authz/userRole/:user/:role",API.USERS,new Code(facade,"Get if User is In Role", true) {
            @Override
            public void handle(AuthzTrans trans, HttpServletRequest req, HttpServletResponse resp) throws Exception {
                Result<Void> r = context.getUserInRole(trans, resp, pathParam(req,":user"),pathParam(req,":role"));
                switch(r.status) {
                    case OK:
                        resp.setStatus(HttpStatus.OK_200);
                        break;
                    default:
                        context.error(trans,resp,r);
                }
            }
        });

        /**
         * Get User Role if exists
         */
        authzAPI.route(GET,"/authz/users/:user/:role",API.USERS,new Code(facade,"Get if User is In Role", true) {
            @Override
            public void handle(AuthzTrans trans, HttpServletRequest req, HttpServletResponse resp) throws Exception {
                Result<Void> r = context.getUserInRole(trans, resp, pathParam(req,":user"),pathParam(req,":role"));
                switch(r.status) {
                    case OK:
                        resp.setStatus(HttpStatus.OK_200);
                        break;
                    default:
                        context.error(trans,resp,r);
                }
            }
        });



    }

}
